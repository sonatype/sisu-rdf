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

import org.sonatype.sisu.rdf.query.grammar.Criteria;
import org.sonatype.sisu.rdf.query.grammar.Negation;


/**
 * Default {@link Negation} implementation.
 *
 * @author Alin Dreghiciu
 */
public class DefaultNegation
    implements Negation
{

    /**
     * Negated condition.
     */
    private final Criteria m_condition;

    /**
     * Constructor.
     *
     * @param condition negated condition
     */
    public DefaultNegation( final Criteria condition )
    {
        assert condition != null : "Negated condition must be specified (cannot be null)";

        m_condition = condition;
    }

    /**
     * {@inheritDoc}
     */
    public Criteria criteria()
    {
        return m_condition;
    }

    /**
     * Equals any {@link Negation} with the same condition.
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
        if ( !( o instanceof Negation ) )
        {
            return false;
        }

        final Negation that = (Negation) o;

        return criteria().equals( that.criteria() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        return criteria().hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return String.format( "NOT (%s)", m_condition );
    }
}