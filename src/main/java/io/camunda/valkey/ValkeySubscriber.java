package io.camunda.valkey;


import io.lettuce.core.RedisURI;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.RedisPubSubAdapter;

public class ValkeySubscriber {

    public void subscribe(String host, int port, String channel) {

        RedisURI uri =
                RedisURI.builder()
                        .withHost(host)
                        .withPort(port)
                        .build();

        RedisClient client = RedisClient.create(uri);

        StatefulRedisPubSubConnection<String, String>
                connection =
                client.connectPubSub();

        connection.addListener(new RedisPubSubAdapter<>() {

                    @Override
                    public void message(
                            String channel,
                            String message) {

                        System.out.println("Channel: " + channel);

                        System.out.println("Message: " + message);}
                });

        connection.sync()
                .subscribe(channel);

        System.out.println(
                "Subscribed to: " + channel);
    }
}