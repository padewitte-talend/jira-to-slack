package org.talend.jiraToSlack;

import org.springframework.web.bind.annotation.RestController;

import org.talend.jiraToSlack.dto.WebHookDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class JiraWebhookServerController {

	@Autowired
	private JiraWebhookService jiraWebhookService;

	@RequestMapping("/")
	public String index() {
		return "Hello I am there. My only endpoint is /jira-webhook!";
	}

	@PostMapping("/jira-webhook")
	public String jiraWebhook(@RequestBody WebHookDTO webookDTO){
		jiraWebhookService.processWebhook(webookDTO);
		return webookDTO.toString().substring(5, 20);
	}

}