package com.inari.dash.game.cave.unit;

import com.inari.dash.game.cave.unit.enemy.Butterfly;
import com.inari.dash.game.cave.unit.enemy.Firefly;
import com.inari.dash.game.cave.unit.misc.Amoeba;
import com.inari.dash.game.cave.unit.misc.Exit;
import com.inari.dash.game.cave.unit.misc.Explosion;
import com.inari.dash.game.cave.unit.misc.ExplosionToDiamond;
import com.inari.dash.game.cave.unit.misc.Sand;
import com.inari.dash.game.cave.unit.misc.Space;
import com.inari.dash.game.cave.unit.rockford.Rockford;
import com.inari.dash.game.cave.unit.stone.Diamond;
import com.inari.dash.game.cave.unit.stone.Rock;
import com.inari.dash.game.cave.unit.wall.BrickWall;
import com.inari.dash.game.cave.unit.wall.ExpandingWall;
import com.inari.dash.game.cave.unit.wall.MagicWall;
import com.inari.dash.game.cave.unit.wall.SolidWall;

public enum UnitType {

    SPACE( new Space() ),
    SAND( new Sand() ),
    
    ROCK( new Rock() ),
    DIAMOND( new Diamond() ),
    
    FIREFLY( new Firefly() ),
    BUTTERFLY( new Butterfly() ),
    AMOEBA( new Amoeba() ),
    
    BRICK_WALL( new BrickWall() ),
    SOLID_WALL( new SolidWall() ),
    MAGIC_WALL( new MagicWall() ),
    EXPANDING_WALL( new ExpandingWall() ),

    ROCKFORD( new Rockford() ),
    EXIT( new Exit() ),

    EXPLOSION_TO_DIAMOND( new ExplosionToDiamond() ),
    EXPLOSION( new Explosion() ),
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
