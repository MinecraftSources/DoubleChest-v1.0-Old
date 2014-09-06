package io.minestack.db.entity.server;

import io.minestack.db.entity.DCNode;
import io.minestack.db.entity.DCPlayer;
import io.minestack.db.entity.driver.DCDrivableEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class DCServer extends DCDrivableEntity {

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
