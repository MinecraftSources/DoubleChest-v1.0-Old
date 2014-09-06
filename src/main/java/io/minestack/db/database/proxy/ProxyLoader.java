package io.minestack.db.database.proxy;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import io.minestack.db.database.driver.DrivableEntityLoader;
import io.minestack.db.database.driver.DriverLoader;
import io.minestack.db.database.node.NodeLoader;
import io.minestack.db.database.proxy.driver.bungee.BungeeDriverLoader;
import io.minestack.db.entity.DCNode;
import io.minestack.db.entity.proxy.DCProxy;
import io.minestack.db.mongo.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;

import java.util.ArrayList;

@Log4j2
public class ProxyLoader extends DrivableEntityLoader<DCProxy> {

    private final ProxyTypeLoader proxyTypeLoader;
    private final NodeLoader nodeLoader;

    public ProxyLoader(MongoDatabase db, ProxyTypeLoader proxyTypeLoader, NodeLoader nodeLoader) {
        super(db, "proxies");
        this.proxyTypeLoader = proxyTypeLoader;
        this.nodeLoader = nodeLoader;

        DriverLoader driverLoader = new BungeeDriverLoader(db, getCollection());
        getDrivers().put(driverLoader.getDriverName(), driverLoader);
    }

    public DCProxy getNodeProxies(DCNode node) {
        DBObject dbObject = getDb().findOne(getCollection(), new BasicDBObject("_node", node.get_id()));
        if (dbObject != null) {
            return loadEntity((ObjectId) dbObject.get("_id"));
        }
        return null;
    }

    public ArrayList<DCProxy> getProcies() {
        ArrayList<DCProxy> proxies = new ArrayList<>();

        DBCursor dbCursor = getDb().findMany(getCollection());
        while (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            DCProxy proxy = loadEntity((ObjectId) dbObject.get("_id"));
            if (proxy != null) {
                proxies.add(proxy);
            }
        }
        dbCursor.close();
        return proxies;
    }

    @Override
    protected DCProxy loadDrivableEntity(DBObject dbObject) {
        DCProxy proxy = new DCProxy();
        proxy.set_id((ObjectId) dbObject.get("_id"));
        proxy.setProxyType(proxyTypeLoader.loadEntity((ObjectId) dbObject.get("_proxytype")));
        proxy.setNode(nodeLoader.loadEntity((ObjectId) dbObject.get("_node")));
        proxy.setLastUpdate((Long) dbObject.get("lastUpdate"));
        proxy.setContainerId((String) dbObject.get("containerId"));

        return proxy;
    }

    @Override
    protected void saveDrivableEntity(DCProxy entity, DBObject entityObject) {
        entityObject.put("lastUpdate", entity.getLastUpdate());
        entityObject.put("containerId", entity.getContainerId());
    }

    @Override
    protected void insertDrivableEntity(DCProxy entity, DBObject entityObject) {
        entityObject.put("_proxytype", entity.getProxyType().get_id());
        entityObject.put("_node", entity.getNode().get_id());
        entityObject.put("lastUpdate", entity.getLastUpdate());
        entityObject.put("containerId", "NULL");
    }
}
