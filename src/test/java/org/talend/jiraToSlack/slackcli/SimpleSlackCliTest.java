package org.talend.jiraToSlack.slackcli;

import static org.junit.jupiter.api.Assertions.fail;
import java.util.List;
import java.util.Map;

import com.slack.api.model.Conversation;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class SimpleSlackCliTest {
    Logger logger = LoggerFactory.getLogger(SimpleSlackCliTest.class);
    String channelId = "C01EXT5AK0U";
    String channelName = "une-bidouille";
    String message = ":wave: Hi from a bot written in Java!";

    @Autowired
    SimpleSlackCli simpleSlackCli;

    @Test
    @Order(1)
    void sendDummyMessage() {

        logger.info("sending a message");
        try {
            simpleSlackCli.sendSimpleMessage(message, channelId);
        } catch (SlackCliException e) {
            logger.info("Unable to send simple message", e);
            fail(e.getMessage());
        }

    }

    @Test
    @Order(1)
    void sendOtherDummyMessage() {

        logger.info("sending another message");
        try {
            simpleSlackCli.sendSimpleMessage(message, channelName);
        } catch (SlackCliException e) {
            logger.info("Unable to send other simple message", e);
            fail(e.getMessage());
        }

    }

    @Test
    @Order(2)
    void createRandomChannel() {
        String generatedString = RandomString.make(10);
        List<String> users = List.of("U01EXT4T5HA");
        logger.info("Creating a random channel");
        
        try {

            Conversation channel = simpleSlackCli.createChannel(generatedString, "dummy", users);
            logger.debug(channel.getId());
        } catch (SlackCliException e) {
            logger.info("Unable to create channel", e);
            fail(e.getMessage());
        }

    }

    @Test
    @Order(3)
    void renameRandomChannel() {
        String generatedStringWas = RandomString.make(10);
        String generatedStringTo = RandomString.make(10);
        List<String> users = List.of("U01EXT4T5HA");

        logger.info("Renaming a random channel");
        try {

            Conversation channel = simpleSlackCli.createChannel(generatedStringWas, "dummy", users);
            simpleSlackCli.renameChannelAndAdd(channel.getId(), generatedStringTo, null);
            logger.debug(channel.getId());

        } catch (SlackCliException e) {
            logger.info("Unable to rename channel", e);
            fail(e.getMessage());
        }
    }

    @Test
    @Order(4)
    void findChannelByName() {
        
        logger.info("Listing all channel");
        try {

            Map<String, String> channelId = simpleSlackCli.loadChannelList();
            logger.debug("List of channels {}", channelId);

        } catch (SlackCliException e) {
            logger.info("Unable to find channel", e);
            fail(e.getMessage());
        }
    }

    @Test
    @Order(Integer.MAX_VALUE - 1)
    void listAndArchiveAllPrivateConversation() {
        try {

            List<Conversation> channel = simpleSlackCli.listBotPrivateChannels();
            channel.stream().filter(conv -> !conv.isArchived()).forEach(conv -> {
                try {
                    logger.info("Closing " + conv.getName());
                    simpleSlackCli.archiveConversation(conv.getId());
                } catch (SlackCliException s) {
                    logger.info("Failed to close " + conv.getName(), s);
                    fail(s.getCause());
                }
            });
        } catch (SlackCliException e) {
            logger.info("Failed to list private channels", e);
            fail(e.getMessage());
        }
    }


  

}
