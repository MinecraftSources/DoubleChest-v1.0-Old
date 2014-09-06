package io.minestack.db.entity.proxy;

import io.minestack.db.entity.driver.DCDrivableEntity;
import io.minestack.db.entity.server.DCServerType;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

public class DCProxyType extends DCDrivableEntity {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private DCServerType defaultType;

    @Getter
    private HashMap<DCServerType, Boolean> serverTypes = new HashMap<>();

}
