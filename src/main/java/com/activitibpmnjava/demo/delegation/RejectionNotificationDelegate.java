package com.activitibpmnjava.demo.delegation;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * Rejection is just done via a sysout. You could, for example, implement sending mail to the author here.
 * Use your own Mail mechanisms for this or use your application server features.
 */
//@Component("emailAdapter")
public class RejectionNotificationDelegate implements JavaDelegate {

    public void execute(DelegateExecution execution) {
        String content = (String) execution.getVariable("content");
        String comments = (String) execution.getVariable("comments");

        System.out.println("Hi!\n\n"
                + "Unfortunately your tweet has been rejected.\n\n"
                + "Original content: " + content + "\n\n"
                + "Comment: " + comments + "\n\n"
                + "Sorry, please try with better content the next time :-)");
    }

}
