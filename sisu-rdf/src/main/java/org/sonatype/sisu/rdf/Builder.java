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

package org.sonatype.sisu.rdf;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 * Utilities for creating models.
 * 
 * @author Alin Dreghiciu
 */
public class Builder
{

    private static ValueFactory valueFactory = new ValueFactoryImpl();

    /**
     * Private constructor.
     */
    private Builder()
    {
        // utility class
    }

    /**
     * Builder method for a {@link Resource}.
     * 
     * @param uri full resource uri
     * @return created resource
     */
    public static Resource resource( final String uri )
    {
        return uri( uri );
    }

    /**
     * Builder method for a {@link Resource}.
     * 
     * @param namespace resource namespace
     * @param localName resource local name
     * @return created
     */
    public static Resource resource( final String namespace,
                                     final String localName )
    {
        return uri( namespace, localName );
    }

    /**
     * Clones a resource.
     * 
     * @param resource to clone
     * @return cloned resource
     */
    public static Resource resource( final Resource resource )
    {
        assert resource != null : "Cloned resource must be provided (cannot be null)";

        return resource( resource.stringValue() );
    }

    /**
     * Builder method for a predicate ({@link URI}).
     * 
     * @param uri full predicate uri
     * @return create predicate
     */
    public static URI predicate( final String uri )
    {
        return uri( uri );
    }

    /**
     * Builder method for a predicate ({@link URI}).
     * 
     * @param namespace predicate namespace
     * @param localName predicate local name
     * @return created predicate
     */
    public static URI predicate( final String namespace,
                                 final String localName )
    {
        return uri( namespace, localName );
    }

    /**
     * Clones a predicate.
     * 
     * @param predicate to clone
     * @return cloned predicate
     */
    public static URI predicate( final URI predicate )
    {
        assert predicate != null : "Cloned resource must be provided (cannot be null)";

        return uri( predicate.getNamespace(), predicate.getLocalName() );
    }

    /**
     * Builder statement for a literal ({@link Value}).
     * 
     * @param value value in string form
     * @return created value
     */
    public static Value literal( final String value )
    {
        if ( value == null )
        {
            throw new IllegalArgumentException( "Literal cannot be null" );
        }
        return valueFactory.createLiteral( value );
    }

    public static Value decimal( final Double value )
    {
        if ( value == null )
        {
            throw new IllegalArgumentException( "Literal cannot be null" );
        }
        return valueFactory.createLiteral( value );
    }

    /**
     * Clones a value.
     * 
     * @param value to clone
     * @return cloned value
     */
    public static Value value( final Value value )
    {
        assert value != null : "Cloned value must be provided (cannot be null)";

        // TODO review - not all values are literals
        return literal( value.stringValue() );
    }

    /**
     * Builder method for an {@link Value} for a resource type.
     * 
     * @param namespace type namespace
     * @param localName type local name
     * @return created value
     */
    public static URI resourceType( final String namespace,
                                      final String localName )
    {
        return uri( namespace, localName );
    }

    public static Statement statement( Resource subject, URI predicate, Value object )
    {
        return valueFactory.createStatement( subject, predicate, object );
    }

    public static Statement statement( Resource subject, URI predicate, Value object, Resource context )
    {
        return valueFactory.createStatement( subject, predicate, object, context );
    }

    /**
     * Builder method for an {@link URI}.
     * 
     * @param uri full uri
     * @return created
     */
    private static URI uri( final String uri )
    {
        return valueFactory.createURI( uri );
    }

    /**
     * Builder method for an {@link URI}.
     * 
     * @param namespace uri namespace
     * @param localName uri local name
     * @return created
     */
    private static URI uri( final String namespace,
                            final String localName )
    {
        return valueFactory.createURI( namespace, localName );
    }

}