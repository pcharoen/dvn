package edu.harvard.iq.dvn.api.resources;

import edu.harvard.iq.dvn.api.exceptions.*;
import edu.harvard.iq.dvn.api.entities.*;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author leonidandreev
 */
/*
 * This app provides manual, user-controlled registration for the 
 * entities and services of the data-sharing API.
 */
public class DvnApiApp extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        // register root resources/providers:
        classes.add(DvnApiRootResource.class);
        // register service entities:
        classes.add(MetadataInstance.class);
        classes.add(MetadataFormats.class);
        classes.add(MetadataSearchFields.class);
        classes.add(MetadataSearchResults.class);
        // register writers for the entities above:
        classes.add(MetadataWriter.class);
        classes.add(MetadataFormatsWriter.class);
        classes.add(MetadataSearchFieldsWriter.class);
        classes.add(MetadataSearchResultsWriter.class);
        // register custom exceptions:
        classes.add(NotFoundException.class);
        classes.add(NotFoundExceptionMapper.class);
        // register resource beans that will supply the resources above:
        classes.add(MetadataResourceBean.class);
	classes.add(MetadataFormatsResourceBean.class);
        classes.add(MetadataSearchFieldsResourceBean.class);
        classes.add(MetadataSearchResultsResourceBean.class);
        // and the main EJB singleton, the workhorse of the API:
        classes.add(MetadataSingletonBean.class);
        return classes;
    }
}