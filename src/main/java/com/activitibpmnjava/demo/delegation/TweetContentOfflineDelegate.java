package com.activitibpmnjava.demo.delegation;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * Use this delegate instead of TweetContentDelegate, if you don't want to access Twitter, but just to do some sysout.
 */
//@Component("tweetAdapter")
public class TweetContentOfflineDelegate implements JavaDelegate {

    public void execute(DelegateExecution execution) {
        String content = (String) execution.getVariable("content");

        System.out.println("\n\n\n######\n\n\n");
        System.out.println("NOW WE WOULD TWEET: '" + content + "'");
        System.out.println("\n\n\n######\n\n\n");
    }

}
