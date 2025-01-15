package com.aryak.tts_voice.repo;

import com.aryak.tts_voice.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRepository extends MongoRepository<Event, Integer> {

    Event findByEventId(int id);
}

