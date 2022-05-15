package com.activitibpmnjava.demo.model.request;

import lombok.Data;

@Data
public class AppRequest {
    private String wfTaskName;
    private String wfTaskId;
    private String wfActiveProcess;
    private String wfDocType;
    private String wfCurrTrans;
    private String comment;
    private Boolean approved;
}
