package io.minestack.db.entity.server;

import io.minestack.db.entity.DCWorld;
import io.minestack.db.entity.driver.DCDrivableEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class DCServerType extends DCDrivableEntity {

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
    private ArrayList<DCWorld> worlds = new ArrayList<>();

}
