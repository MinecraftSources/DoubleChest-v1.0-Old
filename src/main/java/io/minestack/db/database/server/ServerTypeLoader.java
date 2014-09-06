package io.minestack.db.database.server;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import io.minestack.db.database.driver.DrivableEntityLoader;
import io.minestack.db.database.driver.DriverLoader;
import io.minestack.db.database.plugin.PluginLoader;
import io.minestack.db.database.server.driver.bukkit.BukkitTypeDriverLoader;
import io.minestack.db.database.world.WorldLoader;
import io.minestack.db.entity.DCWorld;
import io.minestack.db.entity.server.DCServerType;
import io.minestack.db.mongo.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;

import java.util.ArrayList;

@Log4j2
public class ServerTypeLoader extends DrivableEntityLoader<DCServerType> {

    private final PluginLoader pluginLoader;
    private final WorldLoader worldLoader;

    public ServerTypeLoader(MongoDatabase db, PluginLoader pluginLoader, WorldLoader worldLoader) {
        super(db, "servertypes");
        this.pluginLoader = pluginLoader;
        this.worldLoader = worldLoader;

        //name index
        BasicDBObject index = new BasicDBObject();
        index.put("name", 1);
        getDb().createIndex(getCollection(), index);

        DriverLoader driverLoader = new BukkitTypeDriverLoader(db, getCollection(), pluginLoader);
        getDrivers().put(driverLoader.getDriverName(), driverLoader);
    }

    public ArrayList<DCServerType> getTypes() {
        ArrayList<DCServerType> types = new ArrayList<>();
        DBCursor dbCursor = getDb().findMany(getCollection());
        while (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            DCServerType type = loadEntity((ObjectId)dbObject.get("_id"));
            if (type != null) {
                types.add(type);
            }
        }
        dbCursor.close();
        return types;
    }

    public DCServerType getType(String typeName) {
        if (typeName == null) {
            return null;
        }
        DBObject dbObject = getDb().findOne(getCollection(), new BasicDBObject("name", typeName));
        if (dbObject != null) {
            return loadEntity((ObjectId) dbObject.get("_id"));
        }
        return null;
    }

    @Override
    protected DCServerType loadDrivableEntity(DBObject dbObject) {
        DCServerType serverType = new DCServerType();
        serverType.set_id((ObjectId) dbObject.get("_id"));
        serverType.setName((String) dbObject.get("name"));
        serverType.setAmount((Integer) dbObject.get("amount"));
        serverType.setMemory((Integer) dbObject.get("memory"));
        serverType.setPlayers((Integer) dbObject.get("players"));
        serverType.setDisabled((Boolean) dbObject.get("disabled"));

        BasicDBList worlds = (BasicDBList) dbObject.get("worlds");
        for (Object obj : worlds) {
            DBObject dbObj = (DBObject) obj;
            ObjectId _worldId = (ObjectId) dbObj.get("_id");
            //log.info("Loading world "+_worldId);
            DCWorld world = worldLoader.loadEntity(_worldId);
            if (world == null) {
                log.error("Error loading world for server "+serverType.getName());
                return null;
            }
            serverType.getWorlds().add(world);

            boolean defaultWorld = (Boolean) dbObj.get("isDefault");
            if (defaultWorld) {
                serverType.setDefaultWorld(world);
            }
        }

        if (serverType.getDefaultWorld() == null) {
            log.error("No default world for server type "+serverType.getName());
            return null;
        }

        return serverType;
    }

    @Override
    protected void saveDrivableEntity(DCServerType entity, DBObject entityObject) {
        entityObject.put("name", entity.getName());
        entityObject.put("players", entity.getPlayers());
        entityObject.put("memory", entity.getMemory());
        entityObject.put("amount", entity.getAmount());
        entityObject.put("disabled", entity.isDisabled());

        BasicDBList worlds = new BasicDBList();
        for (DCWorld world : entity.getWorlds()) {
            BasicDBObject object = new BasicDBObject();
            object.put("_id", world.get_id());
            if (entity.getDefaultWorld() == world) {
                object.put("isDefault", true);
            } else {
                object.put("isDefault", false);
            }
            worlds.add(object);
        }
        entityObject.put("worlds", worlds);
    }

    @Override
    protected void insertDrivableEntity(DCServerType entity, DBObject entityObject) {
        entityObject.put("name", entity.getName());
        entityObject.put("players", entity.getPlayers());
        entityObject.put("memory", entity.getMemory());
        entityObject.put("amount", entity.getAmount());
        entityObject.put("disabled", entity.isDisabled());

        BasicDBList worlds = new BasicDBList();
        for (DCWorld world : entity.getWorlds()) {
            BasicDBObject object = new BasicDBObject();
            object.put("_id", world.get_id());
            if (entity.getDefaultWorld() == world) {
                object.put("isDefault", true);
            } else {
                object.put("isDefault", false);
            }
            worlds.add(object);
        }
        entityObject.put("worlds", worlds);
    }
}
