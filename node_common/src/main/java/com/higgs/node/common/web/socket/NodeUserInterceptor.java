package com.higgs.node.common.web.socket;

import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import java.util.Optional;

public class NodeUserInterceptor implements ChannelInterceptor {
    private static final String HEADER_NODE_NAME = "NODE_NAME";

    @Override
    public Message<?> preSend(@NotNull final Message<?> message, @NotNull final MessageChannel channel) {
        Optional.of(StompHeaderAccessor.wrap(message))
                .filter(it -> StompCommand.CONNECT.equals(it.getCommand()))
                .ifPresent(it -> it.setUser(() -> it.getMessageHeaders().get(NodeUserInterceptor.HEADER_NODE_NAME, String.class)));
        return message;
    }
}
