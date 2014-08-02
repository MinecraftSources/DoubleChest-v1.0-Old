package com.rmb938.mn2.docker.db.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class Server extends Entity {

    @Getter
    @Setter
    private ServerType serverType;

    @Getter
    @Setter
    private Node node;

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
    private ArrayList<Player> players = new ArrayList<>();

}
