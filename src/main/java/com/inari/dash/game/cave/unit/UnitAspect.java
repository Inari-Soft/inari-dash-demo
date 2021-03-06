package com.inari.dash.game.cave.unit;

import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.aspect.AspectGroup;
import com.inari.firefly.entity.EEntity;

public enum UnitAspect implements Aspect {
    NONE,
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
    
    private final Aspect aspect;
    
    private UnitAspect() {
        aspect = EEntity.ENTITY_ASPECT_GROUP.createAspect( name() ); 
    }

    @Override
    public AspectGroup aspectGroup() {
        return aspect.aspectGroup();
    }

    @Override
    public int index() {
        return aspect.index();
    }

}
