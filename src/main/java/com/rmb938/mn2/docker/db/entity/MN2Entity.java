package com.rmb938.mn2.docker.db.entity;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

public abstract class MN2Entity {

    @Getter
    @Setter
    private ObjectId _id;

}