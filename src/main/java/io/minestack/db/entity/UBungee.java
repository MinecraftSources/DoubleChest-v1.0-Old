package io.minestack.db.entity;

import lombok.Getter;
import lombok.Setter;

public class UBungee extends UEntity {

    @Getter
    @Setter
    private UBungeeType bungeeType;

    @Getter
    @Setter
    private UNode node;

    @Getter
    @Setter
    private long lastUpdate;

    @Getter
    @Setter
    private String containerId;

}
