package io.camunda.valkey.config;


import io.camunda.valkey.dto.ValkeyRequest;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.pubsub.StatefulRedisClusterPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;

import java.util.List;

public class ClusterValkeyPubSubConnection {

    private final RedisClusterClient client;

    private final StatefulRedisClusterPubSubConnection<String, String>
            connection;

    public ClusterValkeyPubSubConnection(ValkeyRequest request) {

        List<RedisURI> nodes = request.clusterNodes()
                .stream()
                .map(node -> createUri(node, request))
                .toList();

        this.client = RedisClusterClient.create(nodes);

        this.connection = client.connectPubSub();
    }

    private RedisURI createUri(
            String node,
            ValkeyRequest request) {

        String[] parts = node.split(":");

        RedisURI.Builder builder = RedisURI.builder()
                .withHost(parts[0])
                .withPort(Integer.parseInt(parts[1]));

        if (request.username() != null
                && !request.username().isBlank()) {

            builder.withAuthentication(
                    request.username(),
                    request.password()
            );

        } else if (request.password() != null
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

    public RedisPubSubCommands<String, String> commands() {
        return connection.sync();
    }

    public void close() {
        connection.close();
        client.shutdown();
    }
}