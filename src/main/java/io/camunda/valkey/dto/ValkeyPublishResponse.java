package io.camunda.valkey.dto;

public record ValkeyPublishResponse(
        String channel,
        String message,
        long subscribers
) {}