package io.minestack.db.mongo;

import com.mongodb.*;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class MongoDatabase {

    private DB db;

    public MongoDatabase(List<ServerAddress> addressList, String database) throws MongoException {
        MongoClientOptions clientOptions = MongoClientOptions.builder().connectTimeout(30000)
                .heartbeatConnectRetryFrequency(15)
                .heartbeatConnectTimeout(15)
                .heartbeatFrequency(15)
                .heartbeatThreadCount(1)
                .build();
        MongoClient mongoClient = new MongoClient(addressList, clientOptions);
        if (addressList.size() > 1) {
            mongoClient.setWriteConcern(WriteConcern.REPLICA_ACKNOWLEDGED);
            mongoClient.setReadPreference(ReadPreference.primaryPreferred());
        }
        db = mongoClient.getDB(database);
    }

    private DB getDatabase() {
        db.requestEnsureConnection();
        return db;
    }

    private DBCollection getCollection(String collectionName) {
        DB database = getDatabase();
        return database.getCollection(collectionName);
    }

    public boolean collectionExists(String collectionName) {
        DB database = getDatabase();
        return database.collectionExists(collectionName);
    }

    public void createCollection(String collectionName) {
        DB database = getDatabase();
        database.createCollection(collectionName, new BasicDBObject("capped", false));
    }

    public DBObject findOne(String collection, DBObject query) {
        DBObject dbObject;
        DBCollection dbCollection = getCollection(collection);
        DBCursor dbCursor = dbCollection.find(query).limit(1);
        if (dbCursor.hasNext() == false) {
            return null;
        }
        dbObject = dbCursor.next();
        dbCursor.close();

        return dbObject;
    }

    public void remove(String collection, DBObject query) {
        DBCollection dbCollection = getCollection(collection);
        dbCollection.remove(query);
    }

    public long count(String collection) {
        DBCollection dbCollection = getCollection(collection);
        return dbCollection.count();
    }

    public long count(String collection, DBObject query) {
        DBCollection dbCollection = getCollection(collection);
        return dbCollection.count(query);
    }

    public DBCursor findMany(String collection) {
        DBCollection dbCollection = getCollection(collection);
        return dbCollection.find();
    }

    public DBCursor findMany(String collection, DBObject query) {
        DBCollection dbCollection = getCollection(collection);
        return dbCollection.find(query);
    }

    public void insert(String collection, DBObject object) {
        DBCollection dbCollection = getCollection(collection);
        dbCollection.insert(object);
    }

    public void delete(String collection, DBObject query) {
        DBCollection dbCollection = getCollection(collection);
        dbCollection.remove(query);
    }

    public void updateDocument(String collection, DBObject query, DBObject document) {
        DBCollection dbCollection = getCollection(collection);
        dbCollection.update(query, document);
    }

    public void createIndex(String collection, DBObject keys) {
        DBCollection dbCollection = getCollection(collection);
        dbCollection.createIndex(keys);
    }

    public void createIndex(String collection, DBObject keys, DBObject options) {
        DBCollection dbCollection = getCollection(collection);
        dbCollection.createIndex(keys, options);
    }

}
