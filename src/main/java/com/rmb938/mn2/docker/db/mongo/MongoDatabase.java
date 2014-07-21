package com.rmb938.mn2.docker.db.mongo;

import com.mongodb.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class MongoDatabase {

    private static final Logger logger = LogManager.getLogger(MongoDatabase.class.getName());

    private DB db;

    public MongoDatabase(List<ServerAddress> addressList, String database) throws MongoException {
        MongoClient mongoClient = new MongoClient(addressList);
        mongoClient.setWriteConcern(WriteConcern.REPLICA_ACKNOWLEDGED);
        mongoClient.setReadPreference(ReadPreference.primaryPreferred());
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

}
