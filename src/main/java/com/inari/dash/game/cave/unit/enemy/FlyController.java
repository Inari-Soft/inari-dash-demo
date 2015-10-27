package com.inari.dash.game.cave.unit.enemy;

import com.inari.commons.geom.Direction;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitController;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.dash.game.cave.unit.action.UnitActionType;
import com.inari.firefly.action.event.ActionEvent;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFTimer;

public abstract class FlyController extends UnitController {

    protected FlyController( int id, FFContext context ) {
        super( id, context );
    }
    
    @Override
    protected final void update( FFTimer timer, int entityId ) {
        EUnit unit = entitySystem.getComponent( entityId, EUnit.class );
        
        if ( caveService.hasTypeInSurrounding( entityId, UnitType.AMOEBA ) || 
             caveService.hasTypeInSurrounding( entityId, UnitType.ROCKFORD ) ||
             unit.isHit() 
         ) {
            
            eventDispatcher.notify( new ActionEvent( UnitActionType.EXPLODE.index(), entityId ) );
            return;
        }
        
        Direction currentDirection = unit.getMovement();
        Direction newDirection = getNewDirection( currentDirection );
        // if we have space on new direction, change to new direction and move
        if ( caveService.isOfType( entityId, newDirection, UnitType.SPACE ) ) {
            
            unit.setMovement( newDirection );
            eventDispatcher.notify( new ActionEvent( UnitActionType.MOVE.index(), entityId ) );
            return;
        }
        
        // if we have space on the way, move along
        if ( caveService.isOfType( entityId, currentDirection, UnitType.SPACE ) ) {
            eventDispatcher.notify( new ActionEvent( UnitActionType.MOVE.index(), entityId ) );
            return;
        }
        
        unit.setMovement( changeDirection( currentDirection ) );
    }
    
    protected abstract Direction changeDirection( Direction dir );

    protected abstract Direction getNewDirection( Direction dir );

}
