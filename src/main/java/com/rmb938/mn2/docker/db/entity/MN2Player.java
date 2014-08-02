package com.rmb938.mn2.docker.db.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class MN2Player extends MN2Entity {

    @Getter
    @Setter
    private MN2Server currentServer;

    @Getter
    @Setter
    private String playerName;

    @Getter
    @Setter
    private UUID uuid;

}
