package org.talend.jiraToSlack.slackcli;

public class SlackCliException extends Exception{

    public SlackCliException(String message){
        super(message);
    }

    public SlackCliException(String message, Exception e){
        super(message, e);
    }
    
}
