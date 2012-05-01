package org.sonatype.sisu.rdf.query;

public class Parameter
{
    private final String name;

    private final String value;

    public Parameter( String name, String value )
    {
        this.name = name;
        this.value = value;
    }

    public String name()
    {
        return name;
    }

    public String value()
    {
        return value;
    }

    public static Parameter parameter( String name, String value )
    {
        return new Parameter( name, value );
    }

}
