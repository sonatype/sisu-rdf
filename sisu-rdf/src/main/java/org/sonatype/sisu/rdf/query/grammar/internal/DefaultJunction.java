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
import org.sonatype.sisu.rdf.query.grammar.Junction;

/**
 * Default {@link Junction} implementation.
 *
 * @author Alin Dreghiciu
 */
abstract class DefaultJunction
    implements Junction
{

    /**
     * Junction left side condition.
     */
    private final Criteria m_left;
    /**
     * Junction right side condition.
     */
    private final Criteria m_right;

    /**
     * Constructor.
     *
     * @param left  left side condition
     * @param right right side condition
     */
    DefaultJunction( final Criteria left,
                     final Criteria right )
    {
        assert left != null : "Left side condition must be specified (cannot be null)";
        assert right != null : "Right side condition must be specified (cannot be null)";

        m_left = left;
        m_right = right;
    }

    /**
     * {@inheritDoc}
     */
    public Criteria left()
    {
        return m_left;
    }

    /**
     * {@inheritDoc}
     */
    public Criteria right()
    {
        return m_right;
    }

    /**
     * Equals any {@link Junction} with the same left and right conditions.
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
        if ( !( o instanceof Junction ) )
        {
            return false;
        }

        final Junction that = (Junction) o;

        return left().equals( that.left() )
               && right().equals( that.right() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        int result = left().hashCode();
        result = 31 * result + right().hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return String.format( "(%s) %s (%s)", m_left, junctionSymbol(), m_right );
    }

    /**
     * Returns the junction symbol to be used in string form.
     *
     * @return junction symbol
     */
    abstract String junctionSymbol();

}