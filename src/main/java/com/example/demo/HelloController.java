package com.example.demo;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.annotation.HttpMethodConstraint;

import com.example.demo.dto.WebookDTO;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class HelloController {

	@RequestMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}

	@PostMapping("/jira-webhook")
	public String jiraWebhook(@RequestBody WebookDTO webookDTO){
		return webookDTO.toString().substring(5, 20);
	}

}