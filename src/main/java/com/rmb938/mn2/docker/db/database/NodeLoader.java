package com.rmb938.mn2.docker.db.database;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.rmb938.mn2.docker.db.entity.MN2Node;
import com.rmb938.mn2.docker.db.mongo.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;

@Log4j2
public class NodeLoader extends EntityLoader<MN2Node> {

    public NodeLoader(MongoDatabase db) {
        super(db, "nodes");
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
        return null;
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
            node.setAddress((String) dbObject.get("host"));
            node.setRam((Integer) dbObject.get("ram"));
            Object lastUpdate = dbObject.get("lastUpdate");
            if (lastUpdate instanceof Integer) {
                node.setLastUpdate(((Integer)lastUpdate).longValue());
            } else {
                node.setLastUpdate((Long) lastUpdate);
            }

            return node;
        }
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
