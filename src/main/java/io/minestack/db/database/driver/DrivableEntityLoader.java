package io.minestack.db.database.driver;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import io.minestack.db.database.EntityLoader;
import io.minestack.db.entity.driver.DCDrivableEntity;
import io.minestack.db.entity.driver.DCDriver;
import io.minestack.db.mongo.MongoDatabase;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;

import java.util.HashMap;

@Log4j2
public abstract class DrivableEntityLoader<T extends DCDrivableEntity> extends EntityLoader<T> {

    @Getter
    private final HashMap<String, DriverLoader> drivers = new HashMap<>();

    public DrivableEntityLoader(MongoDatabase db, String collection) {
        super(db, collection);
    }

    protected abstract T loadDrivableEntity(DBObject dbObject);

    protected DCDriver loadDriver(DBObject driver) {
        String driverName = (String) driver.get("driverName");

        DriverLoader driverLoader = drivers.get(driverName);

        if (driverLoader == null) {
            log.error("Unknown Driver Loader "+driverName);
            return null;
        }

        return driverLoader.loadDriver(driver);
    }

    protected abstract void saveDrivableEntity(T entity, DBObject entityObject);

    protected void saveDriver(DCDriver driver, DBObject driverObject) {
        DriverLoader driverLoader = drivers.get(driver.getDriverName());

        if (driverLoader == null) {
            log.error("Unknown Driver Loader "+driver.getDriverName());
            return;
        }

        driverLoader.saveDriver(driver, driverObject);
    }

    protected abstract void insertDrivableEntity(T entity, DBObject entityObject);

    protected void insertDriver(DCDriver driver, DBObject driverObject) {
        DriverLoader driverLoader = drivers.get(driver.getDriverName());

        if (driverLoader == null) {
            log.error("Unknown Driver Loader "+driver.getDriverName());
            return;
        }

        driverLoader.insertDriver(driver, driverObject);
    }

    @Override
    public T loadEntity(ObjectId _id) {
        if (_id == null) {
            log.error("Error loading bungee. _id null");
            return null;
        }

        DBObject dbObject = getDb().findOne(getCollection(), new BasicDBObject("_id", _id));
        if (dbObject == null) {
            log.error("No drivable entity found "+_id.toString());
            return null;
        }

        if (dbObject.get("driver") == null) {
            log.error("This object is not a drivable entity "+_id.toString());
            return null;
        }

        T entity = loadDrivableEntity(dbObject);

        DBObject driver = (DBObject) dbObject.get("driver");
        entity.setDriver(loadDriver(driver));

        if (entity.getDriver() == null) {
            log.error("No driver loaded for "+_id.toString());
            return null;
        }

        return entity;
    }

    @Override
    public void saveEntity(T entity) {
        BasicDBObject values = new BasicDBObject();

        BasicDBObject entityObject = new BasicDBObject();
        saveDrivableEntity(entity, entityObject);
        if (!entityObject.values().isEmpty()) {
            values.putAll(entityObject.toMap());
        }

        if (entity.getDriver() != null) {
            BasicDBObject driver = new BasicDBObject();
            driver.put("driverName", entity.getDriver().getDriverName());
            saveDriver(entity.getDriver(), driver);
            if (!values.values().isEmpty()) {
                values.put("driver", driver);
            }
        } else {
            log.info("No driver to save");
            return;
        }

        if (values.toMap().isEmpty()) {
            return;
        }

        BasicDBObject set = new BasicDBObject("$set", values);
        getDb().updateDocument(getCollection(), new BasicDBObject("_id", entity.get_id()), set);
    }

    @Override
    public ObjectId insertEntity(T entity) {
        BasicDBObject dbObject = new BasicDBObject("_id", new ObjectId());

        BasicDBObject entityObject = new BasicDBObject();
        insertDrivableEntity(entity, entityObject);
        if (!entityObject.values().isEmpty()) {
            dbObject.putAll(entityObject.toMap());
        }

        if (entity.getDriver() != null) {
            BasicDBObject driver = new BasicDBObject();
            driver.put("driverName", entity.getDriver().getDriverName());
            insertDriver(entity.getDriver(), driver);
            dbObject.put("driver", driver);
        } else {
            log.info("No driver to save");
            return null;
        }

        getDb().insert(getCollection(), dbObject);
        return dbObject.getObjectId("_id");
    }

    @Override
    public void removeEntity(T entity) {
        getDb().remove(getCollection(), new BasicDBObject("_id", entity.get_id()));
    }
}
