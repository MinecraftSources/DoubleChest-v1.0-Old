package io.minestack.db.entity.proxy;

import io.minestack.db.entity.DCPlugin;
import io.minestack.db.entity.driver.DCDriver;
import lombok.Getter;

import java.util.HashMap;

public class DCProxyTypeDriver extends DCDriver {

    public DCProxyTypeDriver(String driverName) {
        super(driverName);
    }

    @Getter
    private HashMap<DCPlugin, DCPlugin.PluginConfig> plugins = new HashMap<>();
}
