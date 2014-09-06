package io.minestack.db.database.server.driver.bukkit;

import com.mongodb.DBObject;
import io.minestack.db.database.driver.DriverLoader;
import io.minestack.db.entity.server.driver.bukkit.DCBukkit;
import io.minestack.db.mongo.MongoDatabase;

public class BukkitDriverLoader extends DriverLoader<DCBukkit> {

    public BukkitDriverLoader(MongoDatabase db, String collection) {
        super(db, collection, "bukkit");
    }

    @Override
    public DCBukkit loadDriver(DBObject driver) {
        if (driver == null) {
            return null;
        }

        DCBukkit bukkit = new DCBukkit();

        return bukkit;
    }

    @Override
    public void saveDriver(DCBukkit driver, DBObject driverObject) {
        if (driverObject == null) {
            return;
        }
        if (driverObject == null) {
            return;
        }
    }

    @Override
    public void insertDriver(DCBukkit driver, DBObject driverObject) {
        if (driverObject == null) {
            return;
        }
        if (driverObject == null) {
            return;
        }
    }
}
