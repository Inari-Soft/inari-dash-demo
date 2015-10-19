package com.inari.dash.game.cave.unit.misc;

import java.util.ArrayList;
import java.util.Collection;

import com.inari.commons.GeomUtils;
import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Position;
import com.inari.commons.lang.list.DynArray;
import com.inari.dash.game.cave.CaveService.AmoebaData;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitController;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.sound.event.SoundEvent;
import com.inari.firefly.sound.event.SoundEvent.Type;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFTimer;
import com.inari.firefly.system.FireFly;

public final class AmoebaController extends UnitController {
    
    private static final int TIME_FACTOR = 2;
    
    private boolean soundPaying = false;
    private float growthFaktor = -1;
    private long tick = 0;
    private Position tmpPos = new Position();
    private Collection<Position> tmpPosList = new ArrayList<Position>();

    protected AmoebaController( int id, FFContext context ) {
        super( id, context );
    }

    @Override
    protected final void update( FFTimer timer, int entityId ) {
        EUnit unit = entitySystem.getComponent( entityId, EUnit.class );
        if ( !unit.has( UnitAspect.ACTIVE ) ) {
            return;
        }
        
        tick++;

        if ( !soundPaying ) {
            eventDispatcher.notify( new SoundEvent( UnitType.AMOEBA.handler.getSoundId(), Type.PLAY_SOUND ) );
            soundPaying = true;
        }
        
        AmoebaData amoebaData = caveService.getAmoebaData();
        ETile tile = entitySystem.getComponent( entityId, ETile.class );
        
        if ( growthFaktor < 0 ) {
            growthFaktor = amoebaData.amoebaSlowGrowthProb;
        } else if ( tick / TIME_FACTOR > amoebaData.amoebaTime ) {
            growthFaktor = amoebaData.amoebaFastGrowthProb;
        }
        
        DynArray<Position> gridPositions = tile.getGridPositions();
        
        if ( gridPositions.size() > amoebaData.growthLimit ) {
            transformTo( UnitType.ROCK, entityId, gridPositions );
            return;
        }
        
        tmpPosList.clear();
        boolean hasSpace = false;
        for ( Position pos : gridPositions ) {
            tmpPos.x = pos.x;
            tmpPos.y = pos.y;
            if ( grow( Direction.NORTH ) ) {
                hasSpace = true;
            }
            tmpPos.x = pos.x;
            tmpPos.y = pos.y;
            if ( grow( Direction.EAST ) ) {
                hasSpace = true;
            }
            tmpPos.x = pos.x;
            tmpPos.y = pos.y;
            if ( grow( Direction.SOUTH ) ) {
                hasSpace = true;
            }
            tmpPos.x = pos.x;
            tmpPos.y = pos.y;
            if ( grow( Direction.WEST ) ) {
                hasSpace = true;
            }
        }
        
        if ( !hasSpace ) {
            transformTo( UnitType.DIAMOND, entityId, gridPositions );
            return;
        }
        
        for ( Position newPos : tmpPosList ) {
            UnitType.AMOEBA.handler.createOne( newPos.x, newPos.y );
        }
    }

    private boolean grow( Direction direction ) {
        GeomUtils.movePositionOnDirection( tmpPos, direction, 1, true );
        if ( caveService.hasAspect( tmpPos.x, tmpPos.y, UnitAspect.CONSUMABLE ) ) {
            if ( FireFly.RANDOM.nextFloat() <= growthFaktor ) {
                tmpPosList.add( new Position( tmpPos.x, tmpPos.y ) );
            }
            return true;
        }
        return false;
    }

    private void transformTo( UnitType type, int entityId, DynArray<Position> gridPositions ) {
        entitySystem.delete( entityId );
        for ( Position pos : gridPositions ) {
            type.handler.createOne( pos.x, pos.y );
        }
        eventDispatcher.notify( new SoundEvent( UnitType.AMOEBA.handler.getSoundId(), Type.STOP_PLAYING ) );
    }

}
