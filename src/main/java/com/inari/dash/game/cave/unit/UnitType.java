package com.inari.dash.game.cave.unit;

import com.inari.commons.lang.indexed.IndexedObject;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.dash.game.cave.unit.misc.SandHandle;
import com.inari.dash.game.cave.unit.misc.SpaceHandle;
import com.inari.dash.game.cave.unit.stone.RockHandle;
import com.inari.dash.game.cave.unit.wall.SolidWallHandle;

public enum UnitType implements IndexedObject {
    
    ENTRANCE( null ),           // 0
    EXIT( null ),               // 1
    ROCKFORD( null ),           // 2  
    
    SPACE( new SpaceHandle() ),              // 3
    SAND( new SandHandle() ),               // 4
    
    BRICK_WALL( null ),         // 5
    SOLID_WALL( new SolidWallHandle() ),         // 6
    MAGIC_WALL( null ),         // 7
    
    ROCK( new RockHandle() ),               // 8
    DIAMOND( null ),            // 9
    
    FIREFLY( null ),            // 10
    BUTTERFLY( null ),          // 11
    AMOEBA( null ),             // 12
    
    EXPLOSION1( null ),         // 13
    EXPLOSION2( null ),         // 14
    
    EXPANDING_WALL( null ),     // 15
    
    ;
    
    public final UnitHandle handler;
    private UnitType( UnitHandle handler ) {
        Indexer.registerIndex( indexedObjectType(), ordinal() );
        this.handler = handler;
    }

    @Override
    public final int index() {
        return ordinal();
    }

    @Override
    public final Class<UnitType> indexedObjectType() {
        return UnitType.class;
    }
       
}
