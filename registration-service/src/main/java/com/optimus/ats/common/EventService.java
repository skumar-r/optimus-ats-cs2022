package com.optimus.ats.common;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.eventbus.EventBus;

@ApplicationScoped
public class EventService {
    
    @Inject
    EventBus bus;

    public void eventLog( String action, Object dto, String stringIfDtoIsNull){       
        JsonObject mapObj = new JsonObject();
        mapObj.put("serviceName", "registration-service");
        mapObj.put("action",action);
        mapObj.put("details",Objects.nonNull(dto)?JsonObject.mapFrom(dto).toString(): stringIfDtoIsNull);
        bus.publish("log",mapObj);
    } 
}
