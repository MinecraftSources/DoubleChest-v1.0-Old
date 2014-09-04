package io.minestack.db.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class DCServer extends DCEntity {

    @Getter
    @Setter
    private DCServerType serverType;

    @Getter
    @Setter
    private DCNode node;

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
    @Setter
    private String containerAddress;

    @Getter
    private ArrayList<DCPlayer> players = new ArrayList<>();

}
