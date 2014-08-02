package com.rmb938.mn2.docker.db.database;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.rmb938.mn2.docker.db.entity.MN2Node;
import com.rmb938.mn2.docker.db.entity.MN2Server;
import com.rmb938.mn2.docker.db.entity.MN2ServerType;
import com.rmb938.mn2.docker.db.mongo.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;

import java.util.ArrayList;

@Log4j2
public class ServerLoader extends EntityLoader<MN2Server> {

    private final NodeLoader nodeLoader;
    private final ServerTypeLoader serverTypeLoader;

    public ServerLoader(MongoDatabase db, NodeLoader nodeLoader, ServerTypeLoader serverTypeLoader) {
        super(db, "servers");
        this.nodeLoader = nodeLoader;
        this.serverTypeLoader = serverTypeLoader;
    }

    public Long getCount(MN2ServerType serverType) {
        BasicDBList and = new BasicDBList();
        and.add(new BasicDBObject("_servertype", serverType.get_id()));
        and.add(new BasicDBObject("lastUpdate", new BasicDBObject("$gt", System.currentTimeMillis()-60000)));
        return getDb().count(getCollection(), new BasicDBObject("$and", and));
    }

    public ArrayList<MN2Server> nodeServers(MN2Node node) {
        ArrayList<MN2Server> servers = new ArrayList<>();

        BasicDBList and = new BasicDBList();
        and.add(new BasicDBObject("_node", node.get_id()));
        and.add(new BasicDBObject("lastUpdate", new BasicDBObject("$gt", System.currentTimeMillis()-60000)));
        DBCursor dbCursor = getDb().findMany(getCollection(), new BasicDBObject("$and",and));
        while (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            MN2Server server = loadEntity((ObjectId)dbObject.get("_id"));
            if (server != null) {
                servers.add(server);
            }
        }

        return servers;
    }

    @Override
    public MN2Server loadEntity(ObjectId _id) {
        if (_id == null) {
            log.error("Error loading world. _id null");
            return null;
        }
        DBObject dbObject = getDb().findOne(getCollection(), new BasicDBObject("_id", _id));
        if (dbObject != null) {
            MN2Server server = new MN2Server();
            server.set_id((ObjectId) dbObject.get("_id"));
            server.setServerType(serverTypeLoader.loadEntity((ObjectId) dbObject.get("_servertype")));
            server.setNode(nodeLoader.loadEntity((ObjectId) dbObject.get("_node")));
            server.setContainerId((String) dbObject.get("containerId"));
            server.setLastUpdate((Long) dbObject.get("lastUpdate"));
            server.setNumber((Integer) dbObject.get("number"));
            server.setPort((Integer) dbObject.get("port"));

            BasicDBList players = (BasicDBList) dbObject.get("players");
            if (players != null) {
                for (Object object : players) {
                    String uuid = (String) object;
                    //load player entity and add
                }
            }

            return server;
        }
        return null;
    }

    @Override
    public void saveEntity(MN2Server server) {

    }

    @Override
    public ObjectId insertEntity(MN2Server server) {
        return null;
    }

    @Override
    public void removeEntity(MN2Server entity) {
        getDb().delete(getCollection(), new BasicDBObject("_id", entity.get_id()));
    }
}
