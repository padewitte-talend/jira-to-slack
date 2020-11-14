package org.talend.jiraToSlack;

import org.springframework.web.bind.annotation.RestController;

import org.talend.jiraToSlack.dto.WebookDTO;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class ServerController {

	@RequestMapping("/")
	public String index() {
		return "Simple application converting JIRA webhooks to slack actions";
	}

	@PostMapping("/jira-webhook")
	public String jiraWebhook(@RequestBody WebookDTO webookDTO){
		return webookDTO.toString().substring(5, 20);
	}

}