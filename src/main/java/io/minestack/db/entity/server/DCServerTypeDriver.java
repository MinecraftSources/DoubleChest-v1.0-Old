package io.minestack.db.entity.server;

import io.minestack.db.entity.DCPlugin;
import io.minestack.db.entity.driver.DCDriver;
import lombok.Getter;

import java.util.HashMap;

public class DCServerTypeDriver extends DCDriver {

    public DCServerTypeDriver(String driverName) {
        super(driverName);
    }

    @Getter
    private HashMap<DCPlugin, DCPlugin.PluginConfig> plugins = new HashMap<>();
}
