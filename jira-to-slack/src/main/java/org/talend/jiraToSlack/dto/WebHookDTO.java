package org.talend.jiraToSlack.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

/**
 * Java structure mapping the message send from JIRA server when JIRA ticket get created or modified.
 * @See 
 */
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebHookDTO {
    private Integer id;
    private Date timestamp;
    private Issue issue;
    private User user;
    private Changelog changelog;
    private Comment comment;
    /**
     * Possible values:
     *  - jira:issue_created
     *  - jira:issue_deleted
     *  - jira:issue_updated
     *  - comment_created
     *  - comment_updated
     *  - comment_deleted
     *  - issue_property_set
     *  - issue_property_deleted
     */
    String webhookEvent;

    /**
     * issue_moved
     */
    String issue_event_type_name;



}

