package io.camunda.valkey;


import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.generator.java.annotation.ElementTemplate;
import io.camunda.connector.api.annotation.Operation;
import io.camunda.connector.api.annotation.Variable;
import io.camunda.connector.api.outbound.OutboundConnectorProvider;
import io.camunda.valkey.dto.ValkeyPublishResponse;
import io.camunda.valkey.dto.ValkeyRequest;
import io.camunda.valkey.dto.ValkeyScanResponse;
import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@OutboundConnector(
        name = "Valkey Connector",
//        inputVariables = {"ValkeyRequest"},
        type = "io.camunda:valkey:1"
)
@ElementTemplate(
        id = "io.camunda.valkey.connector.v1",
        name = "Valkey Connector",
        version = 1,
        icon = "icon.svg",
        description = "Valkey connector for standalone and cluster deployments"
//        inputDataClass = ValkeyRequest.class

)
public class ValkeyConnectorFunction implements OutboundConnectorProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValkeyConnectorFunction.class);

    private final ValkeyConnectionManager connectionManager = new ValkeyConnectionManager();

    @Operation(id = "set", name = "SET")
    public Object set(@Variable ValkeyRequest request) {


        var commands = isCluster(request)
                ? clusterCommands(request)
                : standaloneCommands(request);

        if (request.expirationSeconds() == null) {
            return commands.set(request.key(), request.value());
        }

        return commands.set(
                request.key(),
                request.value(),
                SetArgs.Builder.ex(request.expirationSeconds())
        );

    }

    @Operation(id = "get", name = "GET")
    public Object get(@Variable ValkeyRequest request) {

        if (isCluster(request)) {
            return clusterCommands(request)
                    .get(request.key());
        }

        return standaloneCommands(request)
                .get(request.key());
    }

    @Operation(id = "exists", name = "EXISTS")
    public Object exists(@Variable ValkeyRequest request) {

        if (isCluster(request)) {

            return clusterCommands(request)
                    .exists(request.key());
        }

        return standaloneCommands(request)
                .exists(request.key());
    }

    @Operation(id = "delete", name = "DELETE")
    public Object delete(@Variable ValkeyRequest request) {

        if (isCluster(request)) {

            return clusterCommands(request)
                    .del(request.key());
        }

        return standaloneCommands(request)
                .del(request.key());
    }

    @Operation(id = "expire", name = "EXPIRE")
    public Object expire(@Variable ValkeyRequest request) {

        if (isCluster(request)) {

            return clusterCommands(request)
                    .expire(request.key(), request.expirationSeconds());
        }

        return standaloneCommands(request)
                .expire(request.key(), request.expirationSeconds());
    }


    @Operation(id = "incr", name = "INCR")
    public Object incr(@Variable ValkeyRequest request) {

        if (isCluster(request)) {

            return clusterCommands(request)
                    .incr(request.key());
        }

        return standaloneCommands(request)
                .incr(request.key());
    }

    @Operation(id = "hset", name = "HSET")
    public Object hset(@Variable ValkeyRequest request) {

        if (isCluster(request)) {

            return clusterCommands(request)
                    .hset(
                            request.key(),
                            request.field(),
                            request.value()
                    );
        }

        return standaloneCommands(request)
                .hset(
                        request.key(),
                        request.field(),
                        request.value()
                );
    }



    @Operation(id = "hgetall", name = "HGETALL")
    public Object hgetall(@Variable ValkeyRequest request) {

        if (isCluster(request)) {

            return clusterCommands(request)
                    .hgetall(request.key());
        }

        return standaloneCommands(request)
                .hgetall(request.key());
    }

    @Operation(id = "hdel", name = "HDEL")
    public Object hdel(@Variable ValkeyRequest request) {

        if (isCluster(request)) {

            return clusterCommands(request)
                    .hdel(request.key(), request.field());
        }

        return standaloneCommands(request)
                .hdel(request.key(), request.field());
    }

    @Operation(id = "lpush", name = "LPUSH")
    public Object lpush(@Variable ValkeyRequest request) {

        if (isCluster(request)) {

            return clusterCommands(request)
                    .lpush(request.key(), request.value());
        }

        return standaloneCommands(request)
                .lpush(request.key(), request.value());
    }

    @Operation(id = "rpush", name = "RPUSH")
    public Object rpush(@Variable ValkeyRequest request) {

        if (isCluster(request)) {

            return clusterCommands(request)
                    .rpush(request.key(), request.value());
        }

        return standaloneCommands(request)
                .rpush(request.key(), request.value());
    }

    @Operation(id = "lpop", name = "LPOP")
    public Object lpop(@Variable ValkeyRequest request) {

        if (isCluster(request)) {

            return clusterCommands(request)
                    .lpop(request.key());
        }

        return standaloneCommands(request)
                .lpop(request.key());
    }

    @Operation(id = "rpop", name = "RPOP")
    public Object rpop(@Variable ValkeyRequest request) {

        if (isCluster(request)) {

            return clusterCommands(request)
                    .rpop(request.key());
        }

        return standaloneCommands(request)
                .rpop(request.key());
    }


    @Operation(id = "sadd", name = "SADD")
    public Object sadd(@Variable ValkeyRequest request) {

        if (isCluster(request)) {

            return clusterCommands(request)
                    .sadd(request.key(), request.value());
        }

        return standaloneCommands(request)
                .sadd(request.key(), request.value());
    }

    @Operation(id = "srem", name = "SREM")
    public Object srem(@Variable ValkeyRequest request) {

        if (isCluster(request)) {

            return clusterCommands(request)
                    .srem(request.key(), request.value());
        }

        return standaloneCommands(request)
                .srem(request.key(), request.value());
    }

    @Operation(id = "smembers", name = "SMEMBERS")
    public Object smembers(@Variable ValkeyRequest request) {

        if (isCluster(request)) {

            return clusterCommands(request)
                    .smembers(request.key());
        }

        return standaloneCommands(request)
                .smembers(request.key());
    }

    @Operation(id = "sismember", name = "SISMEMBER")
    public Object sismember(@Variable ValkeyRequest request) {

        if (isCluster(request)) {

            return clusterCommands(request)
                    .sismember(request.key(), request.value());
        }

        return standaloneCommands(request)
                .sismember(request.key(), request.value());
    }

    @Operation(id = "incrby", name = "INCRBY")
    public Object incrby(@Variable ValkeyRequest request) {

        if (isCluster(request)) {

            return clusterCommands(request)
                    .incrby(request.key(), request.amount());
        }

        return standaloneCommands(request)
                .incrby(request.key(), request.amount());
    }

    @Operation(id = "decrby", name = "DECRBY")
    public Object decrby(@Variable ValkeyRequest request) {

        if (isCluster(request)) {

            return clusterCommands(request)
                    .decrby(request.key(), request.amount());
        }

        return standaloneCommands(request)
                .decrby(request.key(), request.amount());
    }

    @Operation(id = "type", name = "TYPE")
    public Object type(@Variable ValkeyRequest request) {

        if (isCluster(request)) {

            return clusterCommands(request)
                    .type(request.key());
        }

        return standaloneCommands(request)
                .type(request.key());
    }

    @Operation(id = "rename", name = "RENAME")
    public Object rename(@Variable ValkeyRequest request) {

        if (isCluster(request)) {

            return clusterCommands(request)
                    .rename(request.key(), request.newKey());
        }

        return standaloneCommands(request)
                .rename(request.key(), request.newKey());
    }

    @Operation(id = "publish", name = "PUBLISH")
    public Object publish(@Variable ValkeyRequest request) {

        if (request.channel() == null || request.channel().isBlank()) {
            throw new IllegalArgumentException("Channel must not be null");
        }

        if (request.message() == null) {
            throw new IllegalArgumentException("Message must not be null");
        }

        long subscribers;

        if (request.connectionType() ==
                ValkeyRequest.ConnectionType.CLUSTER) {

            subscribers = connectionManager
                    .getClusterPubSub(request)
                    .commands()
                    .publish(
                            request.channel(),
                            request.message()
                    );

        } else {

            subscribers = connectionManager
                    .getStandalonePubSub(request)
                    .connection()
                    .sync()
                    .publish(
                            request.channel(),
                            request.message()
                    );
        }

        return new ValkeyPublishResponse(
                request.channel(),
                request.message(),
                subscribers
        );
    }

    @Operation(id = "subscribe", name = "SUBSCRIBE")
    public Object subscribe(@Variable ValkeyRequest request) {

        if (request.channel() == null || request.channel().isBlank()) {
            throw new IllegalArgumentException("Channel must not be null");
        }

        var pubSub = connectionManager.getStandalonePubSub(request);

        var connection = pubSub.connection();

       LOGGER.info("Connection open before subscribe: {}",pubSub.isOpen());

       LOGGER.info("Pub/Sub connection created");
       LOGGER.info("Subscribing to channel: {}", request.channel());

        connection.addListener(new RedisPubSubAdapter<String, String>() {

            @Override
            public void message(String channel, String message) {

               LOGGER.info("=================================");
               LOGGER.info("Received message from Valkey");
               LOGGER.info("Channel : {}" ,channel);
               LOGGER.info("Message : {}", message);
               LOGGER.info("=================================");
            }

            @Override
            public void subscribed(String channel, long count) {

               LOGGER.info("SUBSCRIBED:  {} , count= {}" ,channel , count);
            }

            @Override
            public void unsubscribed(String channel, long count) {

                LOGGER.info("UNSUBSCRIBED: {} , count= {}", channel, count);
            }
        });

        connection.sync().subscribe(request.channel());

       LOGGER.info("Connection open after subscribe: {}", pubSub.isOpen());
       LOGGER.info("Successfully subscribed to: {}", request.channel());

        return "Subscribed to channel: " + request.channel();
    }
    @Operation(id = "scan", name = "SCAN")
    public Object scan(@Variable ValkeyRequest request) {

        ScanArgs scanArgs = ScanArgs.Builder.limit(
                request.count() != null
                        ? request.count()
                        : 100
        );

        if (request.pattern() != null
                && !request.pattern().isBlank()) {

            scanArgs.match(request.pattern());
        }

        long cursor = request.cursor() != null
                ? request.cursor()
                : 0L;

        if (request.connectionType()
                == ValkeyRequest.ConnectionType.CLUSTER) {

            return scanCluster(request, scanArgs, cursor);
        }

        KeyScanCursor<String> result =
                standaloneCommands(request)
                        .scan(
                                io.lettuce.core.ScanCursor.of(String.valueOf(cursor)),
                                scanArgs
                        );

        return new ValkeyScanResponse(result.getCursor(), result.getKeys());
    }

    private boolean isCluster(ValkeyRequest request) {
        return request.connectionType() == ValkeyRequest.ConnectionType.CLUSTER;
    }


    private RedisCommands<String, String> standaloneCommands(ValkeyRequest request) {
        return connectionManager
                .getStandalone(request)
                .commands();
    }

    private RedisAdvancedClusterCommands<String, String> clusterCommands(
            ValkeyRequest request) {

        return connectionManager
                .getCluster(request)
                .commands();
    }

    private ValkeyScanResponse scanCluster(
            ValkeyRequest request,
            ScanArgs scanArgs,
            long cursor) {

        KeyScanCursor<String> result =
                clusterCommands(request)
                        .scan(
                                io.lettuce.core.ScanCursor.of(String.valueOf(cursor)),
                                scanArgs
                        );

        return new ValkeyScanResponse(result.getCursor(), result.getKeys());
    }
}