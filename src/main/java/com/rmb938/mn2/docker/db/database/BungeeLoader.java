package com.rmb938.mn2.docker.db.database;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.rmb938.mn2.docker.db.entity.MN2Bungee;
import com.rmb938.mn2.docker.db.entity.MN2Server;
import com.rmb938.mn2.docker.db.mongo.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;

@Log4j2
public class BungeeLoader extends EntityLoader<MN2Bungee> {

    private final BungeeTypeLoader bungeeTypeLoader;
    private final NodeLoader nodeLoader;

    public BungeeLoader(MongoDatabase db, BungeeTypeLoader bungeeTypeLoader, NodeLoader nodeLoader) {
        super(db, "bungees");
        this.bungeeTypeLoader = bungeeTypeLoader;
        this.nodeLoader = nodeLoader;
    }

    @Override
    public MN2Bungee loadEntity(ObjectId _id) {
        if (_id == null) {
            log.error("Error loading world. _id null");
            return null;
        }
        DBObject dbObject = getDb().findOne(getCollection(), new BasicDBObject("_id", _id));
        if (dbObject != null) {
            MN2Bungee bungee = new MN2Bungee();
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
    public void saveEntity(MN2Bungee entity) {
        BasicDBObject values = new BasicDBObject();
        values.put("lastUpdate", entity.getLastUpdate());
        values.put("containerId", entity.getContainerId());

        BasicDBObject set = new BasicDBObject("$set", values);
        getDb().updateDocument(getCollection(), new BasicDBObject("_id", entity.get_id()), set);
        log.info("Saving Bungee " + entity.get_id());
    }

    @Override
    public ObjectId insertEntity(MN2Bungee entity) {
        BasicDBObject dbObject = new BasicDBObject("_id", new ObjectId());
        dbObject.append("_bungeetype", entity.getBungeeType().get_id());
        dbObject.append("_node", entity.getNode().get_id());
        dbObject.append("lastUpdate", 0L);
        dbObject.append("containerId", "NULL");
        getDb().insert(getCollection(), dbObject);
        return (ObjectId) dbObject.get("_id");
    }

    @Override
    public void removeEntity(MN2Bungee entity) {
        getDb().delete(getCollection(), new BasicDBObject("_id", entity.get_id()));
    }
}
