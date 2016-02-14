/**
 *
 * Copyright (c) 2009-2014 Freedomotic team http://freedomotic.com
 *
 * This file is part of Freedomotic
 *
 * This Program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 *
 * This Program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Freedomotic; see the file COPYING. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.freedomotic.plugins.devices.restapiv3.resources.atmosphere;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.freedomotic.api.EventTemplate;
import com.freedomotic.plugins.devices.restapiv3.RestAPIv3;
import com.freedomotic.plugins.devices.restapiv3.representations.MessageCalloutRepresentation;
import io.swagger.annotations.Api;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import org.atmosphere.config.service.AtmosphereService;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;

/**
 *
 * @author matteo
 */
@Path(AtmosphereMessageCalloutResource.PATH)
@Api(value = "ws_messageCallout", description = "WS for receiving generic notifications", position = 10)
@AtmosphereService(
        dispatch = false,
        interceptors = {AtmosphereResourceLifecycleInterceptor.class},
        path = "/" + RestAPIv3.API_VERSION + "/ws/" + AtmosphereMessageCalloutResource.PATH,
        servlet = "org.glassfish.jersey.servlet.ServletContainer")
public class AtmosphereMessageCalloutResource extends AbstractWSResource {

    private static final Logger LOG = Logger.getLogger(AtmosphereMessageCalloutResource.class.getName());

    public final static String PATH = "messagecallout";
    private final BroadcasterFactory factory;
    
    @Inject
    public AtmosphereMessageCalloutResource(BroadcasterFactory factory){
        super();
        this.factory=factory;
    }
    
    @Override
    public void broadcast(EventTemplate message) {
        if (api != null) {
            String msg;
            try {
                msg = om.writeValueAsString(new MessageCalloutRepresentation(message.getProperty("message.text")));
                factory
                        .lookup("/" + RestAPIv3.API_VERSION + "/ws/" + AtmosphereMessageCalloutResource.PATH,true)
                        .broadcast(msg);
            } catch (JsonProcessingException ex) {
            }
        }
    }
}
