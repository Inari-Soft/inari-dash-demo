package com.inari.dash.game.cave.action;

import com.inari.commons.GeomUtils;
import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Position;
import com.inari.dash.game.cave.CaveService;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.action.Action;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.renderer.tile.TileGrid;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;

public final class MoveAction extends Action {

    private Position tmpPos = new Position();
    private CaveService caveService;
    private EntitySystem entitySystem;
    
    public MoveAction( int id ) {
        super( id );
    }
    
    @Override
    public final void init( FFContext context ) throws FFInitException {
        entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );
        caveService = context.getComponent( CaveService.CONTEXT_KEY );
    }

    @Override
    public final void dispose( FFContext context ) {
    }

    @Override
    public final void performAction( int entityId ) {
        TileGrid tileGrid = caveService.getTileGrid();
        
        // First we get the EUnit component and its Direction
        EUnit unit = entitySystem.getComponent( entityId, EUnit.COMPONENT_TYPE );
        Direction movement = unit.getMovement();
        if ( movement == Direction.NONE ) {
            return;
        }
        
        ETile tile = entitySystem.getComponent( entityId, ETile.COMPONENT_TYPE );
        tmpPos.x = tile.getGridXPos();
        tmpPos.y = tile.getGridYPos();
        
        UnitType.SPACE.handler.createOne( tmpPos.x, tmpPos.y );
        GeomUtils.movePositionOnDirection( tmpPos, movement, 1, true );
        int entityOnDirection = tileGrid.get( tmpPos.x, tmpPos.y );
        if ( entityOnDirection != TileGrid.NULL_VALUE ) {
            entitySystem.delete( entityOnDirection );
        }
        tile.setGridXPos( tmpPos.x );
        tile.setGridYPos( tmpPos.y );
        tileGrid.set( entityId, tmpPos.x, tmpPos.y );
        
    }

}
