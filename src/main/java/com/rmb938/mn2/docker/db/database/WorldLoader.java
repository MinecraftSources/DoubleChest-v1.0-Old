package com.rmb938.mn2.docker.db.database;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.rmb938.mn2.docker.db.entity.MN2World;
import com.rmb938.mn2.docker.db.mongo.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;

@Log4j2
public class WorldLoader extends EntityLoader<MN2World> {

    public WorldLoader(MongoDatabase db) {
        super(db, "worlds");
    }

    @Override
    public MN2World loadEntity(ObjectId _id) {
        if (_id == null) {
            log.error("Error loading world. _id null");
            return null;
        }
        DBObject dbObject = getDb().findOne(getCollection(), new BasicDBObject("_id", _id));
        if (dbObject != null) {
            MN2World world = new MN2World();
            world.set_id(_id);
            world.setDbObject(dbObject);
            world.setName((String) dbObject.get("name"));
            world.setFolder((String) dbObject.get("folder"));
            try {
                world.setEnvironment(MN2World.Environment.valueOf((String) dbObject.get("environment")));
            } catch (Exception ex) {
                log.error("Invalid environment for world "+world.getName());
                return null;
            }
            world.setGenerator((String) dbObject.get("generator"));

            //log.info("Loaded World "+world.getName());
            return world;
        }
        log.info("Unknown World "+_id.toString());
        return null;
    }

    @Override
    public void saveEntity(MN2World world) {

    }

    @Override
    public ObjectId insertEntity(MN2World world) {
        return null;
    }

    @Override
    public void removeEntity(MN2World entity) {

    }
}
