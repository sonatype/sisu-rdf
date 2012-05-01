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

package org.sonatype.sisu.rdf.maven.query;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.sonatype.sisu.rdf.Builder;
import org.sonatype.sisu.rdf.maven.MAVEN;
import org.sonatype.sisu.rdf.query.grammar.Bound;
import org.sonatype.sisu.rdf.query.grammar.Criteria;
import org.sonatype.sisu.rdf.query.grammar.Equal;
import org.sonatype.sisu.rdf.query.grammar.Join;

import static org.sonatype.sisu.rdf.query.QueryBuilder.*;

/**
 * Maven specific query grammar builder.
 *
 * @author Alin Dreghiciu
 */
public class MavenQueryBuilder
{

    private static final ArtifactCriterias ARTIFACT_CRITERIAS = new ArtifactCriterias();

    public static ArtifactCriterias artifactsWith()
    {
        return ARTIFACT_CRITERIAS;
    }

    public static ArtifactCriterias artifactsHaving()
    {
        return ARTIFACT_CRITERIAS;
    }

    public static ArtifactCriterias artifactsContaining()
    {
        return ARTIFACT_CRITERIAS;
    }

    public static Equal<URI> artifactType()
    {
        return eq( RDF.TYPE, MAVEN.ARTIFACT_TYPE );
    }

    public static class ArtifactCriterias
    {

        public Equal<Value> groupId( final String value )
        {
            return eq( MAVEN.GROUP_ID, Builder.literal( value ) );
        }

        public Equal<Value> artifactId( final String value )
        {
            return eq( MAVEN.ARTIFACT_ID, Builder.literal( value ) );
        }

        public Equal<Value> version( final String value )
        {
            return eq( MAVEN.VERSION, Builder.literal( value ) );
        }

        public Equal<Value> extension( final String value )
        {
            return eq( MAVEN.EXTENSION, Builder.literal( value ) );
        }

        public Equal<Value> classifier( final String value )
        {
            return eq( MAVEN.CLASSIFIER, Builder.literal( value ) );
        }

        public Criteria coordinates( final String coordinates )
        {
            assert coordinates != null : "Artifact coordinates must be specified (cannot be null)";
            assert coordinates.trim().length() > 0 : "Artifact coordinates must be specified (cannot be empty)";

            Criteria cond = null;
            final String[] segments = coordinates.split( ":" );
            if ( segments.length >= 1 )
            {
                cond = groupId( segments[ 0 ] );
            }
            if ( segments.length >= 2 && segments[ 1 ].trim().length() == 0 )
            {
                cond =
                    cond == null ? artifactId( segments[ 1 ] ) : and( cond, artifactId( segments[ 1 ] ) );
            }
            if ( segments.length >= 3 && segments[ 2 ].trim().length() == 0 )
            {
                cond = cond == null ? version( segments[ 2 ] ) : and( cond, version( segments[ 2 ] ) );
            }
            if ( segments.length >= 4 && segments[ 3 ].trim().length() == 0 )
            {
                cond = cond == null ? extension( segments[ 3 ] ) : and( cond, extension( segments[ 3 ] ) );
            }
            if ( segments.length >= 5 && segments[ 4 ].trim().length() == 0 )
            {
                cond =
                    cond == null ? classifier( segments[ 4 ] ) : and( cond, classifier( segments[ 4 ] ) );
            }
            return cond;
        }

        public Criteria coordinates( final String groupId,
                                      final String artifactId )
        {
            return and(
                groupId( groupId ), artifactId( artifactId )
            );
        }

        public Criteria coordinates( final String groupId,
                                      final String artifactId,
                                      final String version )
        {
            return and(
                groupId( groupId ), artifactId( artifactId ), version( version )
            );
        }

        public Criteria coordinates( final String groupId,
                                      final String artifactId,
                                      final String version,
                                      final String extension )
        {
            return and(
                groupId( groupId ), artifactId( artifactId ), version( version ),
                extension( extension )
            );
        }

        public Criteria coordinates( final String groupId,
                                      final String artifactId,
                                      final String version,
                                      final String extension,
                                      final String classifier )
        {
            return and(
                groupId( groupId ), artifactId( artifactId ), version( version ),
                extension( extension ), classifier( classifier )
            );
        }

        public Join dependencyOn( final Criteria criteria )
        {
            return join( MAVEN.DEPENDS, criteria );
        }

        public Join parent( final Criteria criteria )
        {
            return join( MAVEN.PARENT, criteria );
        }

        public Bound groupId()
        {
            return has( MAVEN.GROUP_ID );
        }

        public Bound artifactId()
        {
            return has( MAVEN.ARTIFACT_ID );
        }

        public Bound version()
        {
            return has( MAVEN.VERSION );
        }

        public Bound extension()
        {
            return has( MAVEN.EXTENSION );
        }

        public Bound classifier()
        {
            return has( MAVEN.CLASSIFIER );
        }


    }
}