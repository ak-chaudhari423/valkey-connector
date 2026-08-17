package io.camunda.valkey.config;

import io.camunda.valkey.dto.ValkeyRequest;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;

import java.util.List;

public class ClusterValkeyConnection {

    private final RedisClusterClient client;

    private final StatefulRedisClusterConnection<String, String> connection;

    public ClusterValkeyConnection(ValkeyRequest request) {

        List<RedisURI> nodes = request.clusterNodes()
                .stream()
                .map(node -> createUri(node, request))
                .toList();

        this.client = RedisClusterClient.create(nodes);

        this.connection = client.connect();
    }

    private RedisURI createUri(
            String node,
            ValkeyRequest request) {

        String[] parts = node.split(":");

        String host = parts[0];
        int port = Integer.parseInt(parts[1]);

        RedisURI.Builder builder = RedisURI.builder()
                .withHost(host)
                .withPort(port);

        if (request.username() != null
                && !request.username().isBlank()
                && request.password() != null
                && !request.password().isBlank()) {

            builder.withAuthentication(
                    request.username(),
                    request.password().toCharArray());
        }

        else if (request.password() != null
                && !request.password().isBlank()) {

            builder.withPassword(
                    request.password().toCharArray());
        }

        if (Boolean.TRUE.equals(request.ssl())) {
            builder.withSsl(true);
        }

        return builder.build();
    }

    public RedisAdvancedClusterCommands<String, String> commands() {
        return connection.sync();
    }

    public void close() {
        connection.close();
        client.shutdown();
    }
}