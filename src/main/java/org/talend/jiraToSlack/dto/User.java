package org.talend.jiraToSlack.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    String name;
    String key;
    String displayName;
    String emailAddress;

}