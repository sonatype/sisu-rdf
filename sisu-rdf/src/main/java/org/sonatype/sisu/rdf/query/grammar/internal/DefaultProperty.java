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
import org.openrdf.model.Value;
import org.sonatype.sisu.rdf.query.grammar.Property;


/**
 * Default {@link Property} implementation.
 *
 * @author Alin Dreghiciu
 */
public class DefaultProperty<T extends Value>
    implements Property<T>
{

    /**
     * Property predicate.
     */
    private final URI m_predicate;
    /**
     * Property value.
     */
    private final T m_value;

    /**
     * Constructor.
     *
     * @param predicate property predicate
     * @param value     property value
     */
    DefaultProperty( final URI predicate,
                     final T value )
    {
        assert predicate != null : "Predicate must be specified (cannot be null)";
        assert value != null : "Value must be specified (cannot be null)";

        m_predicate = predicate;
        m_value = value;
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
    public T value()
    {
        return m_value;
    }

    /**
     * Equals any {@link Property} with the same predicate and value.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof Property ) )
        {
            return false;
        }

        Property<?> that = (Property<?>) o;

        return predicate().equals( that.predicate() )
               && value().equals( that.value() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        int result = predicate().hashCode();
        result = 31 * result + value().hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return String.format( "%s %s", predicate(), value() );
    }

}