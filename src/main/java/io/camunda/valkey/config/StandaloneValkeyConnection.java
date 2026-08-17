package io.camunda.valkey.config;

import io.camunda.valkey.dto.ValkeyRequest;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class StandaloneValkeyConnection {

    private final RedisClient client;

    private final StatefulRedisConnection<String, String> connection;

    public StandaloneValkeyConnection(ValkeyRequest request) {

        RedisURI uri = createUri(request);

        this.client = RedisClient.create(uri);

        this.connection = client.connect();
    }

    private RedisURI createUri(ValkeyRequest request) {

        RedisURI.Builder builder = RedisURI.builder()
                .withHost(request.host()+request.port())
                .withPort(request.port());

//        RedisURI.Builder builder = RedisURI.builder()
//                .withHost("127.0.0.1")
//                .withPort(6379);

        if (request.username() != null
                && !request.username().isBlank()
                && request.password() != null
                && !request.password().isBlank()) {

            builder.withAuthentication(
                    request.username(),
                    request.password().toCharArray()
            );
        }

        /*
         * Password-only authentication
         */
        else if (request.password() != null
                && !request.password().isBlank()) {

            builder.withPassword(
                    request.password().toCharArray()
            );
        }


        if (Boolean.TRUE.equals(request.ssl())) {
            builder.withSsl(true);
        }

        return builder.build();
    }

    public RedisCommands<String, String> commands() {
        return connection.sync();
    }

    public void close() {
        connection.close();
        client.shutdown();
    }
}