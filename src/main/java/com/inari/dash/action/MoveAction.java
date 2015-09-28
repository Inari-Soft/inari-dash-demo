package com.inari.dash.action;

import com.inari.commons.GeomUtils;
import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Position;
import com.inari.dash.Constants;
import com.inari.dash.unit.EUnit;
import com.inari.firefly.action.Action;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.renderer.tile.TileGrid;
import com.inari.firefly.renderer.tile.TileGridSystem;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;

public final class MoveAction extends Action {
    
    protected MoveAction( int id ) {
        super( id );
    }

    private TileGrid tileGrid;
    private EntitySystem entitySystem;
    
    @Override
    public final void init( FFContext context ) throws FFInitException {
        entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );
        tileGrid = context.getComponent( TileGridSystem.CONTEXT_KEY )
            .getTileGrid( Constants.GAME_VIEW_NAME );
    }

    @Override
    public final void dispose( FFContext context ) {
    }

    @Override
    public final void performAction( int entityId ) {
        // First we get the EUnit component and its Direction
        EUnit unit = entitySystem.getComponent( entityId, EUnit.COMPONENT_TYPE );
        Direction movement = unit.getMovement();
        if ( movement == Direction.NONE ) {
            return;
        }
        
        ETile tile = entitySystem.getComponent( entityId, ETile.COMPONENT_TYPE );
        Position position = tile.getGridPosition();
        
        tileGrid.set( TileGrid.NULL_VALUE, position.x, position.y );
        GeomUtils.movePositionOnDirection( position, movement, 1, true );
        int entityOnDirection = tileGrid.get( position.x, position.y );
        if ( entityOnDirection != TileGrid.NULL_VALUE ) {
            entitySystem.delete( entityOnDirection );
        }
        tileGrid.set( entityId, position.x, position.y );
    }

}
