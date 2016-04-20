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

    SPACE( Space.class ),
    SAND( Sand.class ),
    
    ROCK( Rock.class ),
    DIAMOND( Diamond.class ),
    
    FIREFLY( Firefly.class ),
    BUTTERFLY( Butterfly.class ),
    AMOEBA( Amoeba.class ),
    
    BRICK_WALL( BrickWall.class ),
    SOLID_WALL( SolidWall.class ),
    MAGIC_WALL( MagicWall.class ),
    EXPANDING_WALL( ExpandingWall.class ),

    ROCKFORD( Rockford.class ),
    EXIT( Exit.class ),

    EXPLOSION_TO_DIAMOND( ExplosionToDiamond.class ),
    EXPLOSION( Explosion.class ),
    ;
    
    public final Class<? extends Unit> unitType;
    private UnitType( Class<? extends Unit> unitType ) {
        this.unitType = unitType;
    }
}
