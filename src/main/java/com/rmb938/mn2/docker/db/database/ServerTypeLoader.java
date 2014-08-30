package com.rmb938.mn2.docker.db.database;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.rmb938.mn2.docker.db.entity.MN2Plugin;
import com.rmb938.mn2.docker.db.entity.MN2ServerType;
import com.rmb938.mn2.docker.db.entity.MN2World;
import com.rmb938.mn2.docker.db.mongo.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;

import java.util.AbstractMap;
import java.util.ArrayList;

@Log4j2
public class ServerTypeLoader extends EntityLoader<MN2ServerType> {

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

    public ArrayList<MN2ServerType> getTypes() {
        ArrayList<MN2ServerType> types = new ArrayList<>();
        DBCursor dbCursor = getDb().findMany(getCollection());
        while (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            MN2ServerType type = loadEntity((ObjectId)dbObject.get("_id"));
            if (type != null) {
                types.add(type);
            }
        }
        dbCursor.close();
        return types;
    }

    @Override
    public MN2ServerType loadEntity(ObjectId _id) {
        if (_id == null) {
            log.error("Error loading server type. _id null");
            return null;
        }
        DBObject dbObject = getDb().findOne(getCollection(), new BasicDBObject("_id", _id));
        if (dbObject != null) {
            MN2ServerType serverType = new MN2ServerType();
            serverType.set_id(_id);
            serverType.setName((String) dbObject.get("name"));
            serverType.setAmount((Integer) dbObject.get("amount"));
            serverType.setMemory((Integer) dbObject.get("memory"));
            serverType.setPlayers((Integer) dbObject.get("players"));

            //log.info("Loading "+serverType.getName()+" plugins");
            BasicDBList plugins = (BasicDBList) dbObject.get("plugins");
            for (Object obj : plugins) {
                DBObject dbObj = (DBObject) obj;
                ObjectId _pluginId = (ObjectId) dbObj.get("_id");
                MN2Plugin plugin = pluginLoader.loadEntity(_pluginId);
                if (plugin == null) {
                    log.error("Error loading plugin for server "+serverType.getName());
                    return null;
                }

                if (plugin.getType() != MN2Plugin.PluginType.BUKKIT) {
                    log.error("Trying to add Non-Bukkit plugin "+plugin.getName()+" to server "+serverType.getName());
                    return null;
                }
                MN2Plugin.PluginConfig pluginConfig = null;
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
                MN2World world = worldLoader.loadEntity(_worldId);
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
    public void saveEntity(MN2ServerType serverType) {
        BasicDBObject values = new BasicDBObject();

        values.put("name", serverType.getName());
        values.put("players", serverType.getPlayers());
        values.put("memory", serverType.getMemory());
        values.put("amount", serverType.getAmount());

        BasicDBList plugins = new BasicDBList();
        for (MN2Plugin plugin : serverType.getPlugins().keySet()) {
            BasicDBObject object = new BasicDBObject();
            object.put("_id", plugin.get_id());
            MN2Plugin.PluginConfig pluginConfig = serverType.getPlugins().get(plugin);
            if (pluginConfig != null) {
                object.put("_configId", pluginConfig.get_id());
            }
            plugins.add(object);
        }
        values.put("plugins", plugins);

        BasicDBList worlds = new BasicDBList();
        for (MN2World world : serverType.getWorlds()) {
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
    public ObjectId insertEntity(MN2ServerType serverType) {
        BasicDBObject dbObject = new BasicDBObject("_id", new ObjectId());

        dbObject.put("name", serverType.getName());
        dbObject.put("players", serverType.getPlayers());
        dbObject.put("memory", serverType.getMemory());
        dbObject.put("amount", serverType.getAmount());

        BasicDBList plugins = new BasicDBList();
        for (MN2Plugin plugin : serverType.getPlugins().keySet()) {
            BasicDBObject object = new BasicDBObject();
            object.put("_id", plugin.get_id());
            MN2Plugin.PluginConfig pluginConfig = serverType.getPlugins().get(plugin);
            if (pluginConfig != null) {
                object.put("_configId", pluginConfig.get_id());
            }
            plugins.add(object);
        }
        dbObject.put("plugins", plugins);

        BasicDBList worlds = new BasicDBList();
        for (MN2World world : serverType.getWorlds()) {
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
    public void removeEntity(MN2ServerType entity) {
        getDb().remove(getCollection(), new BasicDBObject("_id", entity.get_id()));
    }
}
