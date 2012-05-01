package org.sonatype.sisu.rdf.sesame.jena.internal;

import java.util.Iterator;

import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.query.BindingSet;
import org.openrdf.query.impl.MapBindingSet;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

class JenaUnmarshaller
{
    private final ValueFactory valueFactory;

    JenaUnmarshaller( ValueFactory valueFactory )
    {
        this.valueFactory = valueFactory;
    }

    Statement unmarshallStatement( com.hp.hpl.jena.rdf.model.Statement statement )
    {
        com.hp.hpl.jena.rdf.model.Resource subject = statement.getSubject();
        Property predicate = statement.getPredicate();
        RDFNode object = statement.getObject();
        Value value = unmarshallValue( object );
        Statement result = valueFactory.createStatement(
            valueFactory.createURI( subject.getNameSpace(), subject.getLocalName() ),
            valueFactory.createURI( predicate.getNameSpace(), predicate.getLocalName() ),
            value );

        return result;
    }

    BindingSet unmarshallBindingSet( QuerySolution solution, int bindingSetSize )
    {
        MapBindingSet bindingSet = new MapBindingSet( bindingSetSize );
        Iterator<String> vars = solution.varNames();
        while ( vars.hasNext() )
        {
            String name = vars.next();
            RDFNode object = solution.get( name );
            Value value = unmarshallValue( object );
            bindingSet.addBinding( name, value );
        }
        return bindingSet;
    }

    private Value unmarshallValue( RDFNode object )
    {
        Value value;
        if ( object.isLiteral() )
        {
            Literal literal = object.asLiteral();
            if ( literal.getLanguage() != null && literal.getLanguage().trim().length() > 0 )
            {
                value = valueFactory.createLiteral( literal.getLexicalForm(), literal.getLanguage() );
            }
            else if ( literal.getDatatypeURI() != null
                && !XMLSchema.STRING.stringValue().equals( literal.getDatatypeURI() ) )
            {
                String typeURI = literal.getDatatypeURI();
                typeURI = typeURI.replace( "integer", "int" );
                value =
                    valueFactory.createLiteral( literal.getLexicalForm(), valueFactory.createURI( typeURI ) );
            }
            else
            {
                value = valueFactory.createLiteral( literal.getLexicalForm() );
            }
        }
        else if ( object.isAnon() )
        {
            value = valueFactory.createBNode();
        }
        else
        {
            com.hp.hpl.jena.rdf.model.Resource resource = object.asResource();
            value = valueFactory.createURI( resource.getNameSpace(), resource.getLocalName() );
        }
        return value;
    }

}
