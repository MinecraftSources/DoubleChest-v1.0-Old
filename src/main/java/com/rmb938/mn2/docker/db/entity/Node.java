package com.rmb938.mn2.docker.db.entity;

import lombok.Getter;
import lombok.Setter;

public class Node extends Entity {

    @Getter
    @Setter
    private String address;

    @Getter
    @Setter
    private long lastUpdate = 0L;


    @Getter
    @Setter
    private int ram;
}
