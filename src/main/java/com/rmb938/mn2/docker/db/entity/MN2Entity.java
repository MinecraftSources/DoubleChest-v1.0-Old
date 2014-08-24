package com.rmb938.mn2.docker.db.entity;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.json.JSONObject;

public abstract class MN2Entity {

    @Getter
    @Setter
    private ObjectId _id;

    @Setter
    private DBObject dbObject;

    public JSONObject toJSON() {
        return new JSONObject(JSON.serialize(dbObject));
    }

}
