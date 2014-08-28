package com.rmb938.mn2.docker.db.entity;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private ArrayList<MN2ServerType> serverTypes = new ArrayList<>();

}
