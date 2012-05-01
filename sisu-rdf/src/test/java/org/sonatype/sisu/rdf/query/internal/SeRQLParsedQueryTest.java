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
import org.junit.Test;
import org.sonatype.sisu.rdf.query.grammar.Criteria;
import org.sonatype.sisu.rdf.query.internal.SeRQLParsedQuery;

import static org.mockito.Mockito.*;
import static org.sonatype.sisu.rdf.Builder.*;
import static org.sonatype.sisu.rdf.query.QueryBuilder.*;

/**
 * {@link SeRQLParsedQuery} related unit tests.
 * 
 * @author Alin Dreghiciu
 */
public class SeRQLParsedQueryTest
{

    /**
     * Verify SeQL query generated for a null condition.
     */
    @Test
    public void parseNull()
    {
        parse(
            null,
            "CONSTRUCT DISTINCT {s} p {v}\n"
                + "  FROM\n{s} p {v}" );
    }

    /**
     * Verify SeQL query generated for an {@link About} condition.
     */
    @Test
    public void parseAbout()
    {
        parse(
            about( resource( "urn:about#1" ) ),
            "CONSTRUCT DISTINCT {s} p {v}\n"
                + "  FROM\n"
                + "{s} p {v}\n"
                + "  WHERE s = <urn:about#1>" );
    }

    /**
     * Verify SeQL query generated for an all condition.
     */
    @Test
    public void parseAll()
    {
        parse(
            all(),
            "CONSTRUCT DISTINCT {s} p {v}\n"
                + "  FROM\n"
                + "{s} p {v}" );
    }

    /**
     * Verify SeQL query generated for a {@link Bound} condition.
     */
    @Test
    public void parseBound()
    {
        parse(
            has( predicate( "urn:maven#groupId" ) ),
            "CONSTRUCT DISTINCT {s} p {v}\n"
                + "  FROM\n"
                + "[{s} ns0:groupId {v0}],\n"
                + "{s} p {v}\n"
                + "  WHERE BOUND (v0)\n  USING NAMESPACE\n"
                + "ns0 = <urn:maven#>" );
    }

    /**
     * Verify SeQL query generated for a {@link Conjunction} condition.
     */
    @Test
    public void parseConjunction()
    {
        parse(
            and(
                about( resource( "urn:about#1" ) ),
                has( predicate( "urn:maven#groupId" ) )
            ),
            "CONSTRUCT DISTINCT {s} p {v}\n"
                + "  FROM\n"
                + "[{s} ns0:groupId {v0}],\n"
                + "{s} p {v}\n"
                + "  WHERE (s = <urn:about#1>) AND (BOUND (v0))\n"
                + "  USING NAMESPACE\n"
                + "ns0 = <urn:maven#>" );
    }

    /**
     * Verify SeQL query generated for a {@link Disjunction} condition.
     */
    @Test
    public void parseDisjunction()
    {
        parse(
            or(
                about( resource( "urn:about#1" ) ),
                has( predicate( "urn:maven#groupId" ) )
            ),
            "CONSTRUCT DISTINCT {s} p {v}\n"
                + "  FROM\n"
                + "[{s} ns0:groupId {v0}],\n"
                + "{s} p {v}\n"
                + "  WHERE (s = <urn:about#1>) OR (BOUND (v0))\n"
                + "  USING NAMESPACE\n"
                + "ns0 = <urn:maven#>" );
    }

    /**
     * Verify SeQL query generated for a {@link Equal} condition.
     */
    @Test
    public void parseEqual()
    {
        parse(
            eq( predicate( "urn:maven#groupId" ), literal( "org.sonatype" ) ),
            "CONSTRUCT DISTINCT {s} p {v}\n"
                + "  FROM\n"
                + "{s} ns0:groupId {v0},\n"
                + "{s} p {v}\n"
                + "  WHERE v0 = \"org.sonatype\"\n"
                + "  USING NAMESPACE\n"
                + "ns0 = <urn:maven#>" );
    }

    /**
     * Verify SeQL query generated for a {@link Equal} condition on a resource.
     */
    @Test
    public void parseEqualResource()
    {
        parse(
            eq( predicate( "urn:maven#parent" ), resource( "urn:about#1" ) ),
            "CONSTRUCT DISTINCT {s} p {v}\n"
                + "  FROM\n"
                + "{s} ns0:parent {v0},\n"
                + "{s} p {v}\n"
                + "  WHERE v0 = <urn:about#1>\n"
                + "  USING NAMESPACE\n"
                + "ns0 = <urn:maven#>" );
    }

    /**
     * Verify SeQL query generated for a {@link Join} condition.
     */
    @Test
    public void parseJoin()
    {
        parse(
            join( predicate( "urn:maven#parent" ), eq( predicate( "urn:maven#groupId" ), literal( "org.sonatype" ) ) ),
            "CONSTRUCT DISTINCT {s} p {v}\n"
                + "  FROM\n"
                + "{s} ns0:parent {v0},\n"
                + "{v0} ns0:groupId {v1},\n"
                + "{s} p {v}\n"
                + "  WHERE v1 = \"org.sonatype\"\n"
                + "  USING NAMESPACE\n"
                + "ns0 = <urn:maven#>" );
    }

    /**
     * Verify SeQL query generated for a {@link Matches} condition.
     */
    @Test
    public void parseMatches()
    {
        parse(
            matches( predicate( "urn:maven#groupId" ), literal( "org.sonatype.**" ) ),
            "CONSTRUCT DISTINCT {s} p {v}\n"
                + "  FROM\n"
                + "{s} ns0:groupId {v0},\n"
                + "{s} p {v}\n"
                + "  WHERE v0 LIKE \"org.sonatype.**\"\n"
                + "  USING NAMESPACE\n"
                + "ns0 = <urn:maven#>" );
    }

    /**
     * Verify SeQL query generated for a {@link Negation} condition.
     */
    @Test
    public void parseNegation()
    {
        parse(
            not( matches( predicate( "urn:maven#groupId" ), literal( "org.sonatype.**" ) ) ),
            "CONSTRUCT DISTINCT {s} p {v}\n"
                + "  FROM\n"
                + "{s} ns0:groupId {v0},\n"
                + "{s} p {v}\n"
                + "  WHERE NOT (v0 LIKE \"org.sonatype.**\")\n"
                + "  USING NAMESPACE\n"
                + "ns0 = <urn:maven#>" );
    }

    /**
     * Verify SeQL query generated for a {@link Negation} condition over an {@link About} condition.
     */
    @Test
    public void parseAboutNegation()
    {
        parse(
            not( about( resource( "urn:about#1" ) ) ),
            "CONSTRUCT DISTINCT {s} p {v}\n"
                + "  FROM\n"
                + "{s} p {v}\n"
                + "  WHERE s != <urn:about#1>" );
    }

    /**
     * Verify SeQL query generated for a {@link Negation} condition over an {@link Equal} condition.
     */
    @Test
    public void parseEqualNegation()
    {
        parse(
            not( eq( predicate( "urn:maven#groupId" ), literal( "org.sonatype" ) ) ),
            "CONSTRUCT DISTINCT {s} p {v}\n"
                + "  FROM\n"
                + "{s} ns0:groupId {v0},\n"
                + "{s} p {v}\n"
                + "  WHERE v0 != \"org.sonatype\"\n"
                + "  USING NAMESPACE\n"
                + "ns0 = <urn:maven#>" );
    }

    /**
     * Verify SeQL query generated for a {@link Negation} condition over an {@link Equal} condition.
     */
    @Test
    public void parseEqualResourceNegation()
    {
        parse(
            not( eq( predicate( "urn:maven#parent" ), resource( "urn:about#1" ) ) ),
            "CONSTRUCT DISTINCT {s} p {v}\n"
                + "  FROM\n"
                + "{s} ns0:parent {v0},\n"
                + "{s} p {v}\n"
                + "  WHERE v0 != <urn:about#1>\n"
                + "  USING NAMESPACE\n"
                + "ns0 = <urn:maven#>" );
    }

    /**
     * On an unknown (unsupported) condition and UnsupportedOperationException should be thrown.
     */
    @Test( expected = UnsupportedOperationException.class )
    public void parseUnknownCondition()
    {
        final SeRQLParsedQuery underTest = new SeRQLParsedQuery( mock( Criteria.class ) );

        underTest.query();
    }

    public void parse( final Criteria criteria,
                       final String expected )
    {
        final SeRQLParsedQuery underTest = new SeRQLParsedQuery( criteria );

        assertThat( "Parsed", underTest.query(), is( equalTo( expected ) ) );
    }

}