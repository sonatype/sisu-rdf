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

import org.openrdf.query.QueryLanguage;
import org.sonatype.sisu.rdf.query.grammar.Criteria;

/**
 * Parses criteria into RDF query language.
 *
 * @author Alin Dreghiciu
 */
public interface QueryParser
{

    /**
     * Returns the query language supported by this parser.
     *
     * @return query language
     */
    QueryLanguage language();

    /**
     * Parses the criteria into a query language.
     *
     * @param criteria to be parsed
     *
     * @return parsed query
     */
    String parse( Criteria criteria );

}