package org.talend.jiraToSlack.dto;

import java.io.File;

import org.talend.jiraToSlack.dto.DtoBuilder;
import org.talend.jiraToSlack.dto.SimpleDTO;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SimpleDTOTests {

    @Test
	void simpleDto1() {
        System.out.println("testWebHook1");
        String sampleJson = "{\"name\": \"Pear yPhone 72\",\"category\": \"cellphone\",\"details\": {\"displayAspectRatio\": \"97:3\",\"audioConnector\": \"none\"}}";
        SimpleDTO webHook = DtoBuilder.createFromString(sampleJson, SimpleDTO.class);
        System.out.println(webHook);

    }
    
    @Test
	void simpleDto2() {
        String sampleJson = "{\"id\": 12, \"name\": \"Pear yPhone 72\",\"category\": \"cellphone\",\"details\": {\"displayAspectRatio\": \"97:3\",\"audioConnector\": \"none\"}}";
        SimpleDTO webHook = DtoBuilder.createFromString(sampleJson, SimpleDTO.class);
        System.out.println(webHook);

    }
    /*@Test
	void loadWebHookSample() {
        
        WebHookDTO webHook = DtoBuilder.createFromFile(new File("/home/pdewitte/perso/jira-to-slack/src/test/java/com/example/demo/samples/samples.json"));
        ToStringCreator toStringCreator = new ToStringCreator(webHook);
        System.out.println(toStringCreator.toString());

    }*/


}