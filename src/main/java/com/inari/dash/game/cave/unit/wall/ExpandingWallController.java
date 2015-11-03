package com.inari.dash.game.cave.unit.wall;

import java.util.HashSet;

import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Position;
import com.inari.dash.game.cave.unit.UnitController;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.sound.event.SoundEvent;
import com.inari.firefly.sound.event.SoundEvent.Type;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFTimer;

public final class ExpandingWallController extends UnitController {

    protected ExpandingWallController( int id, FFContext context ) {
        super( id, context );
    }

    @Override
    protected final void update( FFTimer timer, int entityId ) {
        ETile tile = entitySystem.getComponent( entityId, ETile.class );
        for ( Position pos : new HashSet<Position>( tile.getGridPositions() ) ) {
            if ( caveService.isOfType( pos.x, pos.y, Direction.EAST, UnitType.SPACE ) ) {
                caveService.createOne( pos.x + 1, pos.y, UnitType.EXPANDING_WALL );
                eventDispatcher.notify( new SoundEvent( UnitType.EXPANDING_WALL.handler.getSoundId(), Type.PLAY_SOUND ) );
                return;
            }
            if ( caveService.isOfType( pos.x, pos.y, Direction.WEST, UnitType.SPACE ) ) {
                caveService.createOne( pos.x - 1, pos.y, UnitType.EXPANDING_WALL );
                eventDispatcher.notify( new SoundEvent( UnitType.EXPANDING_WALL.handler.getSoundId(), Type.PLAY_SOUND ) );
                return;
            }
        }
    }

}
