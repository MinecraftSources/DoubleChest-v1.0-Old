package io.minestack.db.database.proxy.driver.bungee;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import io.minestack.db.database.driver.DriverLoader;
import io.minestack.db.database.plugin.PluginLoader;
import io.minestack.db.entity.DCPlugin;
import io.minestack.db.entity.proxy.driver.bungee.DCBungeeType;
import io.minestack.db.mongo.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;

@Log4j2
public class BungeeTypeDriverLoader extends DriverLoader<DCBungeeType> {

    private final PluginLoader pluginLoader;

    public BungeeTypeDriverLoader(MongoDatabase db, String collection, PluginLoader pluginLoader) {
        super(db, collection, "bungeeType");
        this.pluginLoader = pluginLoader;
    }

    @Override
    public DCBungeeType loadDriver(DBObject driver) {
        DCBungeeType bungeeType = new DCBungeeType();

        BasicDBList plugins = (BasicDBList) driver.get("plugins");
        for (Object obj : plugins) {
            DBObject dbObj = (DBObject) obj;
            ObjectId _pluginId = (ObjectId) dbObj.get("_id");
            DCPlugin plugin = pluginLoader.loadEntity(_pluginId);
            if (plugin == null) {
                log.error("Error loading plugin for bungee type");
                return null;
            }

            if (plugin.getType() != DCPlugin.PluginType.BUNGEE) {
                log.error("Trying to add Non-Bungee plugin "+plugin.getName()+" to bungee");
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
            bungeeType.getPlugins().put(plugin, pluginConfig);
        }

        return bungeeType;
    }

    @Override
    public void saveDriver(DCBungeeType driver, DBObject driverObject) {
        BasicDBList plugins = new BasicDBList();
        for (DCPlugin plugin : driver.getPlugins().keySet()) {
            BasicDBObject object = new BasicDBObject();
            object.put("_id", plugin.get_id());
            DCPlugin.PluginConfig pluginConfig = driver.getPlugins().get(plugin);
            if (pluginConfig != null) {
                object.put("_configId", pluginConfig.get_id());
            }
            plugins.add(object);
        }
        driverObject.put("plugins", plugins);
    }

    @Override
    public void insertDriver(DCBungeeType driver, DBObject driverObject) {
        BasicDBList plugins = new BasicDBList();
        for (DCPlugin plugin : driver.getPlugins().keySet()) {
            BasicDBObject object = new BasicDBObject();
            object.put("_id", plugin.get_id());
            DCPlugin.PluginConfig pluginConfig = driver.getPlugins().get(plugin);
            if (pluginConfig != null) {
                object.put("_configId", pluginConfig.get_id());
            }
            plugins.add(object);
        }
        driverObject.put("plugins", plugins);
    }
}
