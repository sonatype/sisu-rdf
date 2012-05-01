package org.sonatype.sisu.rdf.maven.internal.sp;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class FileUtilsTest
{
    @Test
    public void readFile() throws IOException
    {
        String content = FileUtils.fileRead( new File("target/test-classes/someFile.txt") );
        assertThat( "File content", content, is( equalTo( "Some text" ) ) );
    }
}
