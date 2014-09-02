package io.minestack.db.entity;


import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

public class UBungeeType extends UEntity {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private UServerType defaultType;

    @Getter
    private HashMap<UPlugin, UPlugin.PluginConfig> plugins = new HashMap<>();

    @Getter
    private HashMap<UServerType, Boolean> serverTypes = new HashMap<>();

}
