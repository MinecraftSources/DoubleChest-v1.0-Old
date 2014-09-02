package io.minestack.db.database;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import io.minestack.db.entity.DCServerType;
import io.minestack.db.entity.DCWorld;
import io.minestack.db.entity.DCPlugin;
import io.minestack.db.mongo.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;

import java.util.ArrayList;

@Log4j2
public class ServerTypeLoader extends EntityLoader<DCServerType> {

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
    public DCServerType loadEntity(ObjectId _id) {
        if (_id == null) {
            log.error("Error loading server type. _id null");
            return null;
        }
        DBObject dbObject = getDb().findOne(getCollection(), new BasicDBObject("_id", _id));
        if (dbObject != null) {
            DCServerType serverType = new DCServerType();
            serverType.set_id(_id);
            serverType.setName((String) dbObject.get("name"));
            serverType.setAmount((Integer) dbObject.get("amount"));
            serverType.setMemory((Integer) dbObject.get("memory"));
            serverType.setPlayers((Integer) dbObject.get("players"));
            serverType.setDisabled((Boolean) dbObject.get("disabled"));

            //log.info("Loading "+serverType.getName()+" plugins");
            BasicDBList plugins = (BasicDBList) dbObject.get("plugins");
            for (Object obj : plugins) {
                DBObject dbObj = (DBObject) obj;
                ObjectId _pluginId = (ObjectId) dbObj.get("_id");
                DCPlugin plugin = pluginLoader.loadEntity(_pluginId);
                if (plugin == null) {
                    log.error("Error loading plugin for server "+serverType.getName());
                    return null;
                }

                if (plugin.getType() != DCPlugin.PluginType.BUKKIT) {
                    log.error("Trying to add Non-Bukkit plugin "+plugin.getName()+" to server "+serverType.getName());
                    return null;
                }
                DCPlugin.PluginConfig pluginConfig = null;
                if (dbObj.containsField("_configId")) {
                    ObjectId _configId = (ObjectId) dbObj.get("_configId");
                    pluginConfig = plugin.getConfigs().get(_configId);
                    if (pluginConfig == null) {
                        log.error("Plugin config " + _configId + " does not exist for plugin " + plugin.getName());
                        return null;
                    }
                }
                serverType.getPlugins().put(plugin, pluginConfig);
            }

            //log.info("Loading "+serverType.getName()+" worlds");
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

            //log.info("Loaded Type "+serverType.getName());
            return serverType;
        }
        log.error("Unknown Server Type "+_id.toString());
        return null;
    }

    @Override
    public void saveEntity(DCServerType serverType) {
        BasicDBObject values = new BasicDBObject();

        values.put("name", serverType.getName());
        values.put("players", serverType.getPlayers());
        values.put("memory", serverType.getMemory());
        values.put("amount", serverType.getAmount());
        values.put("disabled", serverType.isDisabled());

        BasicDBList plugins = new BasicDBList();
        for (DCPlugin plugin : serverType.getPlugins().keySet()) {
            BasicDBObject object = new BasicDBObject();
            object.put("_id", plugin.get_id());
            DCPlugin.PluginConfig pluginConfig = serverType.getPlugins().get(plugin);
            if (pluginConfig != null) {
                object.put("_configId", pluginConfig.get_id());
            }
            plugins.add(object);
        }
        values.put("plugins", plugins);

        BasicDBList worlds = new BasicDBList();
        for (DCWorld world : serverType.getWorlds()) {
            BasicDBObject object = new BasicDBObject();
            object.put("_id", world.get_id());
            if (serverType.getDefaultWorld() == world) {
                object.put("isDefault", true);
            } else {
                object.put("isDefault", false);
            }
            worlds.add(object);
        }
        values.put("worlds", worlds);

        BasicDBObject set = new BasicDBObject("$set", values);
        getDb().updateDocument(getCollection(), new BasicDBObject("_id", serverType.get_id()), set);
        log.info("Saving Server Type "+serverType.getName());
    }

    @Override
    public ObjectId insertEntity(DCServerType serverType) {
        BasicDBObject dbObject = new BasicDBObject("_id", new ObjectId());

        dbObject.put("name", serverType.getName());
        dbObject.put("players", serverType.getPlayers());
        dbObject.put("memory", serverType.getMemory());
        dbObject.put("amount", serverType.getAmount());
        dbObject.put("disabled", serverType.isDisabled());

        BasicDBList plugins = new BasicDBList();
        for (DCPlugin plugin : serverType.getPlugins().keySet()) {
            BasicDBObject object = new BasicDBObject();
            object.put("_id", plugin.get_id());
            DCPlugin.PluginConfig pluginConfig = serverType.getPlugins().get(plugin);
            if (pluginConfig != null) {
                object.put("_configId", pluginConfig.get_id());
            }
            plugins.add(object);
        }
        dbObject.put("plugins", plugins);

        BasicDBList worlds = new BasicDBList();
        for (DCWorld world : serverType.getWorlds()) {
            BasicDBObject object = new BasicDBObject();
            object.put("_id", world.get_id());
            if (serverType.getDefaultWorld() == world) {
                object.put("isDefault", true);
            } else {
                object.put("isDefault", false);
            }
            worlds.add(object);
        }
        dbObject.put("worlds", worlds);

        getDb().insert(getCollection(), dbObject);
        return (ObjectId) dbObject.get("_id");
    }

    @Override
    public void removeEntity(DCServerType entity) {
        getDb().remove(getCollection(), new BasicDBObject("_id", entity.get_id()));
    }
}
