package io.minestack.db.entity;

import lombok.Getter;
import lombok.Setter;

public class DCBungee extends DCEntity {

    @Getter
    @Setter
    private DCBungeeType bungeeType;

    @Getter
    @Setter
    private DCNode node;

    @Getter
    @Setter
    private long lastUpdate;

    @Getter
    @Setter
    private String containerId;

}
