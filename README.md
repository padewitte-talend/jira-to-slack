# JIRA-TO-SLACK

## What is this bot
A bot accepting jira webhook calls and transforming them in appropriate slack actions.

Currently, the set of supported features are:
- JIRA issue created : create a private channel name "<JIRA-ID>-internal" with the initial slack user in it (configured in application.properties overloaded by flux config).   
- JIRA issue deleted or moved to done: archive the channel
- JIRA issue renamed : rename the channel
- JIRA status or priority changed: post a message in channel  


## How to configure it ?
Like any Spring boot application all parameters are set with their default values in application.properies.

`managed.projects` list contains all projected configured. If you want to let bot managed TMC and TICO value should be set to `TMC,TICO`.
Every project managed a prop should have an associated property with user to add at channel creation. To find this id copy a link to the user profile. The string after last `/` is th euser id.
To configure TMC default users the property could be set as below.
`tmc.initial.usersid.list=UT2PAQJP7`

This bot is deployed with flux in AT (to be publicly accessible). To set extra configuration, you can PR [Flux url]


## Building it
Spring boot power, launch well knowed mvn commands and you have the application build.

The jenkins file `jenkinsfiles/Jenkinsfile-jira-to-slack.groovy` provide an overview of building steos.
The jenkins server used is [TODO]()


```
$ mvn -T 1C -Dcheckstyle.skip -Dpmd.skip=true  -Dmaven.test.skip=true spring-boot:build-image

# Pushing the image to a local repository (usefull to host on a local K8S cluster)
$ docker tag artifactory.datapwn.com/tlnd-docker-dev/talend/management-console/tmc-qa/talend-slack-bots/jirabot:0.0.3 localhost:5000/tlnd-docker-dev/talend/management-console/tmc-qa/talend-slack-bots/jirabot:0.0.3
$ docker push  localhost:5000/tlnd-docker-dev/talend/management-console/tmc-qa/talend-slack-bots/jirabot:0.0.3

```

## Test the webhook

Some JUnit test are here to test webhook content (looks sensitive to JIRA server version) and slack cli. Configure a webhook on https://jira-stage.talendforge.org/ and use a slack trial workspace.

```
curl --quiet \
--method POST \
--header 'content-type: application/json' \
--body-file '/home/pdewitte/perso/jira-to-slack/src/test/java/org/talend/jiraToSlack/samples/webhook_doc_jira.json' \
--output-document \
- http://localhost:8080/jira-webhook
```


## Contribute
This bot is Talend property feel free to push any evolution. The only rules, do not break was is already working.

## Support
Do not be ashamed to git blame to get some support.
