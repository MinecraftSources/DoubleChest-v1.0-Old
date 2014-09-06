package io.minestack.db.database.node;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import io.minestack.db.database.EntityLoader;
import io.minestack.db.database.proxy.ProxyTypeLoader;
import io.minestack.db.entity.DCNode;
import io.minestack.db.entity.proxy.DCProxyType;
import io.minestack.db.mongo.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;

import java.util.ArrayList;

@Log4j2
public class NodeLoader extends EntityLoader<DCNode> {

    private final ProxyTypeLoader proxyTypeLoader;

    public NodeLoader(MongoDatabase db, ProxyTypeLoader proxyTypeLoader) {
        super(db, "nodes");
        this.proxyTypeLoader = proxyTypeLoader;
    }

    public DCNode getMaster() {
        log.info("Finding Master");
        long start = System.currentTimeMillis();
        DBCursor dbCursor = getDb().findMany(getCollection(), new BasicDBObject("lastUpdate", new BasicDBObject("$gt", System.currentTimeMillis()-30000)));
        dbCursor = dbCursor.sort(new BasicDBObject("_id", 1));
        if (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            DCNode node = loadEntity((ObjectId)dbObject.get("_id"));
            if (node != null) {
                dbCursor.close();
                long end = System.currentTimeMillis();
                log.info("Found Master "+node.getAddress()+" "+(end-start));
                return node;
            }
        }
        dbCursor.close();
        long end = System.currentTimeMillis();
        log.error("Could not find master "+(end-start));
        return null;
    }

    public ArrayList<DCNode> getNodes() {
        ArrayList<DCNode> nodes = new ArrayList<>();
        DBCursor dbCursor = getDb().findMany(getCollection());
        while (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            DCNode node = loadEntity((ObjectId)dbObject.get("_id"));
            if (node != null) {
                nodes.add(node);
            }
        }
        dbCursor.close();
        return nodes;
    }

    public ArrayList<DCNode> getOnlineNodes() {
        ArrayList<DCNode> nodes = new ArrayList<>();
        DBCursor dbCursor = getDb().findMany(getCollection(), new BasicDBObject("lastUpdate", new BasicDBObject("$gt", System.currentTimeMillis()-30000)));
        while (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            DCNode node = loadEntity((ObjectId)dbObject.get("_id"));
            if (node != null) {
                nodes.add(node);
            }
        }
        dbCursor.close();
        return nodes;
    }

    @Override
    public DCNode loadEntity(ObjectId _id) {
        if (_id == null) {
            log.error("Error loading node. _id null");
            return null;
        }
        DBObject dbObject = getDb().findOne(getCollection(), new BasicDBObject("_id", _id));
        if (dbObject != null) {
            DCNode node = new DCNode();
            node.set_id(_id);
            node.setAddress((String) dbObject.get("host"));
            node.setRam((Integer) dbObject.get("ram"));
            Object lastUpdate = dbObject.get("lastUpdate");
            if (lastUpdate instanceof Integer) {
                node.setLastUpdate(((Integer)lastUpdate).longValue());
            } else {
                node.setLastUpdate((Long) lastUpdate);
            }

            ObjectId _proxyTypeId = (ObjectId) dbObject.get("_proxyType");
            if (_proxyTypeId != null) {
                DCProxyType proxyType = proxyTypeLoader.loadEntity(_proxyTypeId);
                node.setProxyType(proxyType);
            }

            return node;
        }
        log.info("Unknown Node "+_id.toString());
        return null;
    }

    @Override
    public void saveEntity(DCNode node) {
        BasicDBObject values = new BasicDBObject();

        values.put("ram", node.getRam());
        if (node.getProxyType() != null) {
            values.put("_proxyType", node.getProxyType().get_id());
        } else {
            values.put("_proxyType", null);
        }

        BasicDBObject set = new BasicDBObject("$set", values);
        getDb().updateDocument(getCollection(), new BasicDBObject("_id", node.get_id()), set);
        log.info("Saving Node " + node.getAddress());
    }

    @Override
    public ObjectId insertEntity(DCNode node) {
        BasicDBObject dbObject = new BasicDBObject("_id", new ObjectId());

        dbObject.put("host", node.getAddress());
        dbObject.put("ram", node.getRam());
        dbObject.put("lastUpdate", node.getLastUpdate());
        if (node.getProxyType() != null) {
            dbObject.put("_proxyType", node.getProxyType().get_id());
        } else {
            dbObject.put("_proxyType", null);
        }
        getDb().insert(getCollection(), dbObject);
        return (ObjectId)dbObject.get("_id");
    }

    @Override
    public void removeEntity(DCNode entity) {
        getDb().delete(getCollection(), new BasicDBObject("_id", entity.get_id()));
    }
}
