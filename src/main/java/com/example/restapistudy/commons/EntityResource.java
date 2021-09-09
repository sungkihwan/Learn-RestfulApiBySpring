package com.example.restapistudy.commons;

import com.example.restapistudy.events.Event;
import com.example.restapistudy.events.EventContorller;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import javax.swing.text.html.parser.Entity;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class EntityResource extends EntityModel<Entity> {

    public EntityModel<Event> getEventEntityModelSelf(Event event) {
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventContorller.class)
                .slash("api")
                .slash("events")
                .slash(event.getId());
        EntityModel<Event> eventEntityModel = EntityModel.of(event,
                selfLinkBuilder.withSelfRel());
        return eventEntityModel;
    }

    public EntityModel<Event> getEventEntityModels(Event event, WebMvcLinkBuilder selfLink) {
        EntityModel<Event> eventEntityModel = EntityModel.of(event,
                selfLink.slash(event.getId()).withSelfRel(),
                selfLink.withRel("query-events"),
                selfLink.withRel("update-event"),
                selfLink.slash("docs/index.html#resource-events-create").withRel("profile"));
        return eventEntityModel;
    }
}
