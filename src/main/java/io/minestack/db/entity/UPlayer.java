package io.minestack.db.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.UUID;

public class UPlayer extends UEntity {

    @Getter
    private HashMap<UBungeeType, UServerType> lastServerTypes = new HashMap<>();

    @Getter
    @Setter
    private String playerName;

    @Getter
    @Setter
    private UUID uuid;

}
