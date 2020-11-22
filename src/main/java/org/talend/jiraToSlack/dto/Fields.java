package org.talend.jiraToSlack.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

/**
 * {
            "summary": "I feel the need for speed",
            "created": "2009-12-16T23:46:10.612-0600",
            "description": "I feel the need for speed",
            "labels": [
                "UI",
                "dialogue",
                "move"
            ],
            "priority": "Minor"
        }
 */
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public  class Fields {
    /** "I feel the need for speed" */
    private String summary;

    private String description;

    private Project project;

    private Priority priority;






}