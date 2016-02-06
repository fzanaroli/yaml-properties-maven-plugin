package org.codehaus.mojo.properties;

import java.util.HashSet;
import java.util.Set;

import edu.emory.mathcs.backport.java.util.Arrays;

public enum ResourceType 
{

    PROPERTIES( ".properties" ),
    YAML( new String[] { ".yml", ".yaml" } );

    private final Set<String> fileExtensions;

    ResourceType( final String ... fileExtensions ) 
    {
        this.fileExtensions = new HashSet<String>( Arrays.asList( fileExtensions ) );
    }

    public Set<String> fileExtensions() 
    {
        return new HashSet<String>( fileExtensions );
    }

    public static Set<String> allFileExtensions( final ResourceType ... resourceTypes ) 
    {
        final Set<String> extensions = new HashSet<String>();
        for ( final ResourceType resourceType : resourceTypes ) 
	{
            extensions.addAll( resourceType.fileExtensions() );
        }

        return extensions;
    }

    public static ResourceType getByFileName( final String fileName ) 
    {
        for ( final ResourceType resourceType : ResourceType.values() ) 
	{
            for ( final String extension : resourceType.fileExtensions() ) 
	    {
                if ( fileName.endsWith( extension ) ) 
		{
                    return resourceType;
                }
            }
        }

        return null;
    }

}
