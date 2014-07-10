package com.rmb938.mn2.docker.db.rabbitmq;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.List;

public class RabbitMQ {

    private Connection connection;

    public RabbitMQ(List<Address> addressList, String username, String password) throws IOException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(username);
        factory.setPassword(password);
        connection = factory.newConnection(addressList.toArray(new Address[addressList.size()]));
    }

    public Channel getChannel() throws IOException {
        return connection.createChannel();
    }

}
