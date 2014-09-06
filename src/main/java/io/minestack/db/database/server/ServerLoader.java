package io.minestack.db.database.server;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import io.minestack.db.database.driver.DrivableEntityLoader;
import io.minestack.db.database.driver.DriverLoader;
import io.minestack.db.database.node.NodeLoader;
import io.minestack.db.database.player.PlayerLoader;
import io.minestack.db.database.server.driver.bukkit.BukkitDriverLoader;
import io.minestack.db.entity.DCNode;
import io.minestack.db.entity.DCPlayer;
import io.minestack.db.entity.server.DCServer;
import io.minestack.db.entity.server.DCServerType;
import io.minestack.db.mongo.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;

import java.util.ArrayList;

@Log4j2
public class ServerLoader extends DrivableEntityLoader<DCServer> {

    private final PlayerLoader playerLoader;
    private final NodeLoader nodeLoader;
    private final ServerTypeLoader serverTypeLoader;

    public ServerLoader(MongoDatabase db, NodeLoader nodeLoader, ServerTypeLoader serverTypeLoader, PlayerLoader playerLoader) {
        super(db, "servers");
        this.nodeLoader = nodeLoader;
        this.serverTypeLoader = serverTypeLoader;
        this.playerLoader = playerLoader;

        //servertype index
        BasicDBObject index = new BasicDBObject();
        index.put("_servertype", 1);
        getDb().createIndex(getCollection(), index);

        //node index
        index.put("_node", 1);
        getDb().createIndex(getCollection(), index);

        //servertype and lastUpdate index
        index = new BasicDBObject();
        index.put("_servertype", 1);
        index.put("lastUpdate", 1);
        getDb().createIndex(getCollection(), index);

        //servertype and number index
        index = new BasicDBObject();
        BasicDBObject options = new BasicDBObject();
        options.put("unique", true);
        index.put("_servertype", 1);
        index.put("number", 1);
        getDb().createIndex(getCollection(), index, options);

        DriverLoader driverLoader = new BukkitDriverLoader(db, getCollection());
        getDrivers().put(driverLoader.getDriverName(), driverLoader);
    }

    public Long getCount(DCServerType serverType) {
        return getDb().count(getCollection(), new BasicDBObject("_servertype", serverType.get_id()));
    }

    public DCServer getServer(DCServerType serverType, int number) {
        if (serverType == null) {
            return null;
        }
        BasicDBList and = new BasicDBList();
        and.add(new BasicDBObject("_servertype", serverType.get_id()));
        and.add(new BasicDBObject("number", number));

        DBObject dbObject = getDb().findOne(getCollection(), new BasicDBObject("$and", and));
        if (dbObject != null) {
            return loadEntity((ObjectId) dbObject.get("_id"));
        }
        return null;
    }

    private int getNextNumber(DCServerType serverType) {
        int number = 1;
        BasicDBList and = new BasicDBList();
        and.add(new BasicDBObject("_servertype", serverType.get_id()));
        while (true) {
            and.add(new BasicDBObject("number", number));
            DBObject dbObject = getDb().findOne(getCollection(), new BasicDBObject("$and", and));
            if (dbObject == null) {
                log.info("Found number "+number);
                break;
            }
            log.info("Already has number "+number);
            and.remove(1);
            number += 1;
        }
        return number;
    }

    public ArrayList<DCServer> getNodeServers(DCNode node) {
        ArrayList<DCServer> servers = new ArrayList<>();

        DBCursor dbCursor = getDb().findMany(getCollection(), new BasicDBObject("_node", node.get_id()));
        while (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            DCServer server = loadEntity((ObjectId) dbObject.get("_id"));
            if (server != null) {
                servers.add(server);
            }
        }
        dbCursor.close();
        return servers;
    }

    public ArrayList<DCServer> getServers() {
        ArrayList<DCServer> servers = new ArrayList<>();

        DBCursor dbCursor = getDb().findMany(getCollection());
        dbCursor.sort(new BasicDBObject("_servertype", 1));
        dbCursor.sort(new BasicDBObject("number", 1));
        while (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            DCServer server = loadEntity((ObjectId) dbObject.get("_id"));
            if (server != null) {
                servers.add(server);
            }
        }
        dbCursor.close();
        return servers;
    }

    public ArrayList<DCServer> getTypeServers(DCServerType serverType) {
        ArrayList<DCServer> servers = new ArrayList<>();

        if (serverType == null) {
            return servers;
        }

        DBCursor dbCursor = getDb().findMany(getCollection(), new BasicDBObject("_servertype", serverType.get_id()));
        while (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            DCServer server = loadEntity((ObjectId) dbObject.get("_id"));
            if (server != null) {
                servers.add(server);
            }
        }
        dbCursor.close();
        return servers;
    }

    @Override
    protected DCServer loadDrivableEntity(DBObject dbObject) {
        DCServer server = new DCServer();
        server.set_id((ObjectId) dbObject.get("_id"));
        server.setServerType(serverTypeLoader.loadEntity((ObjectId) dbObject.get("_servertype")));
        server.setNode(nodeLoader.loadEntity((ObjectId) dbObject.get("_node")));
        server.setLastUpdate((Long) dbObject.get("lastUpdate"));
        server.setNumber((Integer) dbObject.get("number"));
        server.setContainerId((String) dbObject.get("containerId"));
        server.setContainerAddress((String) dbObject.get("containerAddress"));
        server.setPort((Integer) dbObject.get("port"));

        BasicDBList players = (BasicDBList) dbObject.get("players");
        for (Object object : players) {
            DBObject dbObj = (DBObject) object;
            ObjectId _playerId = (ObjectId) dbObj.get("_id");
            DCPlayer player = playerLoader.loadEntity(_playerId);
            if (player != null) {
                server.getPlayers().add(player);
            }
        }

        return server;
    }

    @Override
    protected void saveDrivableEntity(DCServer entity, DBObject entityObject) {
        entityObject.put("lastUpdate", entity.getLastUpdate());
        entityObject.put("containerId", entity.getContainerId());
        entityObject.put("containerAddress", entity.getContainerAddress());
        entityObject.put("port", entity.getPort());

        BasicDBList players = new BasicDBList();
        for (DCPlayer player : entity.getPlayers()) {
            BasicDBObject dbObject = new BasicDBObject("_id", player.get_id());
            players.add(dbObject);
        }

        entityObject.put("players", players);
    }

    @Override
    protected void insertDrivableEntity(DCServer entity, DBObject entityObject) {
        entityObject.put("_servertype", entity.getServerType().get_id());
        entityObject.put("_node", entity.getNode().get_id());
        entityObject.put("lastUpdate", entity.getLastUpdate());
        entityObject.put("containerId", "NULL");
        entityObject.put("containerAddress", "NULL");
        entityObject.put("port", -1);
        entityObject.put("players", new BasicDBList());
        entityObject.put("number", getNextNumber(entity.getServerType()));
    }
}
