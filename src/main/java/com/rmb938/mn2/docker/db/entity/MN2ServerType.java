package com.rmb938.mn2.docker.db.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Map;

@Log4j2
public class MN2ServerType extends MN2Entity {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private int players;

    @Getter
    @Setter
    private int memory;

    @Getter
    @Setter
    private int amount;

    @Getter
    @Setter
    private MN2World defaultWorld;

    @Getter
    private ArrayList<Map.Entry<MN2Plugin, MN2Plugin.PluginConfig>> plugins = new ArrayList<>();

    @Getter
    private ArrayList<MN2World> worlds = new ArrayList<>();

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MN2ServerType && get_id().equals(((MN2ServerType) obj).get_id());
    }

    @Override
    public int hashCode() {
        return get_id().hashCode();
    }
}
