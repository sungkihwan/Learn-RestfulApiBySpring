package com.example.restapistudy.events;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class EventContorller {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    @PostMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        // Validation
        eventValidator.validate(eventDto, errors);
        // error body에 담아서 클라이언트에 응답
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        // modelMapper로 Dto를 Entity로 변환
        Event event = modelMapper.map(eventDto, Event.class);
        // update 비즈니스 로직 실행
        event.update();
        // 데이터 저장
        Event newEvent = this.eventRepository.save(event);
        // 저장한 값으로 URI 생성하여 body에 실어 응답
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventContorller.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();
        // HATEOAS로 링크 생성
        EntityModel<Event> eventEntityModel = EntityModel.of(event,
                selfLinkBuilder.slash(event.getId()).withSelfRel(),
                selfLinkBuilder.withRel("query-events"),
                selfLinkBuilder.withRel("update-event")
        );
        return ResponseEntity.created(createdUri).body(eventEntityModel);
    }
}
