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

import org.openrdf.model.Resource;
import org.sonatype.sisu.rdf.query.grammar.About;

/**
 * Default {@link About} implementation.
 *
 * @author Alin Dreghiciu
 */
public class DefaultAbout
    implements About
{

    /**
     * About subject.
     */
    private final Resource m_subject;

    /**
     * Constructor.
     *
     * @param subject the subject the statements are about
     */
    public DefaultAbout( final Resource subject )
    {
        assert subject != null : "About subject must be specified (cannot be null)";

        m_subject = subject;
    }

    /**
     * {@inheritDoc}
     */
    public Resource subject()
    {
        return m_subject;
    }

    /**
     * Equals any {@link About} with the same subject.
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
        if ( !( o instanceof About ) )
        {
            return false;
        }

        final About that = (About) o;

        return subject().equals( that.subject() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        return subject().hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return String.format( "about %s", m_subject );
    }

}