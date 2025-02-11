package com.example.taskmanager.service;

import com.example.taskmanager.entity.Task;
import org.springframework.stereotype.Service;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaskService {

    private final Map<String, Task> taskRepo = new ConcurrentHashMap<>();

    public Mono<Task> createTask(Task task){
        Task newTask = new Task(task.getTitle(), task.getDescription());
        return Mono.just(newTask)
                .delayElement(Duration.ofSeconds(2)) // Simulate a real database operation ora long api call
                //This would block traditional rest api calls but webflux is non-blocking and free to handle other requests
                .doOnNext(t -> taskRepo.put(task.getId(), t))
                .subscribeOn(Schedulers.boundedElastic()); //This instructs webflux to use a thread pool suitable for non-blocking I/o
                //otherwise it will run everything on a single loop
    }

    public Flux<Task> getAllTasks(){
        return Flux.fromIterable(taskRepo.values())
                .delayElements(Duration.ofMillis(100))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteTask(String id){
        taskRepo.remove(id);
        return Mono.empty();
    }

    public Mono<Task> getTaskById(String id){
        Task task = taskRepo.get(id);
        return Mono.just(task);
    }

    public Flux<Task> consumeStream(){
        return Flux.fromIterable(taskRepo.values())
                .delayElements(Duration.ofMillis(200))
                .onBackpressureBuffer(
                        10,
                        item -> System.out.println("Dropping item: " + item),  // Overflow callback
                        BufferOverflowStrategy.DROP_OLDEST
                )
                .log();
    }





}
