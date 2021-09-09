package com.example.restapistudy.events;

import com.example.restapistudy.commons.EntityResource;
import com.example.restapistudy.commons.ErrorsResource;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

// HATEOAS
// ResourceSupport is now RepresentationModel
// Resource is now EntityModel
// Resources is now CollectionModel
// PagedResources is now PagedModel

@RestController
@RequestMapping
@RequiredArgsConstructor
public class EventContorller {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;
    private final EntityResource entityResource;

    @PostMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {

        // error 메시지에 index 링크추가
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ErrorsResource.modelOf(errors));
        }

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ErrorsResource.modelOf(errors));
        }

        // modelMapper로 Dto를 Entity로 변환
        Event event = modelMapper.map(eventDto, Event.class);

        // update 비즈니스 로직 실행
        event.update();
        Event newEvent = this.eventRepository.save(event);

        // 저장한 값으로 URI 생성하여 body에 응답
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventContorller.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();

        // HATEOAS 링크 생성
        return ResponseEntity.created(createdUri).body(entityResource.getEventEntityModels(event,selfLinkBuilder));
    }

    @GetMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity getEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
        Page<Event> page = this.eventRepository.findAll(pageable);
        PagedModel<EntityModel<Event>> pagedModel = assembler.toModel(page, entityResource::getEventEntityModelSelf);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping(value = "/api/events/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity getEvent(@PathVariable Integer id) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Event event = optionalEvent.get();
        return ResponseEntity.ok(entityResource.getEventEntityModelSelf(event));
    }
}