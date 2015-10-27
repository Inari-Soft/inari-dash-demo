package com.inari.dash.game.cave.unit;

import com.inari.dash.game.cave.unit.enemy.ButterflyHandle;
import com.inari.dash.game.cave.unit.enemy.FireflyHandle;
import com.inari.dash.game.cave.unit.misc.AmoebaHandle;
import com.inari.dash.game.cave.unit.misc.ExitHandle;
import com.inari.dash.game.cave.unit.misc.ExplosionHandle;
import com.inari.dash.game.cave.unit.misc.ExplosionToDiamondHandle;
import com.inari.dash.game.cave.unit.misc.SandHandle;
import com.inari.dash.game.cave.unit.misc.SpaceHandle;
import com.inari.dash.game.cave.unit.rockford.RFHandle;
import com.inari.dash.game.cave.unit.stone.DiamondHandle;
import com.inari.dash.game.cave.unit.stone.RockHandle;
import com.inari.dash.game.cave.unit.wall.BrickWallHandle;
import com.inari.dash.game.cave.unit.wall.SolidWallHandle;

public enum UnitType {

    SPACE( new SpaceHandle() ),
    SAND( new SandHandle() ),
    
    BRICK_WALL( new BrickWallHandle() ),
    SOLID_WALL( new SolidWallHandle() ),
    MAGIC_WALL( null ),
    EXPANDING_WALL( null ),
    
    ROCK( new RockHandle() ),
    DIAMOND( new DiamondHandle() ),
    
    FIREFLY( new FireflyHandle() ),
    BUTTERFLY( new ButterflyHandle() ),
    AMOEBA( new AmoebaHandle() ),
    
    ROCKFORD( new RFHandle() ),
    EXIT( new ExitHandle() ),
    
    
    EXPLOSION_TO_DIAMOND( new ExplosionToDiamondHandle() ),
    EXPLOSION( new ExplosionHandle() ),
    ;
    
    public final UnitHandle handler;
    private UnitType( UnitHandle handler ) {
        this.handler = handler;
    }

    @SuppressWarnings( "unchecked" )
    public <T extends UnitHandle> T getHandle() {
        return (T) handler;
    }
}
