package org.talend.jiraToSlack.dto;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WebhookTests {
    Logger logger = LoggerFactory.getLogger(WebhookTests.class);

    @Test
	void webhookFromFile() {
        
        WebHookDTO webHook = DtoBuilder.createFromFile(new File("/home/pdewitte/perso/jira-to-slack/src/test/java/org/talend/jiraToSlack/samples/webhook_doc_jira.json"), WebHookDTO.class);
        assertNotNull(webHook.getUser());
        logger.info(webHook.toString());

    }
}
