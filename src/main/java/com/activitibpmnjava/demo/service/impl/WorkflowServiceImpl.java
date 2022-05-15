package com.activitibpmnjava.demo.service.impl;

import com.activitibpmnjava.demo.model.response.AppResponse;
import com.activitibpmnjava.demo.dto.WfTaskDTO;
import com.activitibpmnjava.demo.exception.WorkflowException;
import com.activitibpmnjava.demo.io.entity.ApplicationWorkflow;
import com.activitibpmnjava.demo.io.entity.QApplicationWorkflow;
import com.activitibpmnjava.demo.io.repo.AppWorkflowRepository;
import com.activitibpmnjava.demo.service.WorkflowService;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class WorkflowServiceImpl implements WorkflowService {

    private Logger logger = LoggerFactory.getLogger(WorkflowServiceImpl.class);

    @Autowired
    private AppWorkflowRepository workflowRepo;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Override
    public AppResponse startNewWorkFlow(String docType, Map<String, Object> variables, String businessKey, String currTrans) {
        ProcessInstance process = runtimeService.startProcessInstanceByKey(docType, businessKey, variables);
        Task task = taskService.createTaskQuery().processInstanceId(process.getId()).singleResult();
        task.setAssignee("amwangi10"); //userUtils.getCurrentUser().getUsername()
        task = taskService.saveTask(task);
        ApplicationWorkflow appWf = saveWorkFlow(docType, currTrans, task);
        return new ModelMapper().map(appWf, AppResponse.class);
    }


    @Override
    public void completeTask(String docType, Map<String, Object> variables, String businessKey, String currTrans){
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(businessKey).active().singleResult();
        if (pi == null) return;
        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).active().singleResult();
        logger.info("Completing taskId {}", task.getId());
        inactivateTasks(task.getId());
        taskService.setVariables(task.getId(), variables);
        taskService.complete(task.getId());
        Task newTask = taskService.createTaskQuery().processInstanceId(pi.getId()).active().singleResult();
        if (newTask != null) {
            newTask.setAssignee("amwangi10"); //userUtils.getCurrentUser().getUsername()
            newTask = taskService.saveTask(newTask);
            saveWorkFlow(docType, currTrans, newTask);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<WfTaskDTO> getTransactionHistory(String businessKey){
        List<WfTaskDTO> history = new ArrayList<>();

        List<HistoricProcessInstance> pi = historyService
                .createHistoricProcessInstanceQuery()
                .includeProcessVariables()
                .processInstanceBusinessKey(businessKey)
                .list();

        if (pi == null || pi.size()==0)
            return history;

        List<HistoricTaskInstance> hTasks = historyService
                .createHistoricTaskInstanceQuery()
                .includeTaskLocalVariables()
                .processInstanceBusinessKey(businessKey)
                .list();

        for (HistoricTaskInstance hti : hTasks) {
            WfTaskDTO wfTaskHistDTO = new WfTaskDTO();
            wfTaskHistDTO.setAssignee(hti.getAssignee());
            wfTaskHistDTO.setId(hti.getId());
            wfTaskHistDTO.setName(hti.getName());
            wfTaskHistDTO.setStartTime(hti.getStartTime());
            wfTaskHistDTO.setEndTime(hti.getEndTime());
            history.add(wfTaskHistDTO);
        }
        return history;
    }

    @Override
    public void assignTask(String businessKey,String username ) throws WorkflowException {
        System.out.print(" Task/Business Id: "+ businessKey);
        if(businessKey==null)
            throw new WorkflowException("Cannot assign to a null transaction....");
        if(StringUtils.isBlank(username)){
            throw new WorkflowException("Select User to assign...");
        }
        ProcessInstance pi =
                runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
        if(pi==null) return;
        System.out.println("Process Instance : "+pi.getId());
        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        System.out.println("Username "+username+" Assignee to task "+task.getAssignee()+" Description "+task.getDescription()+" Name "+task.getName()+" Owner "+task.getOwner()+" Category "+ task.getCategory());
        if(task.getAssignee()!=null) {
            if (task.getAssignee().equalsIgnoreCase(username)) {
                throw new WorkflowException("The task is already assigned to "+username.toUpperCase()+"....Select Another User");
            }
        }
        task.setAssignee(username);
        task = taskService.saveTask(task);
        if(workflowRepo.count(QApplicationWorkflow.applicationWorkflow.wfTaskId.eq(task.getId()))==1){
            System.out.print("Task Id :"+task.getId());
//            User user = userService.findByUserName(username);
            Optional<ApplicationWorkflow> wfDoc = workflowRepo
                    .findOne(QApplicationWorkflow.applicationWorkflow.wfTaskId.eq(task.getId()));
            wfDoc.ifPresentOrElse( wf -> {
//            String assignee =wf.getWfUserId().getName();
                String assignee =wf.getWfUserId();
                wf.setWfUserId(username);
                workflowRepo.save(wf);
            }, () -> System.out.println("Workflow is not available! ") );
        }
    }

    @Override
    public List<AppResponse> findSearchTickets(String taskId, String transCode, String userName, int page, int limit) {
        page =  page>0 ? page-1 : page;
        Pageable pageableRequest = PageRequest.of(page,limit);
        if (taskId == null) taskId = "";
        if (transCode == null) transCode = "";
        if (userName == null) userName = "";
        BooleanExpression pred;
        if (taskId.length() > 0)
            pred = QApplicationWorkflow.applicationWorkflow.wfTaskId.contains(taskId)
                    .and(QApplicationWorkflow.applicationWorkflow.wfActive.eq(true));
        else if (transCode.length() > 0)
            pred = QApplicationWorkflow.applicationWorkflow.wfCurrTrans.contains(transCode)
                    .and(QApplicationWorkflow.applicationWorkflow.wfActive.eq(true));
        else if (userName.length() > 0)
            pred = QApplicationWorkflow.applicationWorkflow.wfUserId.contains(userName)
                    .and(QApplicationWorkflow.applicationWorkflow.wfActive.eq(true));

        else pred = QApplicationWorkflow.applicationWorkflow.wfActive.eq(true);

        Page<ApplicationWorkflow> workflowPages= workflowRepo.findAll(pred, pageableRequest);
        List<ApplicationWorkflow> wfs = workflowPages.getContent();
        Type listType = new TypeToken<List<AppResponse>>() {}.getType();
        return  new ModelMapper().map(wfs, listType);
    }


    @Transactional(readOnly = true)
    public String getActiveTaskName(String docId) {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(docId).singleResult();
        if (pi == null) return null;
        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        return task.getName();
    }


    private ApplicationWorkflow saveWorkFlow(String docType, String currTrans, Task task) {
        ApplicationWorkflow appWf = new ApplicationWorkflow();
        appWf.setWfActive(true);
        appWf.setWfTaskName(task.getName());
        appWf.setWfActiveProcess(task.getTaskDefinitionKey());
        appWf.setWfTaskId(task.getId());
        appWf.setWfDocType(docType);
        appWf.setWfUserId(task.getAssignee());
        appWf.setWfCreatedDate(task.getCreateTime());
        appWf.setWfDueDate(task.getDueDate());
        appWf.setWfCurrTrans(task.getBusinessKey());
        return workflowRepo.save(appWf);
    }

    private void inactivateTasks(String taskId) {
        ApplicationWorkflow wf = workflowRepo.findByWfTaskId(taskId);
        if (wf != null) {
            wf.setWfActive(false);
            workflowRepo.save(wf);
        }
    }


}
