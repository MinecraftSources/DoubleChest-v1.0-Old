package io.minestack.db.entity;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

public abstract class DCEntity {

    @Getter
    @Setter
    private ObjectId _id;

}
