package com.activitibpmnjava.demo.service;

import com.activitibpmnjava.demo.model.response.AppResponse;
import com.activitibpmnjava.demo.dto.WfTaskDTO;
import com.activitibpmnjava.demo.exception.WorkflowException;

import java.util.List;
import java.util.Map;

public interface WorkflowService {

    AppResponse startNewWorkFlow(String docType, Map<String, Object> variables, String businessKey, String currTrans);

    void  completeTask(String docType, Map<String, Object> variables, String businessKey, String currTrans);

    String getActiveTaskName(String docId);

    List<WfTaskDTO> getTransactionHistory(String businessKey);

    void assignTask(String businessKey,String username ) throws WorkflowException;

    public List<AppResponse> findSearchTickets(String taskId, String transCode, String userName, int page, int limit);
}
