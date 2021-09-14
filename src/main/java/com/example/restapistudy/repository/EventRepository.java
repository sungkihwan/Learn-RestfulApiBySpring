package com.example.restapistudy.repository;


import com.example.restapistudy.events.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Integer> {
}
