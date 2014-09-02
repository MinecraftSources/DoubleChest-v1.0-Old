package io.minestack.db.database;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import io.minestack.db.entity.MN2BungeeType;
import io.minestack.db.entity.MN2Player;
import io.minestack.db.entity.MN2ServerType;
import io.minestack.db.mongo.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;

import java.util.UUID;

@Log4j2
public class PlayerLoader extends EntityLoader<MN2Player> {

    private final ServerTypeLoader serverTypeLoader;
    private final BungeeTypeLoader bungeeTypeLoader;

    public PlayerLoader(MongoDatabase db, ServerTypeLoader serverTypeLoader, BungeeTypeLoader bungeeTypeLoader) {
        super(db, "players");
        this.serverTypeLoader = serverTypeLoader;
        this.bungeeTypeLoader = bungeeTypeLoader;

        //name index
        BasicDBObject index = new BasicDBObject();
        index.put("name", 1);
        getDb().createIndex(getCollection(), index);

        //uuid index
        index.clear();
        index.put("uuid", 1);
        getDb().createIndex(getCollection(), index);
    }

    public MN2Player loadPlayer(UUID uuid) {
        if (uuid == null) {
            log.error("Error loading player. uuid null");
            return null;
        }
        DBObject dbObject = getDb().findOne(getCollection(), new BasicDBObject("uuid", uuid.toString()));
        if (dbObject != null) {
            return loadEntity((ObjectId) dbObject.get("_id"));
        }
        return null;
    }

    @Override
    public MN2Player loadEntity(ObjectId _id) {
        if (_id == null) {
            log.error("Error loading player. _id null");
            return null;
        }
        DBObject dbObject = getDb().findOne(getCollection(), new BasicDBObject("_id", _id));
        if (dbObject != null) {
            MN2Player player = new MN2Player();
            player.set_id(_id);
            player.setUuid(UUID.fromString((String) dbObject.get("uuid")));
            player.setPlayerName((String)dbObject.get("name"));

            BasicDBList lastServerTypes = (BasicDBList) dbObject.get("lastServerTypes");
            for (Object obj : lastServerTypes) {
                BasicDBObject lastType = (BasicDBObject) obj;
                MN2BungeeType bungeeType = bungeeTypeLoader.loadEntity((ObjectId)lastType.get("_bungeeType"));
                MN2ServerType serverType = serverTypeLoader.loadEntity((ObjectId)lastType.get("_serverType"));
                if (bungeeType != null && serverType != null) {
                    player.getLastServerTypes().put(bungeeType, serverType);
                }
            }

            return player;
        }
        return null;
    }

    @Override
    public void saveEntity(MN2Player entity) {
        BasicDBObject values = new BasicDBObject();
        values.put("name", entity.getPlayerName());

        BasicDBList lastServerTypes = new BasicDBList();
        for (MN2BungeeType bungeeType : entity.getLastServerTypes().keySet()) {
            MN2ServerType serverType = entity.getLastServerTypes().get(bungeeType);
            BasicDBObject lastType = new BasicDBObject();
            lastType.put("_bungeeType", bungeeType.get_id());
            lastType.put("_serverType", serverType.get_id());
            lastServerTypes.add(lastType);
        }
        values.put("lastServerTypes", lastServerTypes);

        BasicDBObject set = new BasicDBObject("$set", values);
        getDb().updateDocument(getCollection(), new BasicDBObject("_id", entity.get_id()), set);
        log.info("Saving Player " + entity.getPlayerName());
    }

    @Override
    public ObjectId insertEntity(MN2Player entity) {
        BasicDBObject dbObject = new BasicDBObject("_id", new ObjectId());

        dbObject.put("uuid", entity.getUuid().toString());
        dbObject.put("name", entity.getPlayerName());
        dbObject.put("lastServerTypes", new BasicDBList());

        getDb().insert(getCollection(), dbObject);

        return (ObjectId)dbObject.get("_id");
    }

    @Override
    public void removeEntity(MN2Player entity) {

    }
}
