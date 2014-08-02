package com.rmb938.mn2.docker.db.entity;

import lombok.Getter;
import lombok.Setter;

public class World extends Entity {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String folder;

    @Getter
    @Setter
    private Environment environment;

    @Getter
    @Setter
    private String generator;

    public enum Environment {
        NORMAL,
        NETHER,
        THE_END
    }

}
