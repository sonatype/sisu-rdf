package org.sonatype.sisu.rdf.query.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.openrdf.query.QueryLanguage;

public class QueryFile
{

    private final String query;

    private final QueryLanguage queryLanguage;

    private QueryFile( String query, QueryLanguage queryLanguage )
    {
        this.query = query;
        this.queryLanguage = queryLanguage;
    }

    public String query()
    {
        return query;
    }

    public QueryLanguage queryLanguage()
    {
        return queryLanguage;
    }

    public static QueryFile fromFile( File source )
    {
        return new QueryFile( readFileContent( source ), QueryLanguage.valueOf( extension( source.getName() ) ) );
    }

    public static QueryFile fromClasspath( String path )
    {
        return new QueryFile( readFileContent( QueryFile.class.getClassLoader().getResourceAsStream( path ) ),
            QueryLanguage.valueOf( extension( path ) ) );
    }

    private static String extension( String name )
    {
        int dotIdx = name.lastIndexOf( "." );
        if ( dotIdx > 0 && dotIdx < name.length() - 1 )
        {
            return name.substring( dotIdx + 1 ).trim();
        }
        return null;
    }

    private static String readFileContent( File file )
    {
        try
        {
            return readFileContent( new FileInputStream( file ) );
        }
        catch ( FileNotFoundException e )
        {
            throw new RuntimeException( e );
        }
    }

    private static String readFileContent( InputStream is )
    {
        StringBuilder content = new StringBuilder();
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader( new InputStreamReader( is ) );
            String line;
            while ( ( line = reader.readLine() ) != null )
            {
                if ( !line.trim().startsWith( "#" ) )
                {
                    content.append( line ).append( "\n" );
                }
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
        finally
        {
            if ( reader != null )
            {
                try
                {
                    reader.close();
                }
                catch ( IOException e )
                {
                    throw new RuntimeException( e );
                }
            }
        }
        return content.toString();
    }

}
