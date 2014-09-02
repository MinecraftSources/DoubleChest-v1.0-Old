package io.minestack.db.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashMap;

@Log4j2
public class DCServerType extends DCEntity {

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
    private DCWorld defaultWorld;

    @Getter
    @Setter
    private boolean disabled;

    @Getter
    private HashMap<DCPlugin, DCPlugin.PluginConfig> plugins = new HashMap<>();

    @Getter
    private ArrayList<DCWorld> worlds = new ArrayList<>();

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DCServerType && get_id().equals(((DCServerType) obj).get_id());
    }

    @Override
    public int hashCode() {
        return get_id().hashCode();
    }
}
