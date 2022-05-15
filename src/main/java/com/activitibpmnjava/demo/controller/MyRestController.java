package com.activitibpmnjava.demo.controller;

import com.activitibpmnjava.demo.dto.*;
import com.activitibpmnjava.demo.exception.WorkflowException;
import com.activitibpmnjava.demo.model.request.AppRequest;
import com.activitibpmnjava.demo.model.request.UserRegistrationRequest;
import com.activitibpmnjava.demo.model.response.AppResponse;
import com.activitibpmnjava.demo.service.WorkflowService;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class MyRestController {
    private Logger logger = LoggerFactory.getLogger(MyRestController.class);

    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private WorkflowService workflowService;

    @GetMapping(value = "/start-my-process",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String startMyProcess() {
//        runtimeService.startProcessInstanceByKey("my-process");
        Map<String, Object> user = new HashMap<>();
        user.put("INITIATOR", "Kagzz");
        runtimeService.startProcessInstanceByKey("my-process", user);
        long count = runtimeService.createProcessInstanceQuery().count();
        for (int i = 0; i < count; i++) {
            logger.info("Task id: {}", runtimeService.createProcessInstanceQuery().list().get(i));
            logger.info("process id: {}", runtimeService.createProcessInstanceQuery().list().get(i).getId());
        }
        logger.info("We now have {}   process instances", count);
        return "Process started. Number of currently running process instances = "
                + count;
    }

    @GetMapping("/get-tasks/{processInstanceId}")
    public List<TaskRepresentation> getTasks(
            @PathVariable String processInstanceId) {

        List<Task> usertasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();

        return usertasks.stream()
                .map(task -> new TaskRepresentation(
                        task.getId(), task.getName(), task.getProcessInstanceId()))
                .collect(Collectors.toList());
    }

    @GetMapping("/complete-task-A/{processInstanceId}")
    public void completeTaskA(@PathVariable String processInstanceId) {
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        String taskId = task.getId();
        logger.info("Completing taskId {}", taskId);
        taskService.complete(taskId);
    }

    @GetMapping("/star-twitter-process")
    public ResponseEntity<?>  startTwitterProcess() {
        String docType = WorkflowDocType.TWITTER_DOCUMENT.getFileName();
        Map<String, Object> variables = new HashMap<>();
        variables.put("quote", "Twitter Here");
        String businessKey = "12345678";
        String currTrans = "TwitterTrans001";
        AppResponse response = workflowService.startNewWorkFlow(docType, variables, businessKey, currTrans);
        return ResponseEntity.ok(response);
    }


    @GetMapping(value = "/start-process",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void startProcess() {

        UserRegistrationRequest userRequest = new UserRegistrationRequest();
        userRequest.setUserId("23334444");
        userRequest.setApproverId("Kagzzz");

        processRuntime
                .start(ProcessPayloadBuilder
                        .start()
                        .withName("User Registration Process")
                        .withProcessDefinitionKey("userRegistrationProcess")
                        .withVariable("registrationRequest", userRequest)
                        .build()
                );
        logger.info("We now have {}   process instances", runtimeService.createProcessInstanceQuery().count());
    }

    @PostMapping(value = "/start-process",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> startProcess(@RequestBody UserRegistrationRequest userRequest) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("registrationRequest", userRequest);
        String docType = WorkflowDocType.USER_REGISTRATION_DOCUMENT.getFileName();
        String businessKey = userRequest.getNationalIdNo();
        String currTrans = userRequest.toString();
//        runtimeService.startProcessInstanceByKey("userRegistrationProcess", variables);
//        logger.info("We now have {}   process instances", runtimeService.createProcessInstanceQuery().count());
        AppResponse response = workflowService.startNewWorkFlow(docType, variables, businessKey, currTrans);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping(value = "/start-quote-process",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> startQuoteProcess(@RequestBody UserRegistrationRequest userRequest) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("quote", userRequest);
        String docType = WorkflowDocType.QUOTE_DOCUMENT.getFileName();
        String businessKey = userRequest.getNationalIdNo();
        String currTrans = userRequest.toString();
//        runtimeService.startProcessInstanceByKey("userRegistrationProcess", variables);
//        logger.info("We now have {}   process instances", runtimeService.createProcessInstanceQuery().count());
        AppResponse response = workflowService.startNewWorkFlow(docType, variables, businessKey, currTrans);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = "/start",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AppResponse> startRegistrationProcess(@RequestBody UserRegistrationRequest userRegistrationRequest) {
        Map<String, Object> props = new HashMap<>();
        props.put("registrationRequest", userRegistrationRequest);
        props.put("INITIATOR", userRegistrationRequest.getUserId());
        String docType = WorkflowDocType.HELLO_DOCUMENT.getFileName();
        String businessKey = UUID.randomUUID().toString();
        String currTrans = "Policy 001";
        System.out.println(businessKey);
        AppResponse response = workflowService.startNewWorkFlow(docType, props, businessKey, currTrans);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/dochistory/{businessKey}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WfTaskDTO>> getDocHistory(@PathVariable("businessKey") String businessKey) {
        List<WfTaskDTO> history = workflowService.getTransactionHistory(businessKey);
        return new ResponseEntity<>(history, HttpStatus.OK);

    }


    @PostMapping(path ="/complete-task",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void completeTask(@RequestBody AppRequest appRequest) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("wfTaskName", appRequest.getWfTaskName());
        String wfCurrTrans = appRequest.getWfCurrTrans();
        variables.put("wfCurrTrans", wfCurrTrans);
        variables.put("comment", appRequest.getComment());
        variables.put("content", appRequest.getComment());
        variables.put("approved", appRequest.getApproved());
        String docType = WorkflowDocType.HELLO_DOCUMENT.getFileName();
        workflowService.completeTask(docType, variables, wfCurrTrans, wfCurrTrans);
    }

    @PostMapping(value = { "reaassigneeTicket" },
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void reaassignUser(AssigneeForm assigneeForm) throws WorkflowException {
        String businessKey = assigneeForm.getBusinessKey();
        String username = assigneeForm.getUsername();
        workflowService.assignTask(businessKey, username);
    }

    @GetMapping(value = {"tickets"},
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AppResponse> getUserPolTrans(@RequestParam(value = "taskId", required = false) String taskId,
                                             @RequestParam(value = "transCode", required = false) String transCode,
                                             @RequestParam(value = "userName", required = false) String userName,
                                             @RequestParam(value = "page",defaultValue = "1") int page,
                                             @RequestParam(value = "limit",defaultValue = "10") int limit) {
        return workflowService.findSearchTickets(taskId, transCode, userName, page, limit);
    }

}
