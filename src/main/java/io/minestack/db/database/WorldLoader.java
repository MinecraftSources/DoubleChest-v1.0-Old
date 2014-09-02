package io.minestack.db.database;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import io.minestack.db.entity.UWorld;
import io.minestack.db.mongo.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;

import java.util.ArrayList;

@Log4j2
public class WorldLoader extends EntityLoader<UWorld> {

    public WorldLoader(MongoDatabase db) {
        super(db, "worlds");
    }

    public ArrayList<UWorld> getWorlds() {
        ArrayList<UWorld> worlds = new ArrayList<>();
        DBCursor dbCursor = getDb().findMany(getCollection());
        while (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            UWorld world = loadEntity((ObjectId) dbObject.get("_id"));
            if (world != null) {
                worlds.add(world);
            }
        }

        dbCursor.close();
        return worlds;
    }

    @Override
    public UWorld loadEntity(ObjectId _id) {
        if (_id == null) {
            log.error("Error loading world. _id null");
            return null;
        }
        DBObject dbObject = getDb().findOne(getCollection(), new BasicDBObject("_id", _id));
        if (dbObject != null) {
            UWorld world = new UWorld();
            world.set_id(_id);
            world.setName((String) dbObject.get("name"));
            world.setFolder((String) dbObject.get("folder"));
            try {
                world.setEnvironment(UWorld.Environment.valueOf((String) dbObject.get("environment")));
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
    public void saveEntity(UWorld world) {
        BasicDBObject values = new BasicDBObject();

        values.put("name", world.getName());
        values.put("folder", world.getFolder());
        values.put("environment", world.getEnvironment().name());
        values.put("generator", world.getGenerator());

        BasicDBObject set = new BasicDBObject("$set", values);
        getDb().updateDocument(getCollection(), new BasicDBObject("_id", world.get_id()), set);
        log.info("Saving World "+world.getName());
    }

    @Override
    public ObjectId insertEntity(UWorld world) {
        BasicDBObject dbObject = new BasicDBObject("_id", new ObjectId());

        dbObject.put("name", world.getName());
        dbObject.put("folder", world.getFolder());
        dbObject.put("environment", world.getEnvironment().name());
        dbObject.put("generator", world.getGenerator());

        getDb().insert(getCollection(), dbObject);
        return (ObjectId) dbObject.get("_id");
    }

    @Override
    public void removeEntity(UWorld entity) {
        getDb().remove(getCollection(), new BasicDBObject("_id", entity.get_id()));
    }
}
