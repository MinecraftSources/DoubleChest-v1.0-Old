package com.rmb938.mn2.docker.db.database;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.rmb938.mn2.docker.db.entity.Plugin;
import com.rmb938.mn2.docker.db.entity.PluginConfig;
import com.rmb938.mn2.docker.db.mongo.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;

@Log4j2
public class PluginLoader extends EntityLoader<Plugin> {

    public PluginLoader(MongoDatabase db) {
        super(db, "plugins");
    }

    @Override
    public Plugin loadEntity(ObjectId _id) {
        if (_id == null) {
            log.error("Error loading plugin. _id null");
            return null;
        }
        DBObject dbObject = getDb().findOne(getCollection(), new BasicDBObject("_id", _id));
        if (dbObject != null) {
            Plugin plugin = new Plugin();
            plugin.set_id(_id);
            plugin.setName((String) dbObject.get("name"));
            plugin.setBaseFolder((String) dbObject.get("baseFolder"));
            plugin.setConfigFolder((String) dbObject.get("configFolder"));
            try {
                plugin.setType(Plugin.PluginType.valueOf((String) dbObject.get("type")));
            } catch (Exception ex) {
                log.info("Invalid Plugin Type for plugin "+plugin.getName());
                return null;
            }

            BasicDBList configs = (BasicDBList) dbObject.get("configs");
            for (Object obj : configs) {
                DBObject dbObj = (DBObject) obj;
                PluginConfig pluginConfig = new PluginConfig();
                pluginConfig.setName((String) dbObj.get("name"));
                pluginConfig.setLocation((String) dbObj.get("location"));

                plugin.getConfigs().put((ObjectId)dbObj.get("_id"), pluginConfig);
            }
            return plugin;
        }
        //log.info("Unknown Plugin "+_id.toString());
        return null;
    }

    @Override
    public void saveEntity(Plugin plugin) {

    }

    @Override
    public ObjectId insertEntity(Plugin plugin) {
        return null;
    }
}
