/*
 * Copyright (c) 2009 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */

package org.sonatype.sisu.rdf.maven;

import static org.sonatype.sisu.rdf.maven.query.MavenQueryBuilder.*;
import static org.sonatype.sisu.rdf.query.QueryBuilder.*;

import org.junit.Test;

/**s
 * @author Alin Dreghiciu
 */
public class MavenQueryBuilderTest
{

    @Test
    public void usage1()
    {
        artifactsWith().groupId( "org.sonatype" );
    }

    @Test
    public void usage2()
    {
        artifactsWith().artifactId( "aether-api" );
    }

    @Test
    public void usage3()
    {
        artifactsWith().version( "1.0" );
    }

    @Test
    public void usage5()
    {
        and( artifactsWith().groupId( "org.sonatype" ), artifactsWith().artifactId( "aether-api" ) );
    }

    @Test
    public void usage6()
    {
        artifactsWith().coordinates( "org.sonatype" );
    }

    @Test
    public void usage7()
    {
        artifactsWith().coordinates( "org.sonatype:aether-api" );
    }

    @Test
    public void usage8()
    {
        artifactsWith().coordinates( "org.sonatype:aether:1.11" );
    }

    @Test
    public void usage9()
    {
        artifactsWith().coordinates( "org.sonatype", "aether-api" ) ;
    }

    @Test
    public void usage10()
    {
        artifactsWith().coordinates( "org.sonatype", "aether-api", "1.11" );
    }

    @Test
    public void usage11()
    {
        artifactsHaving().dependencyOn( artifactsWith().groupId( "org.sonatype" ) );
    }

    @Test
    public void usage12()
    {
        artifactsHaving().parent( artifactsWith().groupId( "org.sonatype" ) );
    }

}