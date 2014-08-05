package com.rmb938.mn2.docker.db.entity;

import lombok.Getter;
import lombok.Setter;

public class MN2Bungee extends MN2Entity {

    @Getter
    @Setter
    private MN2BungeeType bungeeType;

    @Getter
    @Setter
    private MN2Node node;

    @Getter
    @Setter
    private long lastUpdate;

    @Getter
    @Setter
    private String containerId;

}
