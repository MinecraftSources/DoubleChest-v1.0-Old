package com.rmb938.mn2.docker.db.rabbitmq;

import com.rabbitmq.client.*;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.List;

@Log4j2
public class RabbitMQ {

    private final ConnectionFactory factory;
    private final Address[] addresses;

    public RabbitMQ(List<Address> addressList, String username, String password) throws IOException {
        factory = new ConnectionFactory();
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setRequestedHeartbeat(15);
        factory.setConnectionTimeout(5000);
        factory.setNetworkRecoveryInterval(0);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setTopologyRecoveryEnabled(true);
        addresses = addressList.toArray(new Address[addressList.size()]);
    }

    public Connection getConnection() throws IOException {
        return factory.newConnection(addresses);
    }

}
