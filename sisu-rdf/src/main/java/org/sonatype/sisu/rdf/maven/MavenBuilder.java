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

import static org.sonatype.sisu.rdf.Builder.*;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;


/**
 * @author Alin Dreghiciu
 */
public class MavenBuilder
{

    /**
     * Private constructor.
     */
    private MavenBuilder()
    {
        // utility class
    }
    
    public static URI mavenPredicate( final String localName )
    {
        return predicate( MAVEN.NAMESPACE, localName );
    }
    
    public static URI mavenType( final String type )
    {
        return resourceType( MAVEN.NAMESPACE, type );
    }
    
    public static Resource mavenResource( String id )
    {
        return resource( MAVEN.URI_NAMESPACE , id );
    }

    public static URI mavenProperty( final String propertyName )
    {
        return predicate( MAVEN.NAMESPACE_PROPERTY, propertyName );
    }


}