package io.minestack.db.entity.driver;

import io.minestack.db.entity.DCEntity;
import lombok.Getter;
import lombok.Setter;

public class DCDrivableEntity extends DCEntity {

    @Getter
    @Setter
    private DCDriver driver;

}
