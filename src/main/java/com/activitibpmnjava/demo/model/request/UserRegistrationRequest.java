package com.activitibpmnjava.demo.model.request;

import lombok.*;

@Data
@ToString
public class UserRegistrationRequest {
    private String userId;
    private String approverId;
    private String nationalIdNo;
}
