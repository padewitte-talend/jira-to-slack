package org.talend.jiraToSlack.dto;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

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
public class WebookDTO {
    Integer id;
    String key;

    Issue issue;

    User user;

    JsonNode changelog;


}

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class Issue {
    String id;
    String key;
    Fields fields;

}

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class Fields {
    String summary;
    Date created;
    String description;
    String priority;
}

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class User {
    String name;
    String key;
    String displayName;

}