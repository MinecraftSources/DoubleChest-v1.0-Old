package com.rmb938.mn2.docker.db.database;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.rmb938.mn2.docker.db.entity.*;
import com.rmb938.mn2.docker.db.mongo.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;

import java.util.AbstractMap;
import java.util.ArrayList;

@Log4j2
public class BungeeTypeLoader extends EntityLoader<MN2BungeeType> {

    private final PluginLoader pluginLoader;
    private final ServerTypeLoader serverTypeLoader;

    public BungeeTypeLoader(MongoDatabase db, PluginLoader pluginLoader, ServerTypeLoader serverTypeLoader) {
        super(db, "bungeetypes");
        this.pluginLoader = pluginLoader;
        this.serverTypeLoader = serverTypeLoader;
    }

    public ArrayList<MN2BungeeType> getTypes() {
        ArrayList<MN2BungeeType> types = new ArrayList<>();
        DBCursor dbCursor = getDb().findMany(getCollection());
        while (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            MN2BungeeType type = loadEntity((ObjectId)dbObject.get("_id"));
            if (type != null) {
                types.add(type);
            }
        }
        dbCursor.close();
        return types;
    }

    @Override
    public MN2BungeeType loadEntity(ObjectId _id) {
        if (_id == null) {
            log.error("Error loading bungee. _id null");
            return null;
        }
        DBObject dbObject = getDb().findOne(getCollection(), new BasicDBObject("_id", _id));
        if (dbObject != null) {
            MN2BungeeType bungeeType = new MN2BungeeType();
            bungeeType.set_id(_id);
            bungeeType.setName((String) dbObject.get("name"));

            BasicDBList plugins = (BasicDBList) dbObject.get("plugins");
            for (Object obj : plugins) {
                DBObject dbObj = (DBObject) obj;
                ObjectId _pluginId = (ObjectId) dbObj.get("_id");
                MN2Plugin plugin = pluginLoader.loadEntity(_pluginId);
                if (plugin == null) {
                    log.error("Error loading plugin for bungee "+bungeeType.getName());
                    return null;
                }

                if (plugin.getType() != MN2Plugin.PluginType.BUNGEE) {
                    log.error("Trying to add Non-Bungee plugin "+plugin.getName()+" to bungee "+bungeeType.getName());
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
                bungeeType.getPlugins().put(plugin, pluginConfig);
            }

            BasicDBList servertypes = (BasicDBList) dbObject.get("servertypes");
            for (Object obj : servertypes) {
                DBObject dbObj = (DBObject) obj;
                ObjectId _servertype = (ObjectId) dbObj.get("_id");
                MN2ServerType serverType = serverTypeLoader.loadEntity(_servertype);
                if (serverType == null) {
                    log.error("Error loading server type for bungee "+bungeeType.getName());
                    return null;
                }
                bungeeType.getServerTypes().add(serverType);

                boolean defaultType = (Boolean) dbObj.get("isDefault");
                if (defaultType) {
                    bungeeType.setDefaultType(serverType);
                }
            }

            if (bungeeType.getDefaultType() == null) {
                log.error("Error loading default server type for bungee "+bungeeType.getName());
                return null;
            }
            return bungeeType;
        }
        log.info("Unknown Bungee Type "+_id.toString());
        return null;
    }

    @Override
    public void saveEntity(MN2BungeeType entity) {
        BasicDBObject values = new BasicDBObject();

        values.put("name", entity.getName());

        BasicDBList serverTypes = new BasicDBList();
        for (MN2ServerType serverType : entity.getServerTypes()) {
            BasicDBObject object = new BasicDBObject();
            object.put("_id", serverType.get_id());
            if (serverType == entity.getDefaultType()) {
                object.put("isDefault", true);
            } else {
                object.put("isDefault", false);
            }
            serverTypes.add(object);
        }
        values.put("servertypes", serverTypes);

        BasicDBList plugins = new BasicDBList();
        for (MN2Plugin plugin : entity.getPlugins().keySet()) {
            BasicDBObject object = new BasicDBObject();
            object.put("_id", plugin.get_id());
            MN2Plugin.PluginConfig pluginConfig = entity.getPlugins().get(plugin);
            if (pluginConfig != null) {
                object.put("_configId", pluginConfig.get_id());
            }
            plugins.add(object);
        }
        values.put("plugins", plugins);

        BasicDBObject set = new BasicDBObject("$set", values);
        getDb().updateDocument(getCollection(), new BasicDBObject("_id", entity.get_id()), set);
        log.info("Saving Bungee Type "+entity.getName());
    }

    @Override
    public ObjectId insertEntity(MN2BungeeType entity) {
        BasicDBObject dbObject = new BasicDBObject("_id", new ObjectId());

        dbObject.put("name", entity.getName());

        BasicDBList serverTypes = new BasicDBList();
        for (MN2ServerType serverType : entity.getServerTypes()) {
            BasicDBObject object = new BasicDBObject();
            object.put("_id", serverType.get_id());
            if (serverType == entity.getDefaultType()) {
                object.put("isDefault", true);
            } else {
                object.put("isDefault", false);
            }
            serverTypes.add(object);
        }
        dbObject.put("servertypes", serverTypes);

        BasicDBList plugins = new BasicDBList();
        for (MN2Plugin plugin : entity.getPlugins().keySet()) {
            BasicDBObject object = new BasicDBObject();
            object.put("_id", plugin.get_id());
            MN2Plugin.PluginConfig pluginConfig = entity.getPlugins().get(plugin);
            if (pluginConfig != null) {
                object.put("_configId", pluginConfig.get_id());
            }
            plugins.add(object);
        }
        dbObject.put("plugins", plugins);

        getDb().insert(getCollection(), dbObject);
        return (ObjectId) dbObject.get("_id");
    }

    @Override
    public void removeEntity(MN2BungeeType entity) {
        getDb().remove(getCollection(), new BasicDBObject("_id", entity.get_id()));
    }
}
