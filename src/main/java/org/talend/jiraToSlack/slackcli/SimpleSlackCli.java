package org.talend.jiraToSlack.slackcli;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.conversations.ConversationsArchiveRequest;
import com.slack.api.methods.request.conversations.ConversationsCreateRequest;
import com.slack.api.methods.request.conversations.ConversationsInviteRequest;
import com.slack.api.methods.request.conversations.ConversationsListRequest;
import com.slack.api.methods.request.conversations.ConversationsRenameRequest;
import com.slack.api.methods.request.conversations.ConversationsSetTopicRequest;
import com.slack.api.methods.request.conversations.ConversationsSetTopicRequest.ConversationsSetTopicRequestBuilder;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.conversations.ConversationsArchiveResponse;
import com.slack.api.methods.response.conversations.ConversationsCreateResponse;
import com.slack.api.methods.response.conversations.ConversationsInviteResponse;
import com.slack.api.methods.response.conversations.ConversationsListResponse;
import com.slack.api.methods.response.conversations.ConversationsRenameResponse;
import com.slack.api.model.Conversation;
import com.slack.api.model.ConversationType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SimpleSlackCli {

    Logger logger = LoggerFactory.getLogger(SimpleSlackCli.class);

    MethodsClient methods;

    public SimpleSlackCli(@Value("${slack.bot.token}") String slackBotToken) {
        Slack slack = Slack.getInstance();
        methods = slack.methods(slackBotToken);
    }

    /**
     * https://api.slack.com/methods/chat.postMessage
     * @param message
     * @param channelId
     * @throws SlackCliException
     */
    public void sendSimpleMessage(String message, String channelId) throws SlackCliException {
        try {
            // Use a channel ID `C1234567` is preferable
            ChatPostMessageRequest request = ChatPostMessageRequest.builder().channel(channelId).text(message).build();
            // Get a response as a Java object

            ChatPostMessageResponse response = methods.chatPostMessage(request);
            logger.debug(response.toString());
        } catch (IOException | SlackApiException e) {
            throw new SlackCliException("Unable to send message to channel: "+channelId+". Message was: "+message, e);
        }
    }

    /**
     * https://api.slack.com/methods/conversations.create
     * @param channelName
     * @param users
     * @return
     * @throws SlackCliException
     */
    public Conversation createChannel(String channelName, String issueUrl, List<String> users) throws SlackCliException {
        if (users == null || users.size() < 1) {
            throw new SlackCliException("One user at least should be in the channel");
        }

        try {
            // Channel name should not contains any upper case letters
            ConversationsCreateRequest request = ConversationsCreateRequest.builder().name(channelName.toLowerCase())
                    .isPrivate(true).build();
            ConversationsCreateResponse response = methods.conversationsCreate(request);
            if (!response.isOk()) {
                throw new SlackCliException("Unable to create channel " + response.getError());
            }
            Conversation channel = response.getChannel();
            ConversationsSetTopicRequest setTopicRequest = ConversationsSetTopicRequest.builder().channel(channel.getId()).topic(issueUrl).build();
            methods.conversationsSetTopic(setTopicRequest);

            return inviteUsers(channel.getId(), users);

        } catch (IOException | SlackApiException e) {
            throw new SlackCliException("Unable to send message to channel", e);
        }
    }

    /**
     * https://api.slack.com/methods/conversations.invite
     * @param channelId
     * @param users
     * @return
     * @throws SlackCliException
     */
    public Conversation inviteUsers(String channelId, List<String> users) throws SlackCliException {
        try {
            ConversationsInviteRequest inviteRequest = ConversationsInviteRequest.builder().channel(channelId)
                    .users(users).build();
            ConversationsInviteResponse inviteResponse = methods.conversationsInvite(inviteRequest);
            if (!inviteResponse.isOk()) {
                throw new SlackCliException("Unable to create channel " + inviteResponse.getErrors());
            }
            return inviteResponse.getChannel();
        } catch (IOException | SlackApiException e) {
            throw new SlackCliException("Unable to send message to channel", e);
        }
    }




    private String iterateOverChannelCursor(Map<String, String> channels, ConversationsListResponse response){
        if (response.isOk()) {
            logger.debug(response.toString());
            response.getChannels().stream().forEach(chan -> channels.put(chan.getName().toLowerCase(), chan.getId()));
            if(!StringUtils.isEmpty(response.getResponseMetadata().getNextCursor())){
                return response.getResponseMetadata().getNextCursor();
            }
            return null;
        }else{
            throw new SlackCliException("Unable to iterate over channel listing cursor " + response.getError());
        }
    }

    public Map<String, String> loadChannelList()
            throws SlackCliException {

        try {
            Map<String, String> channels = new HashMap<>();

            String curCursor = null;
            do{
                ConversationsListRequest request = ConversationsListRequest.builder().limit(1000).types(List.of(ConversationType.PRIVATE_CHANNEL)).excludeArchived(false).cursor(curCursor).build();
                ConversationsListResponse response = methods.conversationsList(request);
                curCursor = iterateOverChannelCursor(channels, response);
            } while (curCursor != null);

            logger.debug("iteration over");
            return channels;

        } catch (IOException | SlackApiException e) {
            throw new SlackCliException("Unable to send message to channel", e);
        }
    }


    /**
     * https://api.slack.com/methods/conversations.rename
     * @param channelId
     * @param channelNameWillBe
     * @param users
     * @return
     * @throws SlackCliException
     */
    public Conversation renameChannelAndAdd(String channelId, String channelNameWillBe, List<String> users)
            throws SlackCliException {

        try {
            ConversationsRenameRequest request = ConversationsRenameRequest.builder().channel(channelId)
                    .name(channelNameWillBe.toLowerCase()).build();
            ConversationsRenameResponse response = methods.conversationsRename(request);
            if (!response.isOk()) {
                throw new SlackCliException("Unable to rename channel " + response.getError());
            }
            Conversation channel = response.getChannel();
            if (users == null || users.isEmpty()) {
                return response.getChannel();
            } else {
                return inviteUsers(channel.getId(), users);
            }

        } catch (IOException | SlackApiException e) {
            throw new SlackCliException("Unable to send message to channel", e);
        }
    }

    /**
     * Archive a conversation
     * https://api.slack.com/methods/conversations.archive
     * @param channelId
     * @throws SlackCliException
     */
    public void archiveConversation(String channelId) throws SlackCliException {

        try {
            ConversationsArchiveRequest request = ConversationsArchiveRequest.builder().channel(channelId).build();
            ConversationsArchiveResponse response = methods.conversationsArchive(request);
            if (!response.isOk()) {
                throw new SlackCliException("Unable to close channel "+channelId + " : " + response.getError());
            }

        } catch (IOException | SlackApiException e) {
            throw new SlackCliException("Unable to send message to channel", e);
        }
    }

    /**
     * Cursor are not managed
     * https://api.slack.com/methods/conversations.list
     * @return
     * @throws SlackCliException
     */
    public List<Conversation> listBotPrivateChannels() throws SlackCliException {

        try {
            ConversationsListRequest request = ConversationsListRequest.builder().types(List.of(ConversationType.PRIVATE_CHANNEL)).limit(1000).build();
            ConversationsListResponse response = methods.conversationsList(request);
            if (!response.isOk()) {
                throw new SlackCliException("Unable to list channel " + response.getError());
            }
            return response.getChannels();

        } catch (IOException | SlackApiException e) {
            throw new SlackCliException("Unable to send message to channel", e);
        }
    }
}
