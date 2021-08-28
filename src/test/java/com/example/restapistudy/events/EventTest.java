package com.example.restapistudy.events;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class EventTest {

    @Test
    public void builder() throws Exception {
        //given
        Event event = Event.builder()
                .name("hhh")
                .description("kkk")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean() throws Exception {
        //given
        String name = "spring";
        String description = "kimong";

        //when
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        //then
        assertThat(event.getName()).isEqualTo("spring");
        assertThat(event.getDescription()).isEqualTo("kimong");

    }
}