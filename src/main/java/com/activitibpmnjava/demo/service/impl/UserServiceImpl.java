package com.activitibpmnjava.demo.service.impl;

import com.activitibpmnjava.demo.model.request.UserRegistrationRequest;
import com.activitibpmnjava.demo.service.UserService;
import org.activiti.api.process.runtime.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final String VAR_REGISTRATION_REQUEST = "registrationRequest";

    @Override
    public void saveRegistrationStatus(String userId, String comment, Boolean approved) {
        logger.info("We've moved on to the next step ....");
    }

    @Bean
    public Connector updateRegistrationStatusConnector() {
        return integrationContext -> {
            Map<String, Object> inBoundVariables = integrationContext.getInBoundVariables();

            if (logger.isDebugEnabled()) {
                logger.debug("inBoundVariables --> ", inBoundVariables);
            }

            UserRegistrationRequest registrationRequest = (UserRegistrationRequest) inBoundVariables.get(VAR_REGISTRATION_REQUEST);
            String comment = (String) inBoundVariables.get("comment");
            Boolean approved = (Boolean) inBoundVariables.get("approved");

            saveRegistrationStatus(registrationRequest.getUserId(), comment, approved);
            return integrationContext;
        };
    }

}
