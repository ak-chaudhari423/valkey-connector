package io.camunda.valkey;

import io.camunda.valkey.config.ClusterValkeyConnection;
import io.camunda.valkey.config.ClusterValkeyPubSubConnection;
import io.camunda.valkey.config.StandaloneValkeyConnection;
import io.camunda.valkey.config.StandaloneValkeyPubSubConnection;
import io.camunda.valkey.dto.ValkeyRequest;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ValkeyConnectionManager {

    private final ConcurrentMap<String, StandaloneValkeyConnection>
            standaloneConnections = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, ClusterValkeyConnection>
            clusterConnections = new ConcurrentHashMap<>();


    private final ConcurrentMap<String, StandaloneValkeyPubSubConnection>
            standalonePubSubConnections = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, ClusterValkeyPubSubConnection>
            clusterPubSubConnections = new ConcurrentHashMap<>();


    public StandaloneValkeyConnection getStandalone(
            ValkeyRequest request) {

        String connectionKey =
                request.host() + ":" + request.port();

        return standaloneConnections.computeIfAbsent(
                connectionKey,
                key -> new StandaloneValkeyConnection(request)
        );
    }

    public ClusterValkeyConnection getCluster(
            ValkeyRequest request) {

        String connectionKey =
                String.join(",", request.clusterNodes());

        return clusterConnections.computeIfAbsent(
                connectionKey,
                key -> new ClusterValkeyConnection(request)
        );
    }

    public StandaloneValkeyPubSubConnection getStandalonePubSub(
            ValkeyRequest request) {

        String connectionKey =
                request.host() + ":" +
                        request.port() + ":" +
                        request.username();

        return standalonePubSubConnections.computeIfAbsent(
                connectionKey,
                key -> new StandaloneValkeyPubSubConnection(
                        request.host(),
                        request.port(),
                        request.username(),
                        request.password(),
                        Boolean.TRUE.equals(request.ssl())
                )
        );
    }


    public ClusterValkeyPubSubConnection getClusterPubSub(
            ValkeyRequest request) {

        String connectionKey =
                String.join(",", request.clusterNodes());

        return clusterPubSubConnections.computeIfAbsent(
                connectionKey,
                key -> new ClusterValkeyPubSubConnection(request)
        );
    }
    public void shutdown() {

        standaloneConnections
                .values()
                .forEach(StandaloneValkeyConnection::close);

        clusterConnections
                .values()
                .forEach(ClusterValkeyConnection::close);
    }
}