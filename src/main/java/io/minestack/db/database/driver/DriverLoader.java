package io.minestack.db.database.driver;

import com.mongodb.DBObject;
import io.minestack.db.entity.driver.DCDriver;
import io.minestack.db.mongo.MongoDatabase;
import lombok.Getter;

public abstract class DriverLoader<T extends DCDriver> {

    @Getter
    private final MongoDatabase db;

    @Getter
    private final String collection;

    @Getter
    private final String driverName;

    public DriverLoader(MongoDatabase db, String collection, String driverName) {
        this.db = db;
        this.collection = collection;
        this.driverName = driverName;
    }

    public abstract T loadDriver(DBObject driver);

    public abstract void saveDriver(T driver, DBObject driverObject);

    public abstract void insertDriver(T driver, DBObject driverObject);

}
