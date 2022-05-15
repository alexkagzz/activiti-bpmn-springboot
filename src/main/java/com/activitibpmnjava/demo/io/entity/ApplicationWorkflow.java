package com.activitibpmnjava.demo.io.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "app_workflows")
public class ApplicationWorkflow {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long wfId;

    @Column(nullable = false)
    private String wfTaskName;

    @Column(nullable = false)
    private String wfTaskId;

    @Column(nullable = false)
    private Boolean wfActive;

    @Column(nullable = false)
    private String wfActiveProcess;

    @Column(nullable = false)
    private String wfDocType;

    @Column(nullable = false)
    private String wfUserId;

    @Column(nullable = false)
    private String wfCurrTrans;

    @Column(nullable = false)
    private Date wfCreatedDate;

    private Date wfDueDate;

}
