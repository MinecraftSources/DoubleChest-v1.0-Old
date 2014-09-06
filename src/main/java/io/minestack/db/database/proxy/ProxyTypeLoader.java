package io.minestack.db.database.proxy;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import io.minestack.db.database.driver.DrivableEntityLoader;
import io.minestack.db.database.driver.DriverLoader;
import io.minestack.db.database.plugin.PluginLoader;
import io.minestack.db.database.proxy.driver.bungee.BungeeTypeDriverLoader;
import io.minestack.db.database.server.ServerTypeLoader;
import io.minestack.db.entity.proxy.DCProxyType;
import io.minestack.db.entity.server.DCServerType;
import io.minestack.db.mongo.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;

import java.util.ArrayList;

@Log4j2
public class ProxyTypeLoader extends DrivableEntityLoader<DCProxyType> {

    private final ServerTypeLoader serverTypeLoader;

    public ProxyTypeLoader(MongoDatabase db, ServerTypeLoader serverTypeLoader, PluginLoader pluginLoader) {
        super(db, "proxytypes");
        this.serverTypeLoader = serverTypeLoader;

        DriverLoader driverLoader = new BungeeTypeDriverLoader(db, getCollection(), pluginLoader);
        getDrivers().put(driverLoader.getDriverName(), driverLoader);
    }

    public ArrayList<DCProxyType> getTypes() {
        ArrayList<DCProxyType> types = new ArrayList<>();
        DBCursor dbCursor = getDb().findMany(getCollection());
        while (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            DCProxyType type = loadEntity((ObjectId)dbObject.get("_id"));
            if (type != null) {
                types.add(type);
            }
        }
        dbCursor.close();
        return types;
    }

    @Override
    protected DCProxyType loadDrivableEntity(DBObject dbObject) {
        DCProxyType proxyType = new DCProxyType();
        proxyType.set_id((ObjectId) dbObject.get("_id"));
        proxyType.setName((String) dbObject.get("name"));

        BasicDBList servertypes = (BasicDBList) dbObject.get("servertypes");
        for (Object obj : servertypes) {
            DBObject dbObj = (DBObject) obj;
            ObjectId _servertype = (ObjectId) dbObj.get("_id");
            DCServerType serverType = serverTypeLoader.loadEntity(_servertype);
            if (serverType == null) {
                log.error("Error loading server type for bungee "+proxyType.getName());
                return null;
            }
            proxyType.getServerTypes().put(serverType, (Boolean) dbObj.get("allowRejoin"));

            boolean defaultType = (Boolean) dbObj.get("isDefault");
            if (defaultType) {
                proxyType.setDefaultType(serverType);
            }
        }

        return proxyType;
    }

    @Override
    protected void saveDrivableEntity(DCProxyType entity, DBObject entityObject) {
        entityObject.put("name", entity.getName());

        BasicDBList serverTypes = new BasicDBList();
        for (DCServerType serverType : entity.getServerTypes().keySet()) {
            boolean allowRejoin = entity.getServerTypes().get(serverType);
            BasicDBObject object = new BasicDBObject();
            object.put("_id", serverType.get_id());
            object.put("allowRejoin", allowRejoin);
            if (serverType == entity.getDefaultType()) {
                object.put("isDefault", true);
            } else {
                object.put("isDefault", false);
            }
            serverTypes.add(object);
        }
        entityObject.put("servertypes", serverTypes);
    }

    @Override
    protected void insertDrivableEntity(DCProxyType entity, DBObject entityObject) {
        entityObject.put("name", entity.getName());

        BasicDBList serverTypes = new BasicDBList();
        for (DCServerType serverType : entity.getServerTypes().keySet()) {
            boolean allowRejoin = entity.getServerTypes().get(serverType);
            BasicDBObject object = new BasicDBObject();
            object.put("_id", serverType.get_id());
            object.put("allowRejoin", allowRejoin);
            if (serverType == entity.getDefaultType()) {
                object.put("isDefault", true);
            } else {
                object.put("isDefault", false);
            }
            serverTypes.add(object);
        }
        entityObject.put("servertypes", serverTypes);
    }
}
