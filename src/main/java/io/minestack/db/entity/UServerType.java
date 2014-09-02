package io.minestack.db.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashMap;

@Log4j2
public class UServerType extends UEntity {

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
    private UWorld defaultWorld;

    @Getter
    @Setter
    private boolean disabled;

    @Getter
    private HashMap<UPlugin, UPlugin.PluginConfig> plugins = new HashMap<>();

    @Getter
    private ArrayList<UWorld> worlds = new ArrayList<>();

    @Override
    public boolean equals(Object obj) {
        return obj instanceof UServerType && get_id().equals(((UServerType) obj).get_id());
    }

    @Override
    public int hashCode() {
        return get_id().hashCode();
    }
}
