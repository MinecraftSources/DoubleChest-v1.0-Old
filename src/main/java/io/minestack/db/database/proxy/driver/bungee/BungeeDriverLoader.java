package io.minestack.db.database.proxy.driver.bungee;

import com.mongodb.DBObject;
import io.minestack.db.database.driver.DriverLoader;
import io.minestack.db.entity.proxy.driver.bungee.DCBungee;
import io.minestack.db.mongo.MongoDatabase;

public class BungeeDriverLoader extends DriverLoader<DCBungee> {

    public BungeeDriverLoader(MongoDatabase db, String collection) {
        super(db, collection, "bungee");
    }

    @Override
    public DCBungee loadDriver(DBObject driver) {
        if (driver == null) {
            return null;
        }

        DCBungee bungee = new DCBungee();

        return bungee;
    }

    @Override
    public void saveDriver(DCBungee driver, DBObject driverObject) {
        if (driver == null) {
            return;
        }
        if (driverObject == null) {
            return;
        }
    }

    @Override
    public void insertDriver(DCBungee driver, DBObject driverObject) {
        if (driver == null) {
            return;
        }
        if (driverObject == null) {
            return;
        }
    }
}
