package com.example.taskmanager.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class PriorityService {

    public Mono<String> getPriority(String taskId){
        System.out.println("Get priority called for taskId : " + taskId);
        return Mono.just("HIGH")
                .delayElement(Duration.ofMillis(300));
    }
}
