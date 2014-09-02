package io.minestack.db.database;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import io.minestack.db.entity.DCBungeeType;
import io.minestack.db.entity.DCNode;
import io.minestack.db.mongo.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;

import java.util.ArrayList;

@Log4j2
public class NodeLoader extends EntityLoader<DCNode> {

    private final BungeeTypeLoader bungeeTypeLoader;

    public NodeLoader(MongoDatabase db, BungeeTypeLoader bungeeTypeLoader) {
        super(db, "nodes");
        this.bungeeTypeLoader = bungeeTypeLoader;
    }

    public DCNode getMaster() {
        DBCursor dbCursor = getDb().findMany(getCollection(), new BasicDBObject("lastUpdate", new BasicDBObject("$gt", System.currentTimeMillis()-30000)));
        dbCursor = dbCursor.sort(new BasicDBObject("_id", 1));
        if (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            DCNode node = loadEntity((ObjectId)dbObject.get("_id"));
            dbCursor.close();
            return node;
        }
        dbCursor.close();
        return null;
    }

    public ArrayList<DCNode> getNodes() {
        ArrayList<DCNode> nodes = new ArrayList<>();
        DBCursor dbCursor = getDb().findMany(getCollection());
        while (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            DCNode node = loadEntity((ObjectId)dbObject.get("_id"));
            if (node != null) {
                nodes.add(node);
            }
        }
        dbCursor.close();
        return nodes;
    }

    public ArrayList<DCNode> getOnlineNodes() {
        ArrayList<DCNode> nodes = new ArrayList<>();
        DBCursor dbCursor = getDb().findMany(getCollection(), new BasicDBObject("lastUpdate", new BasicDBObject("$gt", System.currentTimeMillis()-30000)));
        while (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            DCNode node = loadEntity((ObjectId)dbObject.get("_id"));
            if (node != null) {
                nodes.add(node);
            }
        }
        dbCursor.close();
        return nodes;
    }

    @Override
    public DCNode loadEntity(ObjectId _id) {
        if (_id == null) {
            log.error("Error loading node. _id null");
            return null;
        }
        DBObject dbObject = getDb().findOne(getCollection(), new BasicDBObject("_id", _id));
        if (dbObject != null) {
            DCNode node = new DCNode();
            node.set_id(_id);
            node.setAddress((String) dbObject.get("host"));
            node.setRam((Integer) dbObject.get("ram"));
            Object lastUpdate = dbObject.get("lastUpdate");
            if (lastUpdate instanceof Integer) {
                node.setLastUpdate(((Integer)lastUpdate).longValue());
            } else {
                node.setLastUpdate((Long) lastUpdate);
            }

            ObjectId _bungeeTypeId = (ObjectId) dbObject.get("_bungeeType");
            if (_bungeeTypeId != null) {
                DCBungeeType bungeeType = bungeeTypeLoader.loadEntity(_bungeeTypeId);
                node.setBungeeType(bungeeType);
            }

            return node;
        }
        log.info("Unknown Node "+_id.toString());
        return null;
    }

    @Override
    public void saveEntity(DCNode node) {
        BasicDBObject values = new BasicDBObject();

        values.put("ram", node.getRam());
        if (node.getBungeeType() != null) {
            values.put("_bungeeType", node.getBungeeType().get_id());
        } else {
            values.put("_bungeeType", null);
        }

        BasicDBObject set = new BasicDBObject("$set", values);
        getDb().updateDocument(getCollection(), new BasicDBObject("_id", node.get_id()), set);
        log.info("Saving Node " + node.getAddress());
    }

    @Override
    public ObjectId insertEntity(DCNode node) {
        BasicDBObject dbObject = new BasicDBObject("_id", new ObjectId());

        dbObject.put("host", node.getAddress());
        dbObject.put("ram", node.getRam());
        dbObject.put("lastUpdate", node.getLastUpdate());
        if (node.getBungeeType() != null) {
            dbObject.put("_bungeeType", node.getBungeeType().get_id());
        } else {
            dbObject.put("_bungeeType", null);
        }
        getDb().insert(getCollection(), dbObject);
        return (ObjectId)dbObject.get("_id");
    }

    @Override
    public void removeEntity(DCNode entity) {
        getDb().delete(getCollection(), new BasicDBObject("_id", entity.get_id()));
    }
}
