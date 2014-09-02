package io.minestack.db.database;

import io.minestack.db.entity.DCEntity;
import io.minestack.db.mongo.MongoDatabase;
import lombok.Getter;
import org.bson.types.ObjectId;

public abstract class EntityLoader<T extends DCEntity> {

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
