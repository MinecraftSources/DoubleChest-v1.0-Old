package io.minestack.db.entity.driver;

import io.minestack.db.entity.proxy.driver.bungee.DCBungee;
import io.minestack.db.entity.proxy.driver.bungee.DCBungeeType;
import io.minestack.db.entity.server.driver.bukkit.DCBukkit;
import io.minestack.db.entity.server.driver.bukkit.DCBukkitType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;

@RequiredArgsConstructor
public class DCDriver {

    @Getter
    private static HashMap<String, Class<? extends DCDriver>> drivers = new HashMap<>();

    static {
        drivers.put("bukkitType", DCBukkitType.class);
        drivers.put("bukkit", DCBukkit.class);
        drivers.put("bungeeType", DCBungeeType.class);
        drivers.put("bungee", DCBungee.class);
    }

    @Getter
    private final String driverName;
}
