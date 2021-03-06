package com.inari.dash.game.cave.unit.misc;

import java.util.ArrayList;
import java.util.Collection;

import com.inari.commons.GeomUtils;
import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Position;
import com.inari.commons.lang.list.DynArray;
import com.inari.dash.game.cave.CaveSystem.AmoebaData;
import com.inari.dash.game.cave.unit.Unit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitController;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.audio.AudioSystemEvent;
import com.inari.firefly.audio.AudioSystemEvent.Type;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.system.FireFlyApp;

public final class AmoebaController extends UnitController {
    
    private static final int TIME_FACTOR = 2;
    
    private boolean soundPaying = false;
    private float growthFaktor = -1;
    private long tick = 0;
    private Position tmpPos = new Position();
    private Collection<Position> tmpPosList = new ArrayList<Position>();

    protected AmoebaController( int id ) {
        super( id );
    }

    @Override
    protected final void update( int entityId ) {
        Unit amoebaUnit = getUnit( UnitType.AMOEBA );
        EEntity entity = context.getEntityComponent( entityId, EEntity.TYPE_KEY );
        if ( !entity.hasAspect( UnitAspect.ACTIVE ) ) {
            return;
        }
        
        tick++;

        if ( !soundPaying ) {
            context.notify( new AudioSystemEvent( amoebaUnit.getSoundId(), Type.PLAY_SOUND ) );
            soundPaying = true;
        }
        
        AmoebaData amoebaData = caveService.getAmoebaData();
        ETile tile = context.getEntityComponent( entityId, ETile.TYPE_KEY );
        
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
            amoebaUnit.createOne( newPos.x, newPos.y );
        }
    }

    private boolean grow( Direction direction ) {
        GeomUtils.movePosition( tmpPos, direction, 1, true );
        if ( caveService.hasAspect( tmpPos.x, tmpPos.y, UnitAspect.CONSUMABLE ) ) {
            if ( FireFlyApp.RANDOM.nextFloat() <= growthFaktor ) {
                tmpPosList.add( new Position( tmpPos.x, tmpPos.y ) );
            }
            return true;
        }
        return caveService.hasAspect( tmpPos.x, tmpPos.y, UnitAspect.ALIVE );
    }

    private void transformTo( UnitType type, int entityId, DynArray<Position> gridPositions ) {
        context.deactivateEntity( entityId );
        for ( Position pos : gridPositions ) {
            getUnit( type ).createOne( pos.x, pos.y );
        }
        context.deleteEntity( entityId );
        context.notify( new AudioSystemEvent( getUnit( UnitType.AMOEBA ).getSoundId(), Type.STOP_PLAYING ) );
    }

}
