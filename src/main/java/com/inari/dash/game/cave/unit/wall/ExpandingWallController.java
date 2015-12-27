package com.inari.dash.game.cave.unit.wall;

import java.util.HashSet;

import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Position;
import com.inari.dash.game.cave.unit.UnitController;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.audio.event.AudioEvent;
import com.inari.firefly.audio.event.AudioEvent.Type;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.external.FFTimer;

public final class ExpandingWallController extends UnitController {

    protected ExpandingWallController( int id, FFContext context ) {
        super( id, context );
    }

    @Override
    protected final void update( FFTimer timer, int entityId ) {
        ETile tile = context.getEntityComponent( entityId, ETile.TYPE_KEY );
        for ( Position pos : new HashSet<Position>( tile.getGridPositions() ) ) {
            if ( caveService.isOfType( pos.x, pos.y, Direction.EAST, UnitType.SPACE ) ) {
                caveService.createOne( pos.x + 1, pos.y, UnitType.EXPANDING_WALL );
                context.notify( new AudioEvent( UnitType.EXPANDING_WALL.handler.getSoundId(), Type.PLAY_SOUND ) );
                return;
            }
            if ( caveService.isOfType( pos.x, pos.y, Direction.WEST, UnitType.SPACE ) ) {
                caveService.createOne( pos.x - 1, pos.y, UnitType.EXPANDING_WALL );
                context.notify( new AudioEvent( UnitType.EXPANDING_WALL.handler.getSoundId(), Type.PLAY_SOUND ) );
                return;
            }
        }
    }

}
