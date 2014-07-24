package com.rmb938.mn2.docker.db.rabbitmq;

import com.rabbitmq.client.*;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.List;

@Log4j2
public class RabbitMQ {

    private Connection connection;

    public RabbitMQ(List<Address> addressList, String username, String password) throws IOException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setRequestedHeartbeat(1);
        factory.setConnectionTimeout(5000);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setTopologyRecoveryEnabled(true);
        connection = factory.newConnection(addressList.toArray(new Address[addressList.size()]));
        connection.addBlockedListener(new BlockedListener() {
            @Override
            public void handleBlocked(String s) throws IOException {
                log.info("Blocked Connection "+s);
            }

            @Override
            public void handleUnblocked() throws IOException {

            }
        });
    }

    public Channel getChannel() throws IOException {
        return connection.createChannel();
    }

}
