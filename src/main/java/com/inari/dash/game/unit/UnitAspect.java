package com.inari.dash.game.unit;

import com.inari.commons.lang.indexed.Indexer;

public enum UnitAspect implements IUnitAspect {
    ACTIVE,
    
    ENEMY,
    STONE,

    MASSIVE,
    CONSUMABLE,
    DESTRUCTIBLE,
    ASLOPE,
    
    HORIZONTAL_EXPANDING,
    VERTICAL_EXPANDING
    
    ;
    
    private final int index;
    private UnitAspect() {
        index = Indexer.nextObjectIndex( indexedObjectType() );
    }

    @Override
    public final int index() {
        return index;
    }

    @Override
    public final Class<IUnitAspect> indexedObjectType() {
        return IUnitAspect.class;
    }  
}
