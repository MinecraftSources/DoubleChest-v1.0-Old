package io.minestack.db;

import com.mongodb.ServerAddress;
import com.rabbitmq.client.Address;
import io.minestack.db.database.node.NodeLoader;
import io.minestack.db.database.player.PlayerLoader;
import io.minestack.db.database.plugin.PluginLoader;
import io.minestack.db.database.proxy.ProxyLoader;
import io.minestack.db.database.proxy.ProxyTypeLoader;
import io.minestack.db.database.server.ServerLoader;
import io.minestack.db.database.server.ServerTypeLoader;
import io.minestack.db.database.world.WorldLoader;
import io.minestack.db.mongo.MongoDatabase;
import io.minestack.db.rabbitmq.RabbitMQ;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class DoubleChest {

    @Getter
    private static NodeLoader nodeLoader;

    @Getter
    private static ProxyLoader proxyLoader;

    @Getter
    private static ProxyTypeLoader proxyTypeLoader;

    @Getter
    private static ServerTypeLoader serverTypeLoader;

    @Getter
    private static ServerLoader serverLoader;

    @Getter
    private static PluginLoader pluginLoader;

    @Getter
    private static WorldLoader worldLoader;

    @Getter
    private static PlayerLoader playerLoader;

    @Getter
    private static RabbitMQ rabbitMQ;

    @Getter
    private static boolean needsInit = true;

    @Getter
    private static List<ServerAddress> mongoAddresses;

    private static DoubleChest database;

    public static void initDatabase(List<ServerAddress> mongoAddresses, List<Address> rabbitAddresses, String rabbitUsername, String rabbitPassword) throws Exception {
        if (DoubleChest.database == null) {
            DoubleChest.needsInit = false;
            database = new DoubleChest(mongoAddresses, rabbitAddresses, rabbitUsername, rabbitPassword);
        }
    }

    public static MongoDatabase createMongoInstance(String database) {
        return new MongoDatabase(mongoAddresses, database);
    }

    private DoubleChest(List<ServerAddress> mongoAddresses, List<Address> rabbitAddresses, String rabbitUsername, String rabbitPassword) throws Exception {
        if (mongoAddresses.isEmpty()) {
            throw new Exception("No valid mongo addresses");
        }
        log.info("Setting up mongo database minestack");
        DoubleChest.mongoAddresses = mongoAddresses;
        MongoDatabase mongoDatabase = createMongoInstance("minestack");

        if (rabbitAddresses.isEmpty()) {
            throw new Exception("No valid RabbitMQ addresses");
        }

            log.info("Setting up RabbitMQ");
            rabbitMQ = new RabbitMQ(rabbitAddresses, rabbitUsername, rabbitPassword);

        pluginLoader = new PluginLoader(mongoDatabase);
        worldLoader = new WorldLoader(mongoDatabase);
        serverTypeLoader = new ServerTypeLoader(mongoDatabase, getPluginLoader(), getWorldLoader());
        proxyTypeLoader = new ProxyTypeLoader(mongoDatabase, getServerTypeLoader(), getPluginLoader());
        nodeLoader = new NodeLoader(mongoDatabase, getProxyTypeLoader());
        proxyLoader = new ProxyLoader(mongoDatabase, getProxyTypeLoader(), getNodeLoader());
        playerLoader = new PlayerLoader(mongoDatabase, getServerTypeLoader(), getProxyTypeLoader());
        serverLoader = new ServerLoader(mongoDatabase, getNodeLoader(), getServerTypeLoader(), getPlayerLoader());
    }

}
