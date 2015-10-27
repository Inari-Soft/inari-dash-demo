package com.inari.dash.game.cave.unit;

import com.inari.commons.lang.aspect.Aspect;

public enum UnitAspect implements Aspect {
    ACTIVE,
    ALIVE,
    
    ENEMY,
    STONE,
    WALKABLE,
    CONSUMABLE,
    DESTRUCTIBLE,
    ASLOPE,
    
    HORIZONTAL_EXPANDING,
    VERTICAL_EXPANDING
    
    ;

    @Override
    public final int aspectId() {
        return ordinal();
    }

}
