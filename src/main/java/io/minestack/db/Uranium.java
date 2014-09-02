package io.minestack.db;

import com.mongodb.ServerAddress;
import com.rabbitmq.client.Address;
import io.minestack.db.database.*;
import io.minestack.db.mongo.MongoDatabase;
import io.minestack.db.rabbitmq.RabbitMQ;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class Uranium {

    @Getter
    private static NodeLoader nodeLoader;

    @Getter
    private static BungeeLoader bungeeLoader;

    @Getter
    private static BungeeTypeLoader bungeeTypeLoader;

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

    private static Uranium database;

    public static void initDatabase(List<ServerAddress> mongoAddresses, List<Address> rabbitAddresses, String rabbitUsername, String rabbitPassword) throws Exception {
        if (Uranium.database != null) {
            Uranium.needsInit = false;
            Uranium.database = new Uranium(mongoAddresses, rabbitAddresses, rabbitUsername, rabbitPassword);
        }
    }

    private Uranium(List<ServerAddress> mongoAddresses, List<Address> rabbitAddresses, String rabbitUsername, String rabbitPassword) throws Exception {
        if (mongoAddresses.isEmpty()) {
            throw new Exception("No valid mongo addresses");
        }
        log.info("Setting up mongo database minestack");
        MongoDatabase mongoDatabase = new MongoDatabase(mongoAddresses, "minestack");

        if (rabbitAddresses.isEmpty()) {
            throw new Exception("No valid RabbitMQ addresses");
        }

            log.info("Setting up RabbitMQ");
            rabbitMQ = new RabbitMQ(rabbitAddresses, rabbitUsername, rabbitPassword);

        Uranium.pluginLoader = new PluginLoader(mongoDatabase);
        Uranium.worldLoader = new WorldLoader(mongoDatabase);
        Uranium.serverTypeLoader = new ServerTypeLoader(mongoDatabase, Uranium.getPluginLoader(), Uranium.getWorldLoader());
        Uranium.bungeeTypeLoader = new BungeeTypeLoader(mongoDatabase, Uranium.getPluginLoader(), Uranium.getServerTypeLoader());
        Uranium.nodeLoader = new NodeLoader(mongoDatabase, Uranium.getBungeeTypeLoader());
        Uranium.bungeeLoader = new BungeeLoader(mongoDatabase, Uranium.getBungeeTypeLoader(), Uranium.getNodeLoader());
        Uranium.playerLoader = new PlayerLoader(mongoDatabase, Uranium.getServerTypeLoader(), Uranium.getBungeeTypeLoader());
        Uranium.serverLoader = new ServerLoader(mongoDatabase, Uranium.getNodeLoader(), Uranium.getServerTypeLoader(), Uranium.getPlayerLoader());
    }

}
