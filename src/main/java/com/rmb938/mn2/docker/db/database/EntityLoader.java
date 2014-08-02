package com.rmb938.mn2.docker.db.database;

import com.rmb938.mn2.docker.db.entity.MN2Entity;
import com.rmb938.mn2.docker.db.mongo.MongoDatabase;
import lombok.Getter;
import org.bson.types.ObjectId;

public abstract class EntityLoader<T extends MN2Entity> {

    @Getter
    private final MongoDatabase db;

    @Getter
    private final String collection;

    protected EntityLoader(MongoDatabase db, String collection) {
        this.db = db;
        this.collection = collection;
        //db.createCollection(collection);
    }

    public abstract T loadEntity(ObjectId _id);

    public abstract void saveEntity(T entity);

    public abstract ObjectId insertEntity(T entity);

    public abstract void removeEntity(T entity);

}
