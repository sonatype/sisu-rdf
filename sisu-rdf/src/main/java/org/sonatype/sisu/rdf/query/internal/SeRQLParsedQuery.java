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

import static java.lang.String.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.sonatype.sisu.rdf.query.grammar.About;
import org.sonatype.sisu.rdf.query.grammar.Bound;
import org.sonatype.sisu.rdf.query.grammar.Conjunction;
import org.sonatype.sisu.rdf.query.grammar.Criteria;
import org.sonatype.sisu.rdf.query.grammar.Disjunction;
import org.sonatype.sisu.rdf.query.grammar.Equal;
import org.sonatype.sisu.rdf.query.grammar.Join;
import org.sonatype.sisu.rdf.query.grammar.Matches;
import org.sonatype.sisu.rdf.query.grammar.Negation;

/**
 * A SeRQL parsed query from a criteria.
 *
 * @author Alin Dreghiciu
 */
class SeRQLParsedQuery
{

    /**
     * Criteria to be parsed.
     */
    private final Criteria criteria;
    /**
     * Parsed query. Lazy.
     */
    private String query;

    /**
     * Constructor.
     *
     * @param criteria searcher criteria to be parsed
     */
    SeRQLParsedQuery( final Criteria criteria )
    {
        this.criteria = criteria;
    }

    /**
     * Parses and returns the query.
     *
     * @return parsed query
     */
    String query()
    {
        if ( query != null )
        {
            return query;
        }

        final Namespaces namespaces = new Namespaces();
        final Paths paths = new Paths( namespaces );
        final String where = process( "s", criteria, paths );
        final StringBuilder query = new StringBuilder()
            .append( "CONSTRUCT DISTINCT {s} p {v}" )
            .append( paths.queryPart() );
        if ( where != null && where.length() > 0 )
        {
            query.append( "\n  WHERE " ).append( where );
        }
        query.append( namespaces.queryPart() );

        this.query = query.toString();
        return this.query;
    }

    /**
     * Converts a criteria on its SeRQL string form.
     *
     * @param from      subject
     * @param criteria criteria to convert
     * @param paths     current paths
     *
     * @return SeRQL string from of criteria
     */
    private String process( final String from,
                            final Criteria criteria,
                            final Paths paths )
    {
        if ( criteria == null )
        {
            return null;
        }

        if ( criteria instanceof About )
        {
            return processAbout( (About) criteria );
        }

        if ( criteria instanceof Bound )
        {
            return processBound( from, (Bound) criteria, paths );
        }

        if ( criteria instanceof Conjunction )
        {
            return processConjunction( from, (Conjunction) criteria, paths );
        }

        if ( criteria instanceof Disjunction )
        {
            return processDisjunction( from, (Disjunction) criteria, paths );
        }

        if ( criteria instanceof Equal )
        {
            return processEqual( from, (Equal<?>) criteria, paths );
        }

        if ( criteria instanceof Join )
        {
            return processJoin( from, (Join) criteria, paths );
        }

        if ( criteria instanceof Matches )
        {
            return processMatches( from, (Matches<?>) criteria, paths );
        }

        if ( criteria instanceof Negation )
        {
            return processNegation( from, (Negation) criteria, paths );
        }

        throw new UnsupportedOperationException(
            format( "Unsupported criteria type: %s", criteria.getClass().getName() )
        );
    }

    /**
     * Converts a bound criteria on its SeRQL string form.
     *
     * @param from  subject
     * @param bound criteria to convert
     * @param paths current paths
     *
     * @return SeRQL string from of criteria
     */
    private String processBound( final String from,
                                 final Bound bound,
                                 final Paths paths )
    {
        return format( "BOUND (%s)", paths.getOptional( "s", bound.predicate() ) );
    }

    /**
     * Converts a negation criteria on its SeRQL string form.
     *
     * @param from     subject
     * @param negation criteria to convert
     * @param paths    current paths
     *
     * @return SeRQL string from of criteria
     */
    private String processNegation( final String from,
                                    final Negation negation,
                                    final Paths paths )
    {
        if ( negation.criteria() instanceof Equal )
        {
            return processNotEqual( from, (Equal<?>) negation.criteria(), paths );
        }

        if ( negation.criteria() instanceof About )
        {
            return processNotAbout( (About) negation.criteria() );
        }

        return format( "NOT (%s)", process( from, negation.criteria(), paths ) );
    }

    /**
     * Converts an about criteria on its SeRQL string form.
     *
     * @param about criteria to convert
     *
     * @return SeRQL string from of criteria
     */
    private String processAbout( final About about )
    {
        return format( "s = <%s>", about.subject().stringValue() );
    }

    /**
     * Converts an not about criteria on its SeRQL string form.
     *
     * @param notAbout criteria to convert
     *
     * @return SeRQL string from of criteria
     */
    private String processNotAbout( final About notAbout )
    {
        return format( "s != <%s>", notAbout.subject().stringValue() );
    }

    /**
     * Converts an equals criteria on its SeRQL string form.
     *
     * @param from  subject
     * @param equal criteria to convert
     * @param paths current paths
     *
     * @return SeRQL string from of criteria
     */
    private String processEqual( final String from,
                                 final Equal<? extends Value> equal,
                                 final Paths paths )
    {
        if ( equal.value() instanceof Resource )
        {
            return format( "%s = <%s>", paths.get( from, equal.predicate() ), equal.value().stringValue() );
        }

        return format( "%s = \"%s\"", paths.get( from, equal.predicate() ), equal.value().stringValue() );
    }

    /**
     * Converts an not equals criteria on its SeRQL string form.
     *
     * @param from     subject
     * @param notEqual criteria to convert
     * @param paths    current paths
     *
     * @return SeRQL string from of criteria
     */
    private String processNotEqual( final String from,
                                    final Equal<? extends Value> notEqual,
                                    final Paths paths )
    {
        if ( notEqual.value() instanceof Resource )
        {
            return format( "%s != <%s>", paths.get( from, notEqual.predicate() ), notEqual.value().stringValue() );
        }

        return format( "%s != \"%s\"", paths.get( from, notEqual.predicate() ), notEqual.value().stringValue() );
    }

    /**
     * Converts a matches criteria on its SeRQL string form.
     *
     * @param from    subject
     * @param matches criteria to convert
     * @param paths   current paths
     *
     * @return SeRQL string from of criteria
     */
    private String processMatches( final String from,
                                   final Matches<? extends Value> matches,
                                   final Paths paths )
    {
        return format( "%s LIKE \"%s\"", paths.get( from, matches.predicate() ), matches.value().stringValue() );
    }

    /**
     * Converts a conjunction criteria on its SeRQL string form.
     *
     * @param from        subject
     * @param conjunction criteria to convert
     * @param paths       current paths
     *
     * @return SeRQL string from of criteria
     */
    private String processConjunction( final String from,
                                       final Conjunction conjunction,
                                       final Paths paths )
    {
        return format(
            "(%s) AND (%s)",
            process( from, conjunction.left(), paths ),
            process( from, conjunction.right(), paths )
        );
    }

    /**
     * Converts a disjunction criteria on its SeRQL string form.
     *
     * @param from        subject
     * @param disjunction criteria to convert
     * @param paths       current paths
     *
     * @return SeRQL string from of criteria
     */
    private String processDisjunction( final String from,
                                       final Disjunction disjunction,
                                       final Paths paths )
    {
        return format(
            "(%s) OR (%s)",
            process( from, disjunction.left(), paths ),
            process( from, disjunction.right(), paths )
        );
    }

    /**
     * Converts a join criteria on its SeRQL string form.
     *
     * @param from  subject
     * @param join  criteria to convert
     * @param paths current paths
     *
     * @return SeRQL string from of criteria
     */
    private String processJoin( final String from,
                                final Join join,
                                final Paths paths )
    {
        return process( paths.get( from, join.predicate() ), join.criteria(), paths );
    }

    /**
     * Nodes traversal paths.
     */
    private static class Paths
    {

        /**
         * Namespaces.
         */
        private final Namespaces namespaces;
        /**
         * Map between subject / predicate and path name.
         */
        private final Map<String, String> paths;
        /**
         * Mandatory paths.
         */
        private final Set<String> mandatory;
        /**
         * Counter used for automatically namespace of an uri generation.
         * Increased on each path creation.
         */
        private int counter;

        /**
         * Constructor.
         *
         * @param namespaces namespaces
         */
        Paths( final Namespaces namespaces )
        {
            this.namespaces = namespaces;
            paths = new TreeMap<String, String>();
            mandatory = new HashSet<String>();
            counter = 0;
        }

        /**
         * Returns the path name for a subject / predicate.
         *
         * @param from      path subject
         * @param predicate path predicate
         *
         * @return path name
         */
        String get( final String from,
                    final URI predicate )
        {
            return get( from, predicate, true );
        }

        /**
         * Returns the path name for a subject / predicate as an optional path.
         *
         * @param from      path subject
         * @param predicate path predicate
         *
         * @return path name
         */
        String getOptional( final String from,
                            final URI predicate )
        {
            return get( from, predicate, false );
        }

        /**
         * Returns the path name for a subject / predicate.
         *
         * @param from      path subject
         * @param predicate path predicate
         * @param mandatory if path is mandatory or optional
         *
         * @return path name
         */
        private String get( final String from,
                            final URI predicate,
                            final boolean mandatory )
        {
            final String ns = namespaces.get( predicate.getNamespace() );
            final String key = format( "{%s} %s:%s", from, ns, predicate.getLocalName() );
            String path = paths.get( key );
            if ( path == null )
            {
                path = "v" + counter++;
                paths.put( key, path );
            }
            if ( mandatory )
            {
                this.mandatory.add( key );
            }
            return path;
        }

        /**
         * Returns the paths string form to be added to an SeRQL query.
         *
         * @return query part for namespaces
         */
        String queryPart()
        {
            final StringBuilder paths = new StringBuilder();
            for ( Map.Entry<String, String> path : this.paths.entrySet() )
            {
                if ( paths.length() != 0 )
                {
                    paths.append( ",\n" );
                }
                if ( mandatory.contains( path.getKey() ) )
                {
                    paths.append( format( "%s {%s}", path.getKey(), path.getValue() ) );
                }
                else
                {
                    paths.append( format( "[%s {%s}]", path.getKey(), path.getValue() ) );
                }
            }

            if ( paths.length() > 0 )
            {
                paths.append( ",\n" );
            }
            paths.insert( 0, "\n  FROM\n" ).append( "{s} p {v}" );

            return paths.toString();
        }

    }

    /**
     * Namespaces -> uri.
     */
    private static class Namespaces
    {

        /**
         * Map between namespace and URI.
         */
        private final Map<String, String> namespaces;
        /**
         * Counter used for automatically namespace of an uri generation.
         * Increased on each namespace creation.
         */
        private int m_counter;

        /**
         * Constructor.
         */
        Namespaces()
        {
            namespaces = new TreeMap<String, String>();
            m_counter = 0;
        }

        /**
         * Returns a namespace for an uri. If uri was not already being used generates a new namespace.
         *
         * @param uri uri to get teh namespace for
         *
         * @return uri corresponding namespace
         */
        String get( final String uri )
        {
            String prefix = namespaces.get( uri );
            if ( prefix == null )
            {
                if ( "http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals( uri ) )
                {
                    prefix = "rdf";
                }
                else if ( "http://www.w3.org/2000/01/rdf-schema#".equals( uri ) )
                {
                    prefix = "rdfs";
                }
                else
                {
                    prefix = "ns" + m_counter++;
                }
                namespaces.put( uri, prefix );
            }
            return prefix;
        }

        /**
         * Returns the namespaces string form to be added to an SeRQL query.
         *
         * @return query part for namespaces
         */
        String queryPart()
        {
            final StringBuilder namespaces = new StringBuilder();
            for ( Map.Entry<String, String> namespace : this.namespaces.entrySet() )
            {
                if ( namespaces.length() != 0 )
                {
                    namespaces.append( ",\n" );
                }
                namespaces.append( format( "%s = <%s>", namespace.getValue(), namespace.getKey() ) );
            }
            if ( namespaces.length() > 0 )
            {
                namespaces.insert( 0, "\n  USING NAMESPACE\n" );
            }
            return namespaces.toString();
        }

    }

}