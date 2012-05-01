package org.sonatype.sisu.rdf.sesame.jena.internal;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class JenaMarshaller
{

    public com.hp.hpl.jena.rdf.model.Resource marshallResource( Model model, Resource subject )
    {
        if ( subject == null )
        {
            return null;
        }
        if ( subject instanceof BNode )
        {
            return model.createResource();
        }
        return model.createResource( subject.stringValue() );
    }

    public Property marshallPredicate( Model model, URI predicate )
    {
        if ( predicate == null )
        {
            return null;
        }
        return model.createProperty( predicate.getNamespace(), predicate.getLocalName() );
    }

    public RDFNode marshallObject( Model model, Value object )
    {
        if ( object == null )
        {
            return null;
        }
        if ( object instanceof Literal )
        {
            Literal literal = (Literal) object;
            if ( literal.getLanguage() == null )
            {
                if ( literal.getDatatype() == null )
                {
                    return model.createLiteral( object.stringValue() );
                }
                else
                {
                    return model.createTypedLiteral( object.stringValue(), literal.getDatatype().stringValue() );
                }
            }
            else
            {
                return model.createLiteral( literal.stringValue(), literal.getLanguage() );
            }
        }
        else if ( object instanceof BNode )
        {
            return model.createResource();
        }
        else if ( object instanceof URI )
        {
            URI uri = (URI) object;
            return model.createResource( uri.stringValue() );
        }
        throw new IllegalArgumentException( "Unsupported type of object:" + object.stringValue() );
    }
}
