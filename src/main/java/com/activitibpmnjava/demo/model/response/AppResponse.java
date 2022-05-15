package com.activitibpmnjava.demo.model.response;

import lombok.Data;

import java.util.Date;

@Data
public class AppResponse {
    private Long wfId;
    private String wfTaskName;
    private String wfTaskId;
    private Boolean wfActive;
    private String wfActiveProcess;
    private String wfDocType;
    private String wfUserId;
    private String wfCurrTrans;
    private Date wfCreatedDate;
    private Date wfDueDate;
}
