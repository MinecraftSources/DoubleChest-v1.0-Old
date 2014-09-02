package io.minestack.db.rabbitmq;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
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
        //factory.setRequestedHeartbeat(25);
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
