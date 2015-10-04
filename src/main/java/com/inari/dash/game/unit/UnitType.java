package com.inari.dash.game.unit;

import com.inari.commons.lang.indexed.Indexer;

public enum UnitType implements IUnitType {
    
    ENTRANCE,           // 0
    EXIT,               // 1
    ROCKFORD,           // 2  
    
    SPACE,              // 3
    SAND,               // 4
    
    BRICK_WALL,         // 5
    SOLID_WALL,         // 6
    MAGIC_WALL,         // 7
    
    ROCK,               // 8
    DIAMOND,            // 9
    
    FIREFLY,            // 10
    BUTTERFLY,          // 11
    AMOEBA,             // 12
    
    EXPLOSION1,         // 13
    EXPLOSION2,         // 14
    
    EXPANDING_WALL,     // 15
    
    ;
    
    private final int index;
    private UnitType() {
        index = Indexer.nextObjectIndex( indexedObjectType() );
    }

    @Override
    public final int index() {
        return index;
    }
    
    @Override
    public final int type() {
        return index;
    }

    @Override
    public final Class<IUnitType> indexedObjectType() {
        return IUnitType.class;
    }
       
}
