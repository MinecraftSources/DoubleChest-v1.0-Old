package io.minestack.db.entity;


import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

public class DCBungeeType extends DCEntity {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private DCServerType defaultType;

    @Getter
    private HashMap<DCPlugin, DCPlugin.PluginConfig> plugins = new HashMap<>();

    @Getter
    private HashMap<DCServerType, Boolean> serverTypes = new HashMap<>();

}
