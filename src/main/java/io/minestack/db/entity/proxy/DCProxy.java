package io.minestack.db.entity.proxy;

import io.minestack.db.entity.DCNode;
import io.minestack.db.entity.driver.DCDrivableEntity;
import lombok.Getter;
import lombok.Setter;

public class DCProxy extends DCDrivableEntity {

    @Getter
    @Setter
    private DCProxyType proxyType;

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
