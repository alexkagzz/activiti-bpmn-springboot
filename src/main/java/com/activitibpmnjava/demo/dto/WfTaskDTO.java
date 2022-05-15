package com.activitibpmnjava.demo.dto;

import lombok.Data;

import java.util.Date;

@Data
public class WfTaskDTO {
    private String assignee;
    private String id;
    private String name;
    private Date startTime;
    private Date endTime;

}
