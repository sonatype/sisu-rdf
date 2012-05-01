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

package org.sonatype.sisu.rdf.query.grammar.internal;

import org.openrdf.model.URI;
import org.sonatype.sisu.rdf.query.grammar.Criteria;
import org.sonatype.sisu.rdf.query.grammar.Join;

/**
 * Default {@link Join} implementation.
 *
 * @author Alin Dreghiciu
 */
public class DefaultJoin
    implements Join
{

    /**
     * Join predicate.
     */
    private final URI m_predicate;
    /**
     * Join condition.
     */
    private final Criteria m_condition;

    /**
     * Constructor.
     *
     * @param predicate join predicate
     * @param condition join condition
     */
    public DefaultJoin( final URI predicate,
                        final Criteria condition )
    {
        assert predicate != null : "Join predicate must be specified (cannot be null)";
        assert condition != null : "Join condition be specified (cannot be null)";

        m_predicate = predicate;
        m_condition = condition;
    }

    /**
     * {@inheritDoc}
     */
    public URI predicate()
    {
        return m_predicate;
    }

    /**
     * {@inheritDoc}
     */
    public Criteria criteria()
    {
        return m_condition;
    }

    /**
     * Equals any {@link Join} with same predicate/condition.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof Join ) )
        {
            return false;
        }

        final Join that = (Join) o;

        return criteria().equals( that.criteria() )
               && predicate().equals( that.predicate() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        int result = predicate().hashCode();
        result = 31 * result + criteria().hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return String.format( "%s join %s", m_predicate, m_condition );
    }

}