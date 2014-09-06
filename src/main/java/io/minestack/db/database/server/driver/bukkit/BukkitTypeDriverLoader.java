package io.minestack.db.database.server.driver.bukkit;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import io.minestack.db.database.driver.DriverLoader;
import io.minestack.db.database.plugin.PluginLoader;
import io.minestack.db.entity.DCPlugin;
import io.minestack.db.entity.server.driver.bukkit.DCBukkitType;
import io.minestack.db.mongo.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;

@Log4j2
public class BukkitTypeDriverLoader extends DriverLoader<DCBukkitType> {

    private final PluginLoader pluginLoader;

    public BukkitTypeDriverLoader(MongoDatabase db, String collection, PluginLoader pluginLoader) {
        super(db, collection, "bukkitType");
        this.pluginLoader = pluginLoader;
    }

    @Override
    public DCBukkitType loadDriver(DBObject driver) {
        DCBukkitType bukkitType = new DCBukkitType();

        BasicDBList plugins = (BasicDBList) driver.get("plugins");
        for (Object obj : plugins) {
            DBObject dbObj = (DBObject) obj;
            ObjectId _pluginId = (ObjectId) dbObj.get("_id");
            DCPlugin plugin = pluginLoader.loadEntity(_pluginId);
            if (plugin == null) {
                log.error("Error loading plugin for server");
                return null;
            }

            if (plugin.getType() != DCPlugin.PluginType.BUKKIT) {
                log.error("Trying to add Non-Bukkit plugin "+plugin.getName()+" to server");
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
            bukkitType.getPlugins().put(plugin, pluginConfig);
        }

        return bukkitType;
    }

    @Override
    public void saveDriver(DCBukkitType driver, DBObject driverObject) {
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
    public void insertDriver(DCBukkitType driver, DBObject driverObject) {
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
