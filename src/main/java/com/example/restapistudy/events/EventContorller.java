package com.example.restapistudy.events;

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
    private final EntityModels entityModels;

    @PostMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {

        // error 메시지에 index 링크추가
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(entityModels.getErrorEntityModel(errors));
        }

        // Validation
        eventValidator.validate(eventDto, errors);

        // error body에 담아서 클라이언트에 응답
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(entityModels.getErrorEntityModel(errors));
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
        return ResponseEntity.created(createdUri).body(entityModels.getEventEntityModels(event,selfLinkBuilder));
    }

    @GetMapping("/api/events")
    public ResponseEntity getEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
        Page<Event> page = this.eventRepository.findAll(pageable);
        PagedModel<EntityModel<Event>> pagedModel = assembler.toModel(page, entityModels::getEventEntityModelSelf);
        return ResponseEntity.ok(pagedModel);
    }
}
