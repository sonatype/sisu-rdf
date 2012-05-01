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

package org.sonatype.sisu.rdf.query;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.sonatype.sisu.rdf.query.grammar.internal.DefaultAbout;
import org.sonatype.sisu.rdf.query.grammar.internal.DefaultBound;
import org.sonatype.sisu.rdf.query.grammar.internal.DefaultConjunction;
import org.sonatype.sisu.rdf.query.grammar.internal.DefaultDisjunction;
import org.sonatype.sisu.rdf.query.grammar.internal.DefaultEqual;
import org.sonatype.sisu.rdf.query.grammar.internal.DefaultJoin;
import org.sonatype.sisu.rdf.query.grammar.internal.DefaultMatches;
import org.sonatype.sisu.rdf.query.grammar.internal.DefaultNegation;

import org.sonatype.sisu.rdf.query.grammar.About;
import org.sonatype.sisu.rdf.query.grammar.Bound;
import org.sonatype.sisu.rdf.query.grammar.Criteria;
import org.sonatype.sisu.rdf.query.grammar.Conjunction;
import org.sonatype.sisu.rdf.query.grammar.Disjunction;
import org.sonatype.sisu.rdf.query.grammar.Equal;
import org.sonatype.sisu.rdf.query.grammar.Join;
import org.sonatype.sisu.rdf.query.grammar.Matches;
import org.sonatype.sisu.rdf.query.grammar.Negation;
import org.sonatype.sisu.rdf.query.grammar.Property;

/**
 * Utilities for building up queries in a fluent api manner.
 *
 * @author Alin Dreghiciu
 */
public class QueryBuilder
{

    /**
     * Private constructor.
     */
    private QueryBuilder()
    {
        // utility class
    }

    /**
     * Builder method for an {@link About} condition.
     *
     * @param subject the subject the statements should be about
     *
     * @return created condition
     */
    public static About about( final Resource subject )
    {
        return new DefaultAbout( subject );
    }

    /**
     * Builder method for an not {@link About} condition.
     *
     * @param subject the subject the statements should be about
     *
     * @return created condition
     */
    public static Negation notAbout( final Resource subject )
    {
        return not( about( subject ) );
    }

    /**
     * Builder method for an {@link Equal} condition.
     *
     * @param predicate predicate to check
     * @param value     value to be equal to
     * @param <T>       value type
     *
     * @return created condition
     */
    public static <T extends Value> Equal<T> eq( final URI predicate,
                                                 final T value )
    {
        return new DefaultEqual<T>( predicate, value );
    }

    /**
     * Builder method for an {@link Equal} condition.
     *
     * @param property predicate / value pair
     * @param <T>      value type
     *
     * @return created condition
     */
    public static <T extends Value> Equal<T> eq( final Property<T> property )
    {
        return eq( property.predicate(), property.value() );
    }

    /**
     * Builder method for a {@link Matches} condition.
     *
     * @param predicate predicate to check
     * @param value     value to match
     * @param <T>       value type
     *
     * @return created condition
     */
    public static <T extends Value> Matches<T> matches( final URI predicate,
                                                        final T value )
    {
        return new DefaultMatches<T>( predicate, value );
    }

    /**
     * Builder method for a {@link Matches} condition.
     *
     * @param property predicate / value pair
     * @param <T>      value type
     *
     * @return created condition
     */
    public static <T extends Value> Matches<T> matches( final Property<T> property )
    {
        return matches( property.predicate(), property.value() );
    }

    /**
     * Builder method for a {@link Conjunction} condition.
     *
     * @param left       left side condition
     * @param right      right side condition
     * @param additional additional condition to be "AND"-ed
     *
     * @return created condition
     */
    public static Conjunction and( final Criteria left,
                                   final Criteria right,
                                   final Criteria... additional )
    {
        Conjunction conjunction = new DefaultConjunction( left, right );
        if ( additional != null )
        {
            for ( Criteria condition : additional )
            {
                conjunction = new DefaultConjunction( conjunction, condition );
            }
        }
        return conjunction;
    }

    /**
     * Builder method for a {@link Disjunction} condition.
     *
     * @param left       left side condition
     * @param right      right side condition
     * @param additional additional condition to be "OR"-ed
     *
     * @return created condition
     */
    public static Disjunction or( final Criteria left,
                                  final Criteria right,
                                  final Criteria... additional )
    {
        Disjunction conjunction = new DefaultDisjunction( left, right );
        if ( additional != null )
        {
            for ( Criteria condition : additional )
            {
                conjunction = new DefaultDisjunction( conjunction, condition );
            }
        }
        return conjunction;
    }

    /**
     * Builder method for a {@link Negation} condition.
     *
     * @param condition negated condition
     *
     * @return created condition
     */
    public static Negation not( final Criteria condition )
    {
        return new DefaultNegation( condition );
    }

    /**
     * Builder method for a {@link Join}.
     *
     * @param predicate join predicate
     * @param condition join condition
     *
     * @return created condition
     */
    public static Join join( final URI predicate,
                             final Criteria condition )
    {
        return new DefaultJoin( predicate, condition );
    }

    /**
     * Builder method for a {@link Bound} condition.
     *
     * @param predicate bounded predicate (predicate that must have a value)
     *
     * @return created condition
     */
    public static Bound has( final URI predicate )
    {
        return new DefaultBound( predicate );
    }

    /**
     * Builder method for an all statements condition.
     *
     * @return created condition
     */
    public static Criteria all()
    {
        return null;
    }

}