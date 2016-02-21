/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.plugins.devices.restapiv3.resources.atmosphere;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.freedomotic.api.API;
import com.freedomotic.app.FreedomoticInjector;
import com.freedomotic.plugins.devices.restapiv3.RestAPIv3;
import com.freedomotic.plugins.devices.restapiv3.representations.PermissionCheckRepresentation;
import com.freedomotic.security.User;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.atmosphere.annotation.Suspend;
import org.atmosphere.config.service.AtmosphereService;
import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.FrameworkConfig;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.ShiroInterceptor;

/**
 *
 * @author matteo
 */
@Path(AtmospherePermissionCheckResource.PATH)
@Api(value = "ws_permissionCheck")
@AtmosphereService(
        dispatch = false,
        interceptors = {AtmosphereResourceLifecycleInterceptor.class, ShiroInterceptor.class},
        path = "/" + RestAPIv3.API_VERSION + "/ws/" + AtmospherePermissionCheckResource.PATH,
        servlet = "org.glassfish.jersey.servlet.ServletContainer")
public class AtmospherePermissionCheckResource {

    public final static String PATH = "ispermitted";

    private final static Injector INJECTOR = Guice.createInjector(new FreedomoticInjector());
    private final static API api = INJECTOR.getInstance(API.class);
    protected ObjectMapper om;

    public AtmospherePermissionCheckResource() {
        om = new ObjectMapper();
        // JAXB annotation
        AnnotationIntrospector jaxbIntrospector = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
        AnnotationIntrospector jacksonIntrospector = new JacksonAnnotationIntrospector();
        om.setAnnotationIntrospector(new AnnotationIntrospectorPair(jaxbIntrospector, jacksonIntrospector));
    }

    @Context
    private HttpServletRequest request;

    @GET
    @Suspend()
    public String get(@Context AtmosphereResource res) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        res.getRequest().setAttribute("FD_USER", username);
        String id = res.uuid();
        //TODO: save user and uuid
        return "";
    }

    @POST
    public void query(String permission) {
        if (api != null) {
            AtmosphereResource r = (AtmosphereResource) request.getAttribute(ApplicationConfig.ATMOSPHERE_RESOURCE);
            if (r != null) {
                String username = r.getRequest().getUserPrincipal().toString();
                User u = api.getAuth().getUser(username);
                Boolean permOK = u.isPermitted(permission);
                PermissionCheckRepresentation p = new PermissionCheckRepresentation(u.getName(), permission, permOK);
                try {
                    r.getResponse().write(om.writeValueAsString(p));
                } catch (JsonProcessingException ex) {
                }
            } else {
                throw new IllegalStateException();
            }
        }
    }

}
