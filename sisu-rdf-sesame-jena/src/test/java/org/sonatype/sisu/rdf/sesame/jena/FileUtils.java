package org.sonatype.sisu.rdf.sesame.jena;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class FileUtils
{

    public static String readFileContent( File file )
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

    public static String readFileContentFromClasspath( String path )
    {
        InputStream in = null;
        try
        {
            in = FileUtils.class.getClassLoader().getResourceAsStream( path );
            if ( in == null )
            {
                throw new RuntimeException( "Path " + path + " not found on classpath" );
            }
            return readFileContent( in );
        }
        finally
        {
            if ( in != null )
            {
                try
                {
                    in.close();
                }
                catch ( IOException e )
                {
                    throw new RuntimeException( e );
                }
            }
        }
    }

    public static String readFileContent( InputStream is )
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

    public static String extension( String name )
    {
        int dotIdx = name.lastIndexOf( "." );
        if ( dotIdx > 0 && dotIdx < name.length() - 1 )
        {
            return name.substring( dotIdx + 1 ).trim();
        }
        return null;
    }

    public static File safeSearchClasspath( String name )
    {
        URL resource = FileUtils.class.getClassLoader().getResource( name );
        if ( resource != null )
        {
            String resourceFile = resource.getFile();
            if ( resourceFile != null )
            {
                return new File( resourceFile );
            }
        }
        throw new RuntimeException( new FileNotFoundException( name ) );
    }

}
