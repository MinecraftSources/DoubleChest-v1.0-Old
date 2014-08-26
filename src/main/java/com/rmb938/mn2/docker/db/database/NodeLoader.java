package com.rmb938.mn2.docker.db.database;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.rmb938.mn2.docker.db.entity.MN2BungeeType;
import com.rmb938.mn2.docker.db.entity.MN2Node;
import com.rmb938.mn2.docker.db.mongo.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;

import java.util.ArrayList;

@Log4j2
public class NodeLoader extends EntityLoader<MN2Node> {

    private final BungeeTypeLoader bungeeTypeLoader;

    public NodeLoader(MongoDatabase db, BungeeTypeLoader bungeeTypeLoader) {
        super(db, "nodes");
        this.bungeeTypeLoader = bungeeTypeLoader;
    }

    public MN2Node getMaster() {
        DBCursor dbCursor = getDb().findMany(getCollection(), new BasicDBObject("lastUpdate", new BasicDBObject("$gt", System.currentTimeMillis()-30000)));
        dbCursor = dbCursor.sort(new BasicDBObject("_id", 1));
        if (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            MN2Node node = loadEntity((ObjectId)dbObject.get("_id"));
            dbCursor.close();
            return node;
        }
        dbCursor.close();
        return null;
    }

    public ArrayList<MN2Node> getNodes() {
        ArrayList<MN2Node> nodes = new ArrayList<>();
        DBCursor dbCursor = getDb().findMany(getCollection());
        if (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            MN2Node node = loadEntity((ObjectId)dbObject.get("_id"));
            if (node != null) {
                nodes.add(node);
            }
        }
        dbCursor.close();
        return nodes;
    }

    public ArrayList<MN2Node> getOnlineNodes() {
        ArrayList<MN2Node> nodes = new ArrayList<>();
        DBCursor dbCursor = getDb().findMany(getCollection(), new BasicDBObject("lastUpdate", new BasicDBObject("$gt", System.currentTimeMillis()-30000)));
        if (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            MN2Node node = loadEntity((ObjectId)dbObject.get("_id"));
            if (node != null) {
                nodes.add(node);
            }
        }
        dbCursor.close();
        return nodes;
    }

    @Override
    public MN2Node loadEntity(ObjectId _id) {
        if (_id == null) {
            log.error("Error loading node. _id null");
            return null;
        }
        DBObject dbObject = getDb().findOne(getCollection(), new BasicDBObject("_id", _id));
        if (dbObject != null) {
            MN2Node node = new MN2Node();
            node.set_id(_id);
            node.setDbObject(dbObject);
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
                MN2BungeeType bungeeType = bungeeTypeLoader.loadEntity(_bungeeTypeId);
                node.setBungeeType(bungeeType);
            }

            return node;
        }
        log.info("Unknown Node "+_id.toString());
        return null;
    }

    @Override
    public void saveEntity(MN2Node node) {

    }

    @Override
    public ObjectId insertEntity(MN2Node node) {
        return null;
    }

    @Override
    public void removeEntity(MN2Node entity) {

    }
}
