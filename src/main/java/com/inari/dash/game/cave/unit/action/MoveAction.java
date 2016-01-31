package com.inari.dash.game.cave.unit.action;

import com.inari.commons.GeomUtils;
import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Position;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.graphics.tile.ETile;

public final class MoveAction extends UnitAction {

    private Position tmpPos = new Position();

    public MoveAction( int id ) {
        super( id );
    }

    @Override
    public final void action( int entityId ) {
        // First we get the EUnit component and its Direction
        EUnit unit = entitySystem.getComponent( entityId, EUnit.TYPE_KEY );
        Direction movement = unit.getMovement();
        if ( movement == Direction.NONE ) {
            return;
        }
        
        ETile tile = entitySystem.getComponent( entityId, ETile.TYPE_KEY );
        tmpPos.x = tile.getGridXPos();
        tmpPos.y = tile.getGridYPos();
        
        UnitType.SPACE.handler.createOne( tmpPos.x, tmpPos.y );
        GeomUtils.movePosition( tmpPos, movement, 1, true );
        int entityOnDirection = caveService.getEntityId( tmpPos.x, tmpPos.y );
        caveService.deleteUnit( entityOnDirection, tmpPos.x, tmpPos.y );
        tile.setGridXPos( tmpPos.x );
        tile.setGridYPos( tmpPos.y );
        caveService.setEntityId( entityId, tmpPos.x, tmpPos.y );
    }

}
