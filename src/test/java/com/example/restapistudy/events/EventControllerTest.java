package com.example.restapistudy.events;

import com.example.restapistudy.commons.TestDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

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
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists());
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
                .andExpect(status().isBadRequest());
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
    @TestDescription("어떤 에러인지 클라이언트에 보여줌")
    public void createEvent_BadRequest_WrongInput1111() throws Exception {
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
                .andExpect(jsonPath("$[0].objectName").exists())
                //.andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists());
                //.andExpect(jsonPath("$[0].rejectedValue").exists());
    }
}