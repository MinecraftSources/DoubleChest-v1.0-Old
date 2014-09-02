package io.minestack.db.entity;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

public abstract class UEntity {

    @Getter
    @Setter
    private ObjectId _id;

}
