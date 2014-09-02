package io.minestack.db.entity;


import io.minestack.db.entity.MN2Entity;
import io.minestack.db.entity.MN2Plugin;
import io.minestack.db.entity.MN2ServerType;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

public class MN2BungeeType extends MN2Entity {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private MN2ServerType defaultType;

    @Getter
    private HashMap<MN2Plugin, MN2Plugin.PluginConfig> plugins = new HashMap<>();

    @Getter
    private HashMap<MN2ServerType, Boolean> serverTypes = new HashMap<>();

}
