package com.activitibpmnjava.demo.delegation;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.net.UnknownHostException;

/**
 * Publish content on Twitter.
 */
//@Component
public class TweetContentDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String content = (String) execution.getVariable("content");

        // Force a network error
        if ("network error".equals(content)) {
            try {
                throw new UnknownHostException("demo twitter account");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        String token = "YOUR TOKEN";
        String tokenSecret = "YOUR TOKEN SECRET";
        AccessToken accessToken = new AccessToken(token, tokenSecret);
        Twitter twitter = new TwitterFactory().getInstance();

        String consumerKey = "YOUR CONSUMER KEY";
        String consumerSecret = "YOUR CONSUMER SECRET";
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        twitter.setOAuthAccessToken(accessToken);

        try {
            twitter.updateStatus(content);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }
}
