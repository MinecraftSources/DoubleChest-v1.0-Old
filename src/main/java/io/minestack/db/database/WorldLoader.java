package io.minestack.db.database;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import io.minestack.db.entity.MN2World;
import io.minestack.db.mongo.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;

import java.util.ArrayList;

@Log4j2
public class WorldLoader extends EntityLoader<MN2World> {

    public WorldLoader(MongoDatabase db) {
        super(db, "worlds");
    }

    public ArrayList<MN2World> getWorlds() {
        ArrayList<MN2World> worlds = new ArrayList<>();
        DBCursor dbCursor = getDb().findMany(getCollection());
        while (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            MN2World world = loadEntity((ObjectId) dbObject.get("_id"));
            if (world != null) {
                worlds.add(world);
            }
        }

        dbCursor.close();
        return worlds;
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
    public ObjectId insertEntity(MN2World world) {
        BasicDBObject dbObject = new BasicDBObject("_id", new ObjectId());

        dbObject.put("name", world.getName());
        dbObject.put("folder", world.getFolder());
        dbObject.put("environment", world.getEnvironment().name());
        dbObject.put("generator", world.getGenerator());

        getDb().insert(getCollection(), dbObject);
        return (ObjectId) dbObject.get("_id");
    }

    @Override
    public void removeEntity(MN2World entity) {
        getDb().remove(getCollection(), new BasicDBObject("_id", entity.get_id()));
    }
}
