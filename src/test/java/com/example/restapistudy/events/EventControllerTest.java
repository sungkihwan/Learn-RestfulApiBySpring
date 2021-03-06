package com.example.restapistudy.events;

import com.example.restapistudy.common.BaseControllerTest;
import com.example.restapistudy.commons.TestDescription;
import com.example.restapistudy.dto.EventDto;
import com.example.restapistudy.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EventControllerTest extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;

    @Test
    @TestDescription("μ μ μΈν")
    public void createEvent() throws Exception {
        //given
        EventDto eventDto = EventDto.builder()
                .name("kihwan")
                .description("Rest Master")
                .beginEnrollmentDateTime(LocalDateTime.of(2021,8,25,11,24))
                .closeEnrollmentDateTime(LocalDateTime.of(2021,8,25,12,24))
                .beginEventDateTime(LocalDateTime.of(2021,8,26,11,24))
                .endEventDateTime(LocalDateTime.of(2021,8,27,11,24))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("κ°μ΄ λμ‘΄")
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update an existing event"),
                                linkWithRel("profile").description("link to profile")),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")),
                        requestFields(
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
                                fieldWithPath("basePrice").description("basePrice of new event"),
                                fieldWithPath("maxPrice").description("maxPrice of new event"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event"),
                                fieldWithPath("location").description("location of new event")),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")),
//                        relaxedResponseFields( λ³Έλ¬Έμ λͺ¨λ  λ΄μ©μ λ¬Έμν νμ§ μμ λ μ¬μ©(links μ λ³΄ λ¬Έμν X)
                        responseFields(
                                fieldWithPath("id").description("id of new event"),
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
                                fieldWithPath("basePrice").description("basePrice of new event"),
                                fieldWithPath("maxPrice").description("maxPrice of new event"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("free").description("this event is free or not"),
                                fieldWithPath("offline").description("this event is offline or not"),
                                fieldWithPath("eventStatus").description("eventStatus of new event"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query events"),
                                fieldWithPath("_links.update-event.href").description("link to update an existing event"),
                                fieldWithPath("_links.profile.href").description("link to profile"))
                ));
    }

    @Test
    @TestDescription("unknown κ°μ΄ λ€μ΄μ¨ κ²½μ°μ μλ¬κ° λ°μνλ μΌμ΄μ€ -> yml νμΌ μμ ν΄μ μ μ°νκ² λ°μμ€ μλ μμ")
    public void createEvent_BadRequest_UnknownInput() throws Exception {
        //given
        Event event = Event.builder()
                .id(100)
                .name("kihwan")
                .description("Rest Master")
                .beginEnrollmentDateTime(LocalDateTime.of(2021,8,25,11,24))
                .closeEnrollmentDateTime(LocalDateTime.of(2021,8,25,12,24))
                .beginEventDateTime(LocalDateTime.of(2021,8,26,11,24))
                .endEventDateTime(LocalDateTime.of(2021,8,27,11,24))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("κ°μ΄ λμ‘΄")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @TestDescription("μλ ₯ κ°μ΄ λΉμ΄μλ κ²½μ°μ μλ¬κ° λ°μ")
    public void createEvent_BadRequest_EmptyInput() throws Exception {
        //given
        EventDto eventDto = EventDto.builder()
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("μλ ₯ κ°μ΄ μλͺ»λ κ²½μ°μ μλ¬κ° λ°μ")
    public void createEvent_BadRequest_WrongInput() throws Exception {
        //given
        EventDto eventDto = EventDto.builder()
                .name("kihwan")
                .description("Rest Master")
                .beginEnrollmentDateTime(LocalDateTime.of(2021,8,25,11,24))
                .closeEnrollmentDateTime(LocalDateTime.of(2021,8,25,12,24))
                .beginEventDateTime(LocalDateTime.of(2021,8,26,11,24))
                // λλλ λ μ§κ° λ λΉ λ₯Έ μλͺ»λ μΈν
                .endEventDateTime(LocalDateTime.of(2021,8,20,11,24))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("κ°μ΄ λμ‘΄")
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("μ΄λ€ μλ¬μΈμ§ ν΄λΌμ΄μΈνΈμ λ³΄μ¬μ£ΌκΈ°")
    public void createEvent_BadRequest_WrongInput2() throws Exception {
        //given
        EventDto eventDto = EventDto.builder()
                .name("kihwan")
                .description("Rest Master")
                .beginEnrollmentDateTime(LocalDateTime.of(2021,8,25,11,24))
                .closeEnrollmentDateTime(LocalDateTime.of(2021,8,25,12,24))
                .beginEventDateTime(LocalDateTime.of(2021,8,26,11,24))
                .endEventDateTime(LocalDateTime.of(2021,8,27,11,24))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("κ°μ΄ λμ‘΄")
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                //.andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists());
                //.andExpect(jsonPath("$[0].rejectedValue").exists());
    }

    // μ‘°ν μΏΌλ¦¬
    @Test
    @TestDescription("μ΄λ²€νΈ λ¦¬μ€νΈ μ‘°ν")
    public void getEvents() throws Exception {
        //given
        IntStream.range(0, 30).forEach(this::generateEvent);

        this.mockMvc.perform(get("/api/events")
                        .param("page","1")
                        .param("size","10")
                        .param("sort","name,DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
        ;
    }

    @Test
    @TestDescription("μ΄λ²€νΈ λ¨κ±΄ μ‘°ν")
    public void getEvent() throws Exception {
        Event event = this.generateEvent(100);

        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists());
    }

    @Test
    @TestDescription("λΉ κ° μ‘°ν 404")
    public void getEvent404() throws Exception {
        Event event = this.generateEvent(100);
        this.mockMvc.perform(get("/api/events/2623424"))
                .andExpect(status().isNotFound());
    }

    @Test
    @TestDescription("μ΄λ²€νΈ μ μ μμ ")
    public void updateEvent() throws Exception {
        Event event = this.generateEvent(400);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(250);
        eventDto.setDescription("λ³κ²½λ νμ€νΈ");
        eventDto.setMaxPrice(45000);
        eventDto.setName("μμ  μλ£");

        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("μμ  μλ£"))
                .andExpect(jsonPath("maxPrice").value(45000))
                .andExpect(jsonPath("description").value("λ³κ²½λ νμ€νΈ"))
                .andExpect(jsonPath("basePrice").value(250))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("update-event"));
    }

    @Test
    @TestDescription("μλͺ»λ λ¦¬νμ€νΈλ‘ 400μλ¬")
    public void updateEvent400Error() throws Exception {
        Event event = this.generateEvent(400);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(250);
        eventDto.setDescription("λ³κ²½λ νμ€νΈ");
        eventDto.setMaxPrice(45000);
        eventDto.setName("μμ  μλ£");

        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("μλ ₯κ°μ΄ μλͺ»λ€ κ²½μ°")
    public void updateEvent400Error2() throws Exception {
        Event event = this.generateEvent(400);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(250000);
        eventDto.setMaxPrice(2000);

        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("μ‘΄μ¬νμ§ μλ μ΄λ²€νΈ μμ ")
    public void updateEvent404() throws Exception {
        Event event = this.generateEvent(400);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        this.mockMvc.perform(put("/api/events/555555555")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private Event generateEvent(int i) {
        Event event = Event.builder()
                .name("event" + i)
                .description("test event")
                .beginEnrollmentDateTime(LocalDateTime.of(2021,8,25,11,24))
                .closeEnrollmentDateTime(LocalDateTime.of(2021,8,25,12,24))
                .beginEventDateTime(LocalDateTime.of(2021,8,26,11,24))
                .endEventDateTime(LocalDateTime.of(2021,8,27,11,24))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("κ°μ΄ λμ‘΄")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();
        return this.eventRepository.save(event);
    }

    @Test
    public void index() throws Exception {
        this.mockMvc.perform(get("/api/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.events").exists());
    }
}