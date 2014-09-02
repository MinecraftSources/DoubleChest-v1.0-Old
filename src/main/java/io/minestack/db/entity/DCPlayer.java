package io.minestack.db.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.UUID;

public class DCPlayer extends DCEntity {

    @Getter
    private HashMap<DCBungeeType, DCServerType> lastServerTypes = new HashMap<>();

    @Getter
    @Setter
    private String playerName;

    @Getter
    @Setter
    private UUID uuid;

}
