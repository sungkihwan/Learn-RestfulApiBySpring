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

    public EntityModel<Event> eventEntityModelSelf(Event event) {
        WebMvcLinkBuilder selfLink = linkTo(EventContorller.class)
                .slash("api")
                .slash("events")
                .slash(event.getId());
        EntityModel<Event> eventEntityModel = EntityModel.of(event,
                selfLink.withSelfRel());
        return eventEntityModel;
    }

    public EntityModel<Event> eventEntityModelCreate(Event event, WebMvcLinkBuilder selfLink) {
        EntityModel<Event> eventEntityModel = EntityModel.of(event,
                selfLink.slash(event.getId()).withSelfRel(),
                selfLink.withRel("query-events"),
                selfLink.withRel("update-event"),
                selfLink.slash("docs/index.html#resource-events-create").withRel("profile"));
        return eventEntityModel;
    }

    public EntityModel<Event> eventEntityModelUpdate(Event event) {
        WebMvcLinkBuilder selfLink = linkTo(EventContorller.class)
                .slash("api")
                .slash("events")
                .slash(event.getId());

        EntityModel<Event> eventEntityModel = EntityModel.of(event,
                selfLink.slash(event.getId()).withSelfRel(),
                selfLink.withRel("query-events"),
                selfLink.withRel("update-event"),
                selfLink.slash("docs/index.html#resource-events-update").withRel("profile"));
        return eventEntityModel;
    }
}
