package org.talend.jiraToSlack.slackcli;

import java.io.IOException;
import java.util.List;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.conversations.ConversationsArchiveRequest;
import com.slack.api.methods.request.conversations.ConversationsCreateRequest;
import com.slack.api.methods.request.conversations.ConversationsInfoRequest;
import com.slack.api.methods.request.conversations.ConversationsInviteRequest;
import com.slack.api.methods.request.conversations.ConversationsListRequest;
import com.slack.api.methods.request.conversations.ConversationsRenameRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.conversations.ConversationsArchiveResponse;
import com.slack.api.methods.response.conversations.ConversationsCreateResponse;
import com.slack.api.methods.response.conversations.ConversationsInviteResponse;
import com.slack.api.methods.response.conversations.ConversationsListResponse;
import com.slack.api.methods.response.conversations.ConversationsRenameResponse;
import com.slack.api.model.Conversation;
import com.slack.api.model.ConversationType;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SimpleSlackCli {

    MethodsClient methods;

    public SimpleSlackCli(@Value("${slack.bot.token}") String botSigninToken) {
        Slack slack = Slack.getInstance();
        methods = slack.methods(botSigninToken);
    }

    public void sendSimpleMessage(String message, String channelId) throws SlackCliException {
        try {
            // Use a channel ID `C1234567` is preferrable
            ChatPostMessageRequest request = ChatPostMessageRequest.builder().channel(channelId).text(message).build();
            // Get a response as a Java object

            ChatPostMessageResponse response = methods.chatPostMessage(request);
            System.out.println(response);
        } catch (IOException | SlackApiException e) {
            throw new SlackCliException("Unable to send message to channel", e);
        }
    }

    public void getChannels() {
        ConversationsInfoRequest request = ConversationsInfoRequest.builder().channel("C01EXT5AK0U").build();
    }

    public Conversation createChannel(String channelName, List<String> users) throws SlackCliException {
        if (users == null && users.size() < 1) {
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

            return inviteUsers(channel.getId(), users);

        } catch (IOException | SlackApiException e) {
            throw new SlackCliException("Unable to send message to channel", e);
        }
    }

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

    public void archiveConversation(String channelId) throws SlackCliException {

        try {
            ConversationsArchiveRequest request = ConversationsArchiveRequest.builder().channel(channelId).build();
            ConversationsArchiveResponse response = methods.conversationsArchive(request);
            if (!response.isOk()) {
                throw new SlackCliException("Unable to close channel " + response.getError());
            }

        } catch (IOException | SlackApiException e) {
            throw new SlackCliException("Unable to send message to channel", e);
        }
    }

    @Deprecated
    /**
     * Cursor are not managed
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