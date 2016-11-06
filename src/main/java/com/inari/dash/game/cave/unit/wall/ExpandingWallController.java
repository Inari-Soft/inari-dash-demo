package com.inari.dash.game.cave.unit.wall;

import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Position;
import com.inari.dash.game.cave.unit.Unit;
import com.inari.dash.game.cave.unit.UnitController;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.audio.AudioSystemEvent;
import com.inari.firefly.audio.AudioSystemEvent.Type;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.prototype.Prototype;

public final class ExpandingWallController extends UnitController {
    
    private Unit expandingWall = null;

    protected ExpandingWallController( int id ) {
        super( id );
    }

    @Override
    protected final void update( int entityId ) {
        ETile tile = context.getEntityComponent( entityId, ETile.TYPE_KEY );
        if ( expandingWall == null ) {
            expandingWall = context.getSystemComponent( Prototype.TYPE_KEY, UnitType.EXPANDING_WALL.ordinal(), Unit.class );
        }
        
        for ( Position pos : tile.getGridPositions() ) {
            if ( caveService.isOfType( pos.x, pos.y, Direction.EAST, UnitType.SPACE ) ) {
                caveService.createOne( pos.x + 1, pos.y, UnitType.EXPANDING_WALL );
                context.notify( new AudioSystemEvent( expandingWall.getSoundId(), Type.PLAY_SOUND ) );
                return;
            }
            if ( caveService.isOfType( pos.x, pos.y, Direction.WEST, UnitType.SPACE ) ) {
                caveService.createOne( pos.x - 1, pos.y, UnitType.EXPANDING_WALL );
                context.notify( new AudioSystemEvent( expandingWall.getSoundId(), Type.PLAY_SOUND ) );
                return;
            }
        }
    }

}
