package org.talend.jiraToSlack.slackcli;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.talend.jiraToSlack.slackcli.SimpleSlackCli;
import org.talend.jiraToSlack.slackcli.SlackCliException;
import com.slack.api.model.Conversation;

import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class SimpleSlackCliTest {
    String channelId = "C01EXT5AK0U";
    String message = ":wave: Hi from a bot written in Java!";

    @Autowired
    SimpleSlackCli simpleSlackCli;

    @Test
    @Order(1)
    void sendDummyMessage() {

        System.out.println("sending a message");
        try {
            simpleSlackCli.sendSimpleMessage(message, channelId);
        } catch (SlackCliException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    @Order(2)
    void createRandomChannel() {
        String generatedString = RandomString.make(10);
        List<String> users = List.of("U01EXT4T5HA");

        System.out.println("Creating a random channel");
        try {

            Conversation channel = simpleSlackCli.createChannel(generatedString, users);
            System.out.println(channel.getId());
        } catch (SlackCliException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    @Order(3)
    void renameRandomChannel() {
        String generatedStringWas = RandomString.make(10);
        String generatedStringTo = RandomString.make(10);
        List<String> users = List.of("U01EXT4T5HA");

        System.out.println("Creating a random channel");
        try {

            Conversation channel = simpleSlackCli.createChannel(generatedStringWas, users);
            simpleSlackCli.renameChannelAndAdd(channel.getId(), generatedStringTo, null);
            System.out.println(channel.getId());

        } catch (SlackCliException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @Order(Integer.MAX_VALUE-1)
    void  listAndArchiveAllPrivateConversation() {
        try {

            List<Conversation> channel = simpleSlackCli.listBotPrivateChannels();
            channel.stream().filter(conv -> !conv.isArchived() ).forEach(conv -> {
                try {
                    System.out.println("Closing " + conv.getName());
                    simpleSlackCli.archiveConversation(conv.getId());
                } catch (SlackCliException s) {
                    System.out.println("Failed to close " + conv.getName());
                    s.printStackTrace();
                    fail(s.getCause());
                }
            });
        } catch (SlackCliException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
