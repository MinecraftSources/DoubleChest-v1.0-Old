package io.minestack.db.entity;

import io.minestack.db.entity.proxy.DCProxyType;
import io.minestack.db.entity.server.DCServerType;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.UUID;

public class DCPlayer extends DCEntity {

    @Getter
    private HashMap<DCProxyType, DCServerType> lastServerTypes = new HashMap<>();

    @Getter
    @Setter
    private String playerName;

    @Getter
    @Setter
    private UUID uuid;

}
