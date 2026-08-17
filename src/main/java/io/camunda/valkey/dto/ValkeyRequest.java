package io.camunda.valkey.dto;


import io.camunda.connector.generator.java.annotation.TemplateProperty;

import java.util.List;
public record ValkeyRequest(

        @TemplateProperty(
                label = "Connection Type",
                description = "Valkey connection type.",
                defaultValue = "STANDALONE"
        )
        ConnectionType connectionType,

        @TemplateProperty(
                label = "Host",
                description = "Valkey hostname or IP address.",
                defaultValue = "localhost"
        )
        String host,

        @TemplateProperty(
                label = "Port",
                description = "Valkey port.",
                defaultValue = "6379"
        )
        Integer port,

        @TemplateProperty(
                label = "Cluster Nodes",
                description = "Comma-separated cluster nodes.",
                optional = true
        )
        List<String> clusterNodes,

        @TemplateProperty(
                label = "Username",
                description = "Valkey ACL username.",
                optional = true
        )
        String username,

        @TemplateProperty(
                label = "Password",
                description = "Valkey password.",
                optional = true
        )
        String password,

        @TemplateProperty(
                label = "SSL/TLS",
                description = "Enable SSL/TLS.",
                defaultValue = "false"
        )
        Boolean ssl,

        @TemplateProperty(
                label = "Key",
                description = "Valkey key."
        )
        String key,

        @TemplateProperty(
                label = "Value",
                description = "Value to store. Required for SET.",
                optional = true
        )
        String value,

        @TemplateProperty(
                label = "Field",
                description = "Hash field used by HGET, HSET and HDEL.",
                optional = true
        )
        String field,

        @TemplateProperty(
                label = "New Key",
                description = "New key name used by the RENAME operation.",
                optional = true
        )
        String newKey,

        @TemplateProperty(
                label = "Expiration (Seconds)",
                description = "Expiration time in seconds. Used by EXPIRE.",
                optional = true
        )
        Long expirationSeconds,

        @TemplateProperty(
                label = "Amount",
                description = "Numeric amount used by INCRBY and DECRBY.",
                optional = true
        )
        Long amount,

        @TemplateProperty(
                label = "Channel",
                description = "Valkey Pub/Sub channel used by PUBLISH and SUBSCRIBE.",
                optional = true
        )
        String channel,

        @TemplateProperty(
                label = "Message",
                description = "Message to publish to the Valkey Pub/Sub channel.",
                optional = true
        )
        String message,

        @TemplateProperty(
                label = "Pattern",
                description = "Optional key pattern used by SCAN, for example user:*.",
                optional = true
        )
        String pattern,

        @TemplateProperty(
                label = "Cursor",
                description = "SCAN cursor. Use 0 to start scanning.",
                optional = true
        )
        Long cursor,

        @TemplateProperty(
                label = "Count",
                description = "Maximum number of keys to return during a SCAN operation.",
                optional = true
        )
        Integer count

) {

    public enum ConnectionType {
        STANDALONE,
        CLUSTER
    }
}