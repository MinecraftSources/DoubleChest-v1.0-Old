package io.minestack.db.database;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import io.minestack.db.entity.DCBungee;
import io.minestack.db.entity.DCNode;
import io.minestack.db.mongo.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;

import java.util.ArrayList;

@Log4j2
public class BungeeLoader extends EntityLoader<DCBungee> {

    private final BungeeTypeLoader bungeeTypeLoader;
    private final NodeLoader nodeLoader;

    public BungeeLoader(MongoDatabase db, BungeeTypeLoader bungeeTypeLoader, NodeLoader nodeLoader) {
        super(db, "bungees");
        this.bungeeTypeLoader = bungeeTypeLoader;
        this.nodeLoader = nodeLoader;
    }

    public DCBungee getNodeBungee(DCNode node) {
        DBObject dbObject = getDb().findOne(getCollection(), new BasicDBObject("_node", node.get_id()));
        if (dbObject != null) {
            return loadEntity((ObjectId) dbObject.get("_id"));
        }
        return null;
    }

    public ArrayList<DCBungee> getBungees() {
        ArrayList<DCBungee> bungees = new ArrayList<>();

        DBCursor dbCursor = getDb().findMany(getCollection());
        while (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            DCBungee bungee = loadEntity((ObjectId) dbObject.get("_id"));
            if (bungee != null) {
                bungees.add(bungee);
            }
        }
        dbCursor.close();
        return bungees;
    }

    @Override
    public DCBungee loadEntity(ObjectId _id) {
        if (_id == null) {
            log.error("Error loading bungee. _id null");
            return null;
        }
        DBObject dbObject = getDb().findOne(getCollection(), new BasicDBObject("_id", _id));
        if (dbObject != null) {
            DCBungee bungee = new DCBungee();
            bungee.set_id((ObjectId) dbObject.get("_id"));
            bungee.setBungeeType(bungeeTypeLoader.loadEntity((ObjectId) dbObject.get("_bungeetype")));
            bungee.setNode(nodeLoader.loadEntity((ObjectId) dbObject.get("_node")));
            bungee.setLastUpdate((Long) dbObject.get("lastUpdate"));
            bungee.setContainerId((String) dbObject.get("containerId"));

            return bungee;
        }
        return null;
    }

    @Override
    public void saveEntity(DCBungee entity) {
        BasicDBObject values = new BasicDBObject();
        values.put("lastUpdate", entity.getLastUpdate());
        values.put("containerId", entity.getContainerId());

        BasicDBObject set = new BasicDBObject("$set", values);
        getDb().updateDocument(getCollection(), new BasicDBObject("_id", entity.get_id()), set);
        log.info("Saving Bungee " + entity.get_id());
    }

    @Override
    public ObjectId insertEntity(DCBungee entity) {
        BasicDBObject dbObject = new BasicDBObject("_id", new ObjectId());
        dbObject.append("_bungeetype", entity.getBungeeType().get_id());
        dbObject.append("_node", entity.getNode().get_id());
        dbObject.append("lastUpdate", entity.getLastUpdate());
        dbObject.append("containerId", "NULL");
        getDb().insert(getCollection(), dbObject);
        return (ObjectId) dbObject.get("_id");
    }

    @Override
    public void removeEntity(DCBungee entity) {
        getDb().delete(getCollection(), new BasicDBObject("_id", entity.get_id()));
    }
}
