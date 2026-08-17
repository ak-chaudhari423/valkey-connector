package io.camunda.valkey.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

public class StandaloneValkeyPubSubConnection {

    private final RedisClient client;

    private final StatefulRedisPubSubConnection<String, String> connection;

    public StandaloneValkeyPubSubConnection(
            String host,
            int port,
            String username,
            String password,
            boolean ssl) {

        String scheme = ssl ? "rediss" : "redis";

        String uri;

        if (username != null && !username.isBlank()) {

            uri = String.format(
                    "%s://%s:%s@%s:%d",
                    scheme,
                    username,
                    password,
                    host,
                    port
            );

        } else {

            uri = String.format(
                    "%s://:%s@%s:%d",
                    scheme,
                    password,
                    host,
                    port
            );
        }

        this.client = RedisClient.create(uri);

        this.connection = client.connectPubSub();
    }

    public StatefulRedisPubSubConnection<String, String> connection() {
        return connection;
    }
    public boolean isOpen() {
        return connection.isOpen();
    }
    public void close() {
        connection.close();
        client.shutdown();
    }
}
