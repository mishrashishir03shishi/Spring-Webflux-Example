package com.example.taskmanager.controller;


import com.example.taskmanager.entity.Task;
import com.example.taskmanager.service.PriorityService;
import com.example.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private PriorityService priorityService;

    @PostMapping
    public Mono<Task> createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    @GetMapping
    public Flux<Task> getAllTasks(){
        return taskService.getAllTasks();
    }

    @GetMapping(value = "/{id}")
    public Mono<Task> getTaskById(@PathVariable String id){
        return taskService.getTaskById(id);
    }

    @DeleteMapping(value = "/{id}")
    public Mono<Void> deleteTask(@PathVariable String id){
        return taskService.deleteTask(id);
    }

    @GetMapping("/stream")
    public Flux<Task> streamTasks(){
        return Flux.fromIterable(taskService.getAllTasks().collectList().block())
                .delayElements(Duration.ofMillis(200))
                .onBackpressureBuffer(10)
                .log();
    }

    @GetMapping("/consume")
    public Flux<Task> consumeSlow(){
        return taskService.getAllTasks()
                .delayElements(Duration.ofSeconds(1))
                .log();
    }

    @GetMapping("/consume-stream")
    public Flux<Task> consumeStream(){
        return taskService.consumeStream()
                .delayElements(Duration.ofSeconds(1))
                .log();
    }

    @GetMapping("/enriched")
    public Flux<Map<String, Object>> getEnrichedTasks(){
        return taskService.getAllTasks()
                .flatMap(task -> priorityService.getPriority(task.getId())
                    .map(priority -> {
                        System.out.println("Started assembling enriched task for task : " + task.getId() + " " + task.getTitle());
                        Map<String, Object> enrichedTask = new HashMap<>();
                        enrichedTask.put("id", task.getId());
                        enrichedTask.put("title", task.getTitle());
                        enrichedTask.put("description", task.getDescription());
                        enrichedTask.put("priority", priority);
                        System.out.println("Assembled Enriched Task for task : " + task.getId() + " " + task.getTitle());
                        return enrichedTask;
                    })
                )
                .log();
    }


    @GetMapping("/merge")
    public Flux<Task> mergeTaskStreams() {
        Flux<Task> stream1 = taskService.getAllTasks().delayElements(Duration.ofMillis(300));
        Flux<Task> stream2 = taskService.getAllTasks().delayElements(Duration.ofMillis(500));

        return Flux.merge(stream1, stream2)
                .log();
    }

}
