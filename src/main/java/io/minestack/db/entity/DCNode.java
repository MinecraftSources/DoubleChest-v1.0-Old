package io.minestack.db.entity;

import lombok.Getter;
import lombok.Setter;

public class DCNode extends DCEntity {

    @Getter
    @Setter
    private String address;

    @Getter
    @Setter
    private long lastUpdate = 0L;

    @Getter
    @Setter
    private int ram;

    @Getter
    @Setter
    private DCBungeeType bungeeType;
}
