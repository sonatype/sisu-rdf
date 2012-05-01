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

import static org.sonatype.sisu.rdf.maven.MavenBuilder.*;

import org.openrdf.model.URI;

/**
 * Maven vocabulary constants.
 * 
 * @author Alin Dreghiciu
 */
public interface MAVEN
{

    /**
     * Ontology namespace.
     */
    static final String NAMESPACE = "http://maven.apache.org/artifact#";

    /**
     * Artifact identity namespace.
     */
    static final String URI_NAMESPACE = "maven:/";

    /**
     * Artifact type.
     */
    static final URI ARTIFACT_TYPE = mavenType( "Artifact" );

    static final URI PROJECT_TYPE = mavenType( "Project" );

    static final URI PROJECT_VERSION_TYPE = mavenType( "ProjectVersion" );

    static final URI DEPENDENCY_TYPE = mavenType( "Dependency" );

    static final URI PROJECT = mavenPredicate( "project" );

    static final URI PROJECT_VERSION = mavenPredicate( "projectVersion" );

    static final URI ARTIFACT = mavenPredicate( "artifact" );

    static final URI DEPENDS = mavenPredicate( "depends" );

    static final URI DEPENDENCY = mavenPredicate( "dependency" );

    static final URI USED_BY = mavenPredicate( "usedBy" );

    static final URI TYPE = mavenPredicate( "type" );

    static final URI SCOPE = mavenPredicate( "scope" );

    /**
     * Group id predicate.
     */
    static final URI GROUP_ID = mavenPredicate( "groupId" );

    /**
     * Artifact id predicate.
     */
    static final URI ARTIFACT_ID = mavenPredicate( "artifactId" );

    /**
     * Version predicate.
     */
    static final URI VERSION = mavenPredicate( "version" );

    /**
     * Base version predicate.
     */
    static final URI BASE_VERSION = mavenPredicate( "baseVersion" );

    /**
     * Classifier predicate.
     */
    static final URI CLASSIFIER = mavenPredicate( "classifier" );

    /**
     * Extension predicate.
     */
    static final URI EXTENSION = mavenPredicate( "extension" );

    /**
     * Path predicate.
     */
    static final URI PATH = mavenPredicate( "path" );

    /**
     * MD5 checksum predicate.
     */
    static final URI MD5_CHECKSUM = mavenPredicate( "md5" );

    /**
     * SHA1 checksum predicate.
     */
    static final URI SHA1_CHECKSUM = mavenPredicate( "sha1" );

    /**
     * GPG checksum predicate.
     */
    static final URI GPG_SIGNATURE = mavenPredicate( "gpg" );

    /**
     * Artifact parent predicate.
     */
    static final URI PARENT = mavenPredicate( "parent" );

    /**
     * Artifact dependencies predicate.
     */
    static final URI DEPENDENCIES = mavenPredicate( "dependencies" );

    /**
     * Artifact name predicate.
     */
    static final URI NAME = mavenPredicate( "name" );

    /**
     * Artifact description predicate.
     */
    static final URI DESCRIPTION = mavenPredicate( "description" );

    /**
     * Artifact url predicate.
     */
    static final URI URL = mavenPredicate( "url" );

    /**
     * Artifact packaging predicate.
     */
    static final URI PACKAGING = mavenPredicate( "packaging" );

    /**
     * Artifact license predicate.
     */
    static final URI LICENSE = mavenPredicate( "license" );
}