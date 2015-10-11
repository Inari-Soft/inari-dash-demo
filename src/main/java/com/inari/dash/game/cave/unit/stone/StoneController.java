package com.inari.dash.game.cave.unit.stone;

import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Position;
import com.inari.dash.game.cave.action.ActionType;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitController;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.action.event.ActionEvent;
import com.inari.firefly.component.attr.AttributeKey;
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
    public final AttributeKey<?>[] getControlledAttribute() {
        return null;
    }

    @Override
    protected void update( FFTimer timer, int entityId ) {
        if ( !caveService.update() ) {
            return;
        }
        
        EUnit unit = entitySystem.getComponent( entityId, EUnit.class );
        ETile tile = entitySystem.getComponent( entityId, ETile.class );
        
        tmpPos.x = tile.getGridXPos();
        tmpPos.y = tile.getGridYPos();
        
        // if we have a falling stone...
        if ( unit.getMovement() == Direction.SOUTH ) {
            // ... and below is empty
            if ( isOfType( tmpPos.x, tmpPos.y, Direction.SOUTH, UnitType.SPACE ) ) {
                // ... keep falling
                eventDispatcher.notify( new ActionEvent( ActionType.MOVE.type(),  entityId ) );
                return;
            }
            
            // roll off if possible
            if ( rollOff( entityId, unit ) ) {
                return;
            }
            
            // stop falling and hit the object below
            unit.setMovement( Direction.NONE );
            eventDispatcher.notify( 
                new ActionEvent( 
                    ActionType.HIT.type(), 
                    getEntityId( tmpPos.x, tmpPos.y, Direction.SOUTH ) 
                ) 
            );
            playSample();
            return;
        }
        
        // we have a stationary stone, check space below...
        if ( isOfType( tmpPos.x, tmpPos.y, Direction.SOUTH, UnitType.SPACE ) ) {
            // start falling
            unit.setMovement( Direction.SOUTH );
            playSample();
            eventDispatcher.notify( new ActionEvent( ActionType.MOVE.type(),  entityId ) );
            return;
        }
        
        // roll of if possible
        rollOff( entityId, unit );
    }

    private boolean rollOff( int entityId, EUnit unit ) {
        // only roll of if the ground is aslope
        if ( !hasAspect( tmpPos.x, tmpPos.y, Direction.SOUTH, UnitAspect.ASLOPE ) ) {
            return false;
        }
        
        if ( isOfType( tmpPos.x, tmpPos.y, Direction.SOUTH_EAST, UnitType.SPACE ) && 
             isOfType( tmpPos.x, tmpPos.y, Direction.EAST, UnitType.SPACE ) && 
             !isMoving( tmpPos.x, tmpPos.y, Direction.NORTH_EAST, Direction.SOUTH ) ) {
            
            // fall to the right
            unit.setMovement( Direction.EAST );
            eventDispatcher.notify( 
                new ActionEvent( ActionType.MOVE.type(), entityId ) 
            );
            unit.setMovement( Direction.SOUTH );
            playSample();
            return true;
        } 
        
        if ( isOfType( tmpPos.x, tmpPos.y, Direction.SOUTH_WEST, UnitType.SPACE ) && 
             isOfType( tmpPos.x, tmpPos.y, Direction.WEST, UnitType.SPACE ) &&
             !isMoving( tmpPos.x, tmpPos.y, Direction.NORTH_WEST, Direction.SOUTH ) ) {
            
            // fall to the left
            unit.setMovement( Direction.WEST );
            eventDispatcher.notify( 
                new ActionEvent( ActionType.MOVE.type(), entityId ) 
            );
            unit.setMovement( Direction.SOUTH );
            playSample();
            return true;
        }
        return false;
    }
    
    private final void playSample() {
        int soundId = getSoundId();
        if ( soundId < 0 ) {
            return;
        }
        eventDispatcher.notify( new SoundEvent( soundId, SoundEvent.Type.PLAY_SOUND ) );
    }

    protected abstract int getSoundId();

}
