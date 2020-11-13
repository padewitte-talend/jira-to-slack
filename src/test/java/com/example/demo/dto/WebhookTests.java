package com.example.demo.dto;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WebhookTests {
    

    @Test
	void webhookFromFile() {
        
        WebookDTO webHook = DtoBuilder.createFromFile(new File("/home/pdewitte/perso/jira-to-slack/src/test/java/com/example/demo/samples/webhook_doc_jira.json"), WebookDTO.class);
        System.out.println(webHook);

    }
}
