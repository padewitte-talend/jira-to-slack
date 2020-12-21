package org.talend.jiraToSlack.dto;

import static org.junit.jupiter.api.Assertions.assertNotNull;


import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SimpleDTOTests {
    Logger logger = LoggerFactory.getLogger(SimpleDTOTests.class);

    @Test
	void simpleDto1() {
        
        String sampleJson = "{\"name\": \"Pear yPhone 72\",\"category\": \"cellphone\",\"details\": {\"displayAspectRatio\": \"97:3\",\"audioConnector\": \"none\"}}";
        SimpleDTO webHook = DtoBuilder.createFromString(sampleJson, SimpleDTO.class);
        assertNotNull(webHook.getDetails());
        logger.info(webHook.toString());

    }
    
    @Test
	void simpleDto2() {
        String sampleJson = "{\"id\": 12, \"name\": \"Pear yPhone 72\",\"category\": \"cellphone\",\"details\": {\"displayAspectRatio\": \"97:3\",\"audioConnector\": \"none\"}}";
        SimpleDTO webHook = DtoBuilder.createFromString(sampleJson, SimpleDTO.class);
        assertNotNull(webHook.getName());
        logger.info(webHook.toString());

    }

}