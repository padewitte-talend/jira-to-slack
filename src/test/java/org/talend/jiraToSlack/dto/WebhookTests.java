package org.talend.jiraToSlack.dto;

import java.io.File;

import org.talend.jiraToSlack.dto.DtoBuilder;
import org.talend.jiraToSlack.dto.WebookDTO;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WebhookTests {
    

    @Test
	void webhookFromFile() {
        
        WebookDTO webHook = DtoBuilder.createFromFile(new File("/home/pdewitte/perso/jira-to-slack/src/test/java/org/talend/demo/samples/webhook_doc_jira.json"), WebookDTO.class);
        System.out.println(webHook);

    }
}
