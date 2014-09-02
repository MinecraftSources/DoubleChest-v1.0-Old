package io.minestack.db.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class UServer extends UEntity {

    @Getter
    @Setter
    private UServerType serverType;

    @Getter
    @Setter
    private UNode node;

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
    private ArrayList<UPlayer> players = new ArrayList<>();

}
