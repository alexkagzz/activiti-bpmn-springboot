package com.activitibpmnjava.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRepresentation {
    private String id;
    private String name;
    private String processInstanceId;

}
