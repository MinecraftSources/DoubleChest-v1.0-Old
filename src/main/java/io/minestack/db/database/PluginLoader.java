package io.minestack.db.database;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import io.minestack.db.entity.UPlugin;
import io.minestack.db.mongo.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;

import java.util.ArrayList;

@Log4j2
public class PluginLoader extends EntityLoader<UPlugin> {

    public PluginLoader(MongoDatabase db) {
        super(db, "plugins");
    }

    public ArrayList<UPlugin> loadPlugins() {
        ArrayList<UPlugin> plugins = new ArrayList<>();
        DBCursor dbCursor = getDb().findMany(getCollection());
        while (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            UPlugin plugin = loadEntity((ObjectId) dbObject.get("_id"));
            if (plugin != null) {
                plugins.add(plugin);
            }
        }
        dbCursor.close();
        return plugins;
    }

    public ArrayList<UPlugin> loadPlugins(UPlugin.PluginType pluginType) {
        ArrayList<UPlugin> plugins = new ArrayList<>();
        DBCursor dbCursor = getDb().findMany(getCollection(), new BasicDBObject("type", pluginType.name()));
        while (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            UPlugin plugin = loadEntity((ObjectId) dbObject.get("_id"));
            if (plugin != null) {
                plugins.add(plugin);
            }
        }
        dbCursor.close();
        return plugins;
    }

    @Override
    public UPlugin loadEntity(ObjectId _id) {
        if (_id == null) {
            log.error("Error loading plugin. _id null");
            return null;
        }
        DBObject dbObject = getDb().findOne(getCollection(), new BasicDBObject("_id", _id));
        if (dbObject != null) {
            UPlugin plugin = new UPlugin();
            plugin.set_id(_id);
            plugin.setName((String) dbObject.get("name"));
            plugin.setBaseFolder((String) dbObject.get("baseFolder"));
            plugin.setConfigFolder((String) dbObject.get("configFolder"));
            try {
                plugin.setType(UPlugin.PluginType.valueOf((String) dbObject.get("type")));
            } catch (Exception ex) {
                log.info("Invalid Plugin Type for plugin "+plugin.getName());
                return null;
            }

            BasicDBList configs = (BasicDBList) dbObject.get("configs");
            for (Object obj : configs) {
                DBObject dbObj = (DBObject) obj;
                UPlugin.PluginConfig pluginConfig = plugin.newPluginConfig();
                pluginConfig.set_id((ObjectId) dbObj.get("_id"));
                pluginConfig.setName((String) dbObj.get("name"));
                pluginConfig.setLocation((String) dbObj.get("location"));

                plugin.getConfigs().put(pluginConfig.get_id(), pluginConfig);
            }
            return plugin;
        }
        log.error("Unknown Plugin " + _id.toString());
        return null;
    }

    @Override
    public void saveEntity(UPlugin plugin) {
        BasicDBObject values = new BasicDBObject();

        values.put("name", plugin.getName());
        values.put("type", plugin.getType().name());
        values.put("baseFolder", plugin.getBaseFolder());
        values.put("configFolder", plugin.getConfigFolder());

        BasicDBList configs = new BasicDBList();
        for (UPlugin.PluginConfig config : plugin.getConfigs().values()) {
            BasicDBObject object = new BasicDBObject();
            object.put("_id", config.get_id());
            object.put("name", config.getName());
            object.put("location", config.getLocation());
            configs.add(object);
        }
        values.put("configs", configs);

        BasicDBObject set = new BasicDBObject("$set", values);
        getDb().updateDocument(getCollection(), new BasicDBObject("_id", plugin.get_id()), set);
        log.info("Saving Plugin " + plugin.getName());
    }

    @Override
    public ObjectId insertEntity(UPlugin plugin) {
        BasicDBObject dbObject = new BasicDBObject("_id", new ObjectId());

        dbObject.put("name", plugin.getName());
        dbObject.put("type", plugin.getType().name());
        dbObject.put("baseFolder", plugin.getBaseFolder());
        dbObject.put("configFolder", plugin.getConfigFolder());

        BasicDBList configs = new BasicDBList();
        for (UPlugin.PluginConfig config : plugin.getConfigs().values()) {
            BasicDBObject object = new BasicDBObject();
            object.put("_id", config.get_id());
            object.put("name", config.getName());
            object.put("location", config.getLocation());
            configs.add(object);
        }
        dbObject.put("configs", configs);

        getDb().insert(getCollection(), dbObject);

        return (ObjectId)dbObject.get("_id");
    }

    @Override
    public void removeEntity(UPlugin entity) {
        getDb().delete(getCollection(), new BasicDBObject("_id", entity.get_id()));
    }
}
