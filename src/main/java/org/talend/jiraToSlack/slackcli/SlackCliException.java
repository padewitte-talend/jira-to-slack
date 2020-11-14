package org.talend.jiraToSlack.slackcli;

public class SlackCliException extends Exception{

    /**
     *
     */
    private static final long serialVersionUID = -8442931980107347670L;

    public SlackCliException(String message) {
        super(message);
    }

    public SlackCliException(String message, Exception e){
        super(message, e);
    }
    
}
