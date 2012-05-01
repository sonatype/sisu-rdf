package org.sonatype.sisu.rdf;

public class RepositoryIdentity
{
    private final String value;

    public RepositoryIdentity( String value )
    {
        this.value = value;
    }

    public String stringValue()
    {
        return value;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( value == null ) ? 0 : value.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        RepositoryIdentity other = (RepositoryIdentity) obj;
        if ( value == null )
        {
            if ( other.value != null )
                return false;
        }
        else if ( !value.equals( other.value ) )
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "RepositoryIdentity [value=" + value + "]";
    }

    public static RepositoryIdentity repositoryIdentity( String value )
    {
        return new RepositoryIdentity( value );
    }

}
