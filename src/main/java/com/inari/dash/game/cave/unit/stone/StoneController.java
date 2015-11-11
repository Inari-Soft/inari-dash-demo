package com.inari.dash.game.cave.unit.stone;

import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Position;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitController;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.dash.game.cave.unit.action.UnitActionType;
import com.inari.firefly.action.event.ActionEvent;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.sound.event.SoundEvent;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFTimer;

public abstract class StoneController extends UnitController {
    
    private final Position tmpPos = new Position();

    protected StoneController( int id, FFContext context ) {
        super( id, context );
    }

    @Override
    protected void update( FFTimer timer, int entityId ) {
        long update = timer.getTime();
        EUnit unit = entitySystem.getComponent( entityId, EUnit.class );
        ETile tile = entitySystem.getComponent( entityId, ETile.class );
        
        tmpPos.x = tile.getGridXPos();
        tmpPos.y = tile.getGridYPos();
        
        // if we have a falling stone...
        if ( unit.getMovement() == Direction.SOUTH ) {
            // ... and below is empty
            if ( caveService.isOfType( tmpPos.x, tmpPos.y, Direction.SOUTH, UnitType.SPACE ) ) {
                // ... keep falling
                eventDispatcher.notify( new ActionEvent( UnitActionType.MOVE.type(),  entityId ) );
                return;
            }
            
            // roll off if possible
            if ( rollOff( entityId, unit, update ) ) {
                return;
            }
            
            // stop falling and hit the object below
            unit.setMovement( Direction.NONE );
            EUnit unitBelow = entitySystem.getComponent( 
                caveService.getEntityId( tmpPos.x, tmpPos.y, Direction.SOUTH ), 
                EUnit.class 
            );
            unitBelow.setHit( true );

            playSample( update );
            return;
        }
        
        // we have a stationary stone, check space below...
        if ( caveService.isOfType( tmpPos.x, tmpPos.y, Direction.SOUTH, UnitType.SPACE ) ) {
            // start falling... or wait for next update
            unit.setMovement( Direction.SOUTH );
            playSample( update );
            eventDispatcher.notify( new ActionEvent( UnitActionType.MOVE.type(),  entityId ) );
            return;
        }
        
        // roll of if possible
        rollOff( entityId, unit, update );
    }

    private boolean rollOff( int entityId, EUnit unit, long update ) {
        // only roll of if the ground is aslope
        if ( !caveService.hasAspect( tmpPos.x, tmpPos.y, Direction.SOUTH, UnitAspect.ASLOPE ) ) {
            return false;
        }
        
        if ( caveService.isOfType( tmpPos.x, tmpPos.y, Direction.SOUTH_EAST, UnitType.SPACE ) && 
             caveService.isOfType( tmpPos.x, tmpPos.y, Direction.EAST, UnitType.SPACE ) && 
             !caveService.isMoving( tmpPos.x, tmpPos.y, Direction.NORTH_EAST, Direction.SOUTH ) ) {
            
            // fall to the right
            unit.setMovement( Direction.EAST );
            eventDispatcher.notify( 
                new ActionEvent( UnitActionType.MOVE.type(), entityId ) 
            );
            unit.setMovement( Direction.SOUTH );
            playSample( update );
            return true;
        } 
        
        if ( caveService.isOfType( tmpPos.x, tmpPos.y, Direction.SOUTH_WEST, UnitType.SPACE ) && 
             caveService.isOfType( tmpPos.x, tmpPos.y, Direction.WEST, UnitType.SPACE ) &&
             !caveService.isMoving( tmpPos.x, tmpPos.y, Direction.NORTH_WEST, Direction.SOUTH ) ) {
            
            // fall to the left
            unit.setMovement( Direction.WEST );
            eventDispatcher.notify( 
                new ActionEvent( UnitActionType.MOVE.type(), entityId ) 
            );
            unit.setMovement( Direction.SOUTH );
            playSample( update );
            return true;
        }
        return false;
    }
    
    private long currentUpdate = 0;
    private final void playSample( long update ) {
        if ( currentUpdate != update ) {
            int soundId = getSoundId();
            if ( soundId < 0 ) {
                return;
            }
            eventDispatcher.notify( new SoundEvent( soundId, SoundEvent.Type.PLAY_SOUND ) );
            currentUpdate = update;
        }
    }

    protected abstract int getSoundId();

}
