package com.rmb938.mn2.docker.db.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class MN2Server extends MN2Entity {

    @Getter
    @Setter
    private MN2ServerType serverType;

    @Getter
    @Setter
    private MN2Node node;

    @Getter
    @Setter
    private long lastUpdate;

    @Getter
    @Setter
    private String containerId;

    @Getter
    @Setter
    private int number;

    @Getter
    @Setter
    private int port;

    @Getter
    private ArrayList<MN2Player> players = new ArrayList<>();

}
