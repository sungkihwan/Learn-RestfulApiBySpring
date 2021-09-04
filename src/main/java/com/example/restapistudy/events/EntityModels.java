package com.example.restapistudy.events;

import com.example.restapistudy.index.IndexController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class EntityModels {

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

    public EntityModel<Errors> getErrorEntityModel(Errors errors) {
        WebMvcLinkBuilder selfLink = linkTo(methodOn(IndexController.class).index());
        EntityModel<Errors> errorsEntityModel = EntityModel.of(errors,
                selfLink.withRel("index"));
        return errorsEntityModel;
    }
}
