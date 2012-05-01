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
import org.sonatype.sisu.rdf.query.grammar.Equal;

/**
 * Default {@link Equal} implementation.
 *
 * @author Alin Dreghiciu
 */
public class DefaultEqual<T extends Value>
    extends DefaultComparison<T>
    implements Equal<T>
{

    /**
     * Constructor.
     *
     * @param predicate comparison predicate
     * @param value     comparison value
     */
    public DefaultEqual( final URI predicate,
                         final T value )
    {
        super( predicate, value );
    }

    /**
     * Returns "=";
     *
     * {@inheritDoc}
     */
    @Override
    String comparatorSymbol()
    {
        return "=";
    }

}