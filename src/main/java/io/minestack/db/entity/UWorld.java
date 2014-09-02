package io.minestack.db.entity;

import lombok.Getter;
import lombok.Setter;

public class UWorld extends UEntity {

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
