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
    @TestDescription("정상 인풋")
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
                .location("강촌 더존")
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
//                        relaxedResponseFields( 본문의 모든 내용을 문서화 하지 않을 때 사용(links 정보 문서화 X)
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
    @TestDescription("unknown 값이 들어온 경우에 에러가 발생하는 케이스 -> yml 파일 수정해서 유연하게 받아줄 수도 있음")
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
                .location("강촌 더존")
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
    @TestDescription("입력 값이 비어있는 경우에 에러가 발생")
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
    @TestDescription("입력 값이 잘못된 경우에 에러가 발생")
    public void createEvent_BadRequest_WrongInput() throws Exception {
        //given
        EventDto eventDto = EventDto.builder()
                .name("kihwan")
                .description("Rest Master")
                .beginEnrollmentDateTime(LocalDateTime.of(2021,8,25,11,24))
                .closeEnrollmentDateTime(LocalDateTime.of(2021,8,25,12,24))
                .beginEventDateTime(LocalDateTime.of(2021,8,26,11,24))
                // 끝나는 날짜가 더 빠른 잘못된 인풋
                .endEventDateTime(LocalDateTime.of(2021,8,20,11,24))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강촌 더존")
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("어떤 에러인지 클라이언트에 보여주기")
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
                .location("강촌 더존")
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

    // 조회 쿼리
    @Test
    @TestDescription("이벤트 리스트 조회")
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
    @TestDescription("이벤트 단건 조회")
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
    @TestDescription("빈 값 조회 404")
    public void getEvent404() throws Exception {
        Event event = this.generateEvent(100);
        this.mockMvc.perform(get("/api/events/2623424"))
                .andExpect(status().isNotFound());
    }

    @Test
    @TestDescription("이벤트 정상 수정")
    public void updateEvent() throws Exception {
        Event event = this.generateEvent(400);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(250);
        eventDto.setDescription("변경된 테스트");
        eventDto.setMaxPrice(45000);
        eventDto.setName("수정 완료");

        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("수정 완료"))
                .andExpect(jsonPath("maxPrice").value(45000))
                .andExpect(jsonPath("description").value("변경된 테스트"))
                .andExpect(jsonPath("basePrice").value(250))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("update-event"));
    }

    @Test
    @TestDescription("잘못된 리퀘스트로 400에러")
    public void updateEvent400Error() throws Exception {
        Event event = this.generateEvent(400);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(250);
        eventDto.setDescription("변경된 테스트");
        eventDto.setMaxPrice(45000);
        eventDto.setName("수정 완료");

        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력값이 잘못돤 경우")
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
    @TestDescription("존재하지 않는 이벤트 수정")
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
                .location("강촌 더존")
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