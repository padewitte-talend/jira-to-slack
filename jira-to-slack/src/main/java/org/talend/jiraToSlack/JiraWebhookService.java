package org.talend.jiraToSlack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.slack.api.model.Conversation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.talend.jiraToSlack.dto.WebHookDTO;
import org.talend.jiraToSlack.slackcli.SimpleSlackCli;
import org.talend.jiraToSlack.slackcli.SlackCliException;

@Service
public class JiraWebhookService {
    Logger logger = LoggerFactory.getLogger(JiraWebhookService.class);

    private static Map<String, String> CHANNELS_IDS = null;

    @PostConstruct
    public void init() {
        // logger.info("Loading channel ids async");
        // loadChannelIds();
        logger.info("Post constructing JiraWebhookService");
        logger.info(welcomeMessage);
    }

    @Autowired
    private SimpleSlackCli simpleSlackCli;

    @Autowired
    private Environment environment;

    @Value("${welcome.message}")
    private String welcomeMessage;

    @Value("${managed.projects}")
    private String[] managedProjects;

    @Value("${browser.url.prefix}")
    private String browserUrlPrefix;

    public void processWebhook(WebHookDTO webookDTO) {
        try {
            /*
             * - jira:issue_created - jira:issue_deleted - jira:issue_updated -
             * comment_created - comment_updated - comment_deleted - issue_property_set -
             * issue_property_delete
             */
            String issueId = webookDTO.getIssue().getKey();
            String project = issueId.split("-")[0].toLowerCase();
            if (Arrays.stream(managedProjects).anyMatch(managedProject -> project.equalsIgnoreCase(managedProject))) {
                String channelName = issueId + "".toLowerCase();
                switch (webookDTO.getWebhookEvent()) {
                    case "jira:issue_created":

                        issueCreated(channelName, webookDTO);

                        break;
                    case "jira:issue_deleted":
                        closeChannel(getChannelId(channelName), webookDTO);
                        break;
                    case "jira:issue_updated":
                        issueUpdated(channelName, webookDTO);
                        break;
                    /*
                     * case "comment_created": commentCreated(channelName, webookDTO); break; case
                     * "comment_updated": commentUpdated(channelName, webookDTO); break; case
                     * "comment_deleted": commentDeteled(channelName, webookDTO); break;
                     
                    case "issue_property_set":
                        issuePropertySet(channelName, webookDTO);
                        break;
                    case "issue_property_deleted":
                        issuePropertyDeleted(channelName, webookDTO);
                        break;*/
                    default:
                        break;
                }
            }else{
                logger.info("Project {} is not managed", project);
            }
        } catch (SlackCliException e) {
            logger.error("Unable to process webhook {}", e.getMessage());
            logger.debug("Cause of error",e);
        }

    }

    private void issueCreated(String channelName, WebHookDTO webookDTO) throws SlackCliException {
        String project = webookDTO.getIssue().getKey().split("-")[0].toLowerCase();
        String url = browserUrlPrefix + webookDTO.getIssue().getKey();

        List<String> usersForProject = (List<String>) environment.getProperty(project + ".initial.usersid.list",
                List.class);
        Conversation conversationChannel = simpleSlackCli.createChannel(channelName, url, usersForProject);
        simpleSlackCli.sendSimpleMessage(
                String.format("%s\n%s\nPriority: %s\nReporter: %s\nLink: %s", webookDTO.getIssue().getFields().getSummary(),webookDTO.getIssue().getFields().getDescription(),
                        webookDTO.getIssue().getFields().getPriority().getName(), webookDTO.getUser().getDisplayName(),url
                        ),
                channelName);

        // TODO Change topic of channel
        // Share priority of message
    }

    private void closeChannel(String channelId, WebHookDTO webookDTO) throws SlackCliException {
        String issueId = webookDTO.getIssue().getKey();
        String deleter = webookDTO.getUser().getDisplayName();
        simpleSlackCli.sendSimpleMessage(String.format("Issue %s was deleted or closed by %s", issueId, deleter),
        channelId);
        simpleSlackCli.archiveConversation(channelId);
    }

    private void issueUpdated(String channelName, WebHookDTO webookDTO) throws SlackCliException {
        logger.debug("issueUpdated partialy implemented yet. Channel name is {}", channelName);
        
        //Handling project change
        webookDTO.getChangelog().getItems().stream().filter(item -> item.getField().equalsIgnoreCase("key"))
                .findFirst().ifPresent(item -> {
                    logger.debug("Renaming channel as key of issue moved. {}", item.toString());
                    String oldChannelName = item.getFromString().toLowerCase() + "-internal";
                    String project = webookDTO.getIssue().getKey().split("-")[0].toLowerCase();
                    List<String> usersForProject = (List<String>) environment
                            .getProperty(project + ".initial.usersid.list", List.class);
                    logger.debug("Renaming from {} to {}", oldChannelName, channelName);
                    String oldChannelId = getChannelId(oldChannelName);
                    if(StringUtils.isEmpty(oldChannelId)){
                        issueCreated(channelName, webookDTO);
                    }else{
                        simpleSlackCli.renameChannelAndAdd(oldChannelId, channelName, usersForProject);
                    }
                });
        //Handling state move to done
        webookDTO.getChangelog().getItems().stream().filter(item -> item.getField().equalsIgnoreCase("status"))
                .findFirst().ifPresent(item -> {
                    logger.debug("Status changed for item {}", item.toString());
                    if ("done".equalsIgnoreCase(item.getToString())) {
                        closeChannel(getChannelId(channelName), webookDTO);
                    }else{
                        simpleSlackCli.sendSimpleMessage(String.format("Status changed to: %s", item.getToString()), getChannelId(channelName));
                    }
                });

        //Handling priority change
        webookDTO.getChangelog().getItems().stream().filter(item -> item.getField().equalsIgnoreCase("priority"))
                .findFirst().ifPresent(item -> {
                    logger.debug("Priority changed for item {}", item.toString());
                    simpleSlackCli.sendSimpleMessage(String.format("Priority changed from %s to %s by %s",
                            item.getFromString(), item.getToString(), webookDTO.getUser().getDisplayName()),
                            channelName);
                });
    }

    /*
    private void commentCreated(String channelName, WebHookDTO webookDTO) throws SlackCliException {
        String commentCreator = webookDTO.getComment().getAuthor().getName();
        String commentBody = webookDTO.getComment().getBody();
        String self = webookDTO.getComment().getSelf();
        simpleSlackCli.sendSimpleMessage(
                String.format("%s added the following comment:\n %s \n See: %s", commentCreator, commentBody, self),
                channelName);
    }

    private void commentUpdated(String channelName, WebHookDTO webookDTO) throws SlackCliException {
        String commentUpdator = webookDTO.getComment().getUpdateAuthor().getName();
        String commentBody = webookDTO.getComment().getBody();
        String self = webookDTO.getComment().getSelf();
        simpleSlackCli.sendSimpleMessage(
                String.format("%s update the following comment:\n %s \n See: %s", commentUpdator, commentBody, self),
                channelName);
    }

    private void commentDeteled(String channelName, WebHookDTO webookDTO) throws SlackCliException {
        String commentBody = webookDTO.getComment().getBody();
        String commentUpdator = webookDTO.getComment().getUpdateAuthor().getName();
        simpleSlackCli.sendSimpleMessage(
                String.format("%s deleted the following comment:\n %s", commentUpdator, commentBody), channelName);
    }

    private void issuePropertySet(String channelName, WebHookDTO webookDTO) throws SlackCliException {
        List<Item> items = webookDTO.getChangelog().getItems();
        for (Item item : items) {
            String field = item.getField();
            String fromvalue = item.getFromString();
            String toValue = item.toString();
            simpleSlackCli.sendSimpleMessage(
                    String.format("Field %s got now value: %s\nWas: %s", field, fromvalue, toValue), channelName);
        }
    }

    private void issuePropertyDeleted(String channelName, WebHookDTO webookDTO) throws SlackCliException {
        List<Item> items = webookDTO.getChangelog().getItems();
        for (Item item : items) {
            String field = item.getField();
            String fromvalue = item.getFromString();
            simpleSlackCli.sendSimpleMessage(String.format("Field %s is now deleted.\nValue was: %s", field, fromvalue),
                    channelName);
        }
    }*/

    private void loadChannelIds(boolean forced) {
        if (forced || CHANNELS_IDS == null) {
            CHANNELS_IDS = simpleSlackCli.loadChannelList();
        }
    }

    public void loadChannelIds() {
        loadChannelIds(false);
        logger.info("Loading channel ids async done. {} channels found", CHANNELS_IDS.size());
    }

    public String getChannelId(String channelName) {
        String ret = null;
        loadChannelIds(false);
        ret = CHANNELS_IDS.get(channelName.toLowerCase());
        if (ret == null) {
            loadChannelIds(true);
            ret = CHANNELS_IDS.get(channelName.toLowerCase());
        }
        logger.debug("Found id:{} for channel name: {}", ret, channelName);
        return ret;
    }

}
