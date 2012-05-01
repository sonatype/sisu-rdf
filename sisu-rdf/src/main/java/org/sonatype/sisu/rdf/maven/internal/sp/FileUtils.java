package org.sonatype.sisu.rdf.maven.internal.sp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.codehaus.plexus.util.IOUtil;

public class FileUtils
{

    private static Charset charset = Charset.forName( "UTF-8" );

    private static CharsetDecoder decoder = charset.newDecoder();

    public static String fileRead( File file )
        throws IOException
    {
        FileInputStream is = null;
        try
        {
            is = new FileInputStream( file );
            FileChannel ch = is.getChannel();
            MappedByteBuffer mb = ch.map( MapMode.READ_ONLY, 0L, ch.size() );
            CharBuffer charBuffer = decoder.decode( mb );
            return charBuffer.toString();
        }
        finally
        {
            IOUtil.close( is );
        }
    }

}
