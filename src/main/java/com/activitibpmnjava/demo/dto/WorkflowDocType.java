package com.activitibpmnjava.demo.dto;

import java.util.Arrays;
import java.util.List;

public enum WorkflowDocType {
    HELLO_DOCUMENT("Say Hello Process","my-process"),
    USER_REGISTRATION_DOCUMENT("User Registration Process","userRegistrationProcess"),
    LEAVE_APPROVAL_DOCUMENT("Leave Approval Process","leave-approval-process"),
    QUOTE_DOCUMENT("General Quotation Process","quoteProcess"),
    TWITTER_DOCUMENT("Camunda Twitter Process","TwitterDemoProcess");
//    GEN_UW_DOCUMENT("General Underwrite Process","genUnderwriteProcess.bpmn20"),

    private String value;

    private String fileName;

    private WorkflowDocType(String value,String fileName) {
        this.value = value;
        this.fileName = fileName;
    }


    public String getValue() {
        return value;
    }

    public String getFileName() {
        return fileName;
    }

    public static List<WorkflowDocType> asList(){
        return Arrays.asList(WorkflowDocType.values());
    }

}
