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
import org.sonatype.sisu.rdf.query.grammar.Bound;


/**
 * Default {@link Bound} implementation.
 *
 * @author Alin Dreghiciu
 */
public class DefaultBound
    implements Bound
{

    /**
     * Bound predicate.
     */
    private final URI m_predicate;

    /**
     * Constructor.
     *
     * @param predicate bound predicate
     */
    public DefaultBound( final URI predicate )
    {
        assert predicate != null : "Bound predicate must be specified (cannot be null)";

        m_predicate = predicate;
    }

    /**
     * {@inheritDoc}
     */
    public URI predicate()
    {
        return m_predicate;
    }

    /**
     * Equals any {@link Bound} with the same predicate.
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
        if ( !( o instanceof Bound ) )
        {
            return false;
        }

        final Bound that = (Bound) o;

        return predicate().equals( that.predicate() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        return predicate().hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return String.format( "%s has value", m_predicate );
    }

}