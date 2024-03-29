package io.minestack.db.entity;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.HashMap;

public class DCPlugin extends DCEntity {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private PluginType type;

    @Getter
    @Setter
    private String baseFolder;

    @Getter
    @Setter
    private String configFolder;

    @Getter
    private HashMap<ObjectId, PluginConfig> configs = new HashMap<>();

    public PluginConfig newPluginConfig() {
        return new PluginConfig();
    }

    public enum PluginType {

        BUKKIT,
        BUNGEE

    }

    public static class PluginConfig extends DCEntity {

        @Getter
        @Setter
        private String name;

        @Getter
        @Setter
        private String location;
    }

}
