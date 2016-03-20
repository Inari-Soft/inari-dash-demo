package com.inari.dash.game.cave.unit.enemy;

import com.inari.commons.geom.Direction;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitController;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.dash.game.cave.unit.action.UnitActionType;
import com.inari.firefly.action.ActionSystemEvent;
import com.inari.firefly.system.external.FFTimer;

public abstract class FlyController extends UnitController {

    protected FlyController( int id ) {
        super( id );
    }
    
    @Override
    protected final void update( FFTimer timer, int entityId ) {
        EUnit unit = context.getEntityComponent( entityId, EUnit.TYPE_KEY );
        
        if ( caveService.hasInSurrounding( entityId, UnitType.AMOEBA ) || 
             caveService.hasInSurrounding( entityId, UnitType.ROCKFORD, UnitAspect.ALIVE ) ||
             unit.isHit() 
         ) {
            
            context.notify( new ActionSystemEvent( UnitActionType.EXPLODE.index(), entityId ) );
            return;
        }
        
        Direction currentDirection = unit.getMovement();
        Direction newDirection = getNewDirection( currentDirection );
        // if we have space on new direction, change to new direction and move
        if ( caveService.isOfType( entityId, newDirection, UnitType.SPACE ) ) {
            
            unit.setMovement( newDirection );
            context.notify( new ActionSystemEvent( UnitActionType.MOVE.index(), entityId ) );
            return;
        }
        
        // if we have space on the way, move along
        if ( caveService.isOfType( entityId, currentDirection, UnitType.SPACE ) ) {
            context.notify( new ActionSystemEvent( UnitActionType.MOVE.index(), entityId ) );
            return;
        }
        
        unit.setMovement( changeDirection( currentDirection ) );
    }
    
    protected abstract Direction changeDirection( Direction dir );

    protected abstract Direction getNewDirection( Direction dir );

}
