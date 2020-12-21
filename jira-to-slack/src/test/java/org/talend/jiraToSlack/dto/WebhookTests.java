package org.talend.jiraToSlack.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        assertNotNull(webHook.getChangelog());
        assertNotNull(webHook.getComment());
        assertNotNull(webHook.getId());
        assertNotNull(webHook.getIssue());
        assertNotNull(webHook.getIssue().getFields());
        assertNotNull(webHook.getTimestamp());
        assertNotNull(webHook.getUser());
        assertNotNull(webHook.getUser().getName());
        assertEquals(webHook.getWebhookEvent(),"jira:issue_updated");

        logger.info(webHook.toString());

    }
}
