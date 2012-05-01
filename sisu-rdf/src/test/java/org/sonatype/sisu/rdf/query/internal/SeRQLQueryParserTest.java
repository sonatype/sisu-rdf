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
package org.sonatype.sisu.rdf.query.internal;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.sonatype.sisu.rdf.Builder.*;

import org.junit.Test;
import org.openrdf.query.QueryLanguage;
import org.sonatype.sisu.rdf.query.QueryBuilder;
import org.sonatype.sisu.rdf.query.internal.SeRQLQueryParser;

/**
 * {@link SeRQLQueryParser} related unit tests.
 * 
 * @author Alin Dreghiciu
 */
public class SeRQLQueryParserTest
{

    @Test
    public void language()
    {
        final SeRQLQueryParser underTest = new SeRQLQueryParser();

        assertThat( "Query Language", underTest.language(), is( equalTo( QueryLanguage.SERQL ) ) );
    }

    @Test
    public void parseNullCondition()
    {
        final SeRQLQueryParser underTest = new SeRQLQueryParser();

        assertNotNull( "Parsed query is not null", underTest.parse( null ) );
    }

    @Test
    public void parseNonNullCondition()
    {
        final SeRQLQueryParser underTest = new SeRQLQueryParser();

        assertNotNull( "Parsed query is not null", underTest.parse( QueryBuilder.about( resource( "urn:test" ) ) ) );
    }

}