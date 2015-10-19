package com.inari.dash.game.cave.unit.misc;

import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitController;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFTimer;

public final class ExplosionController extends UnitController {

    private static final int ANIMATION_DURATION = 6;

    protected ExplosionController( int id, FFContext context ) {
        super( id, context );
    }

    @Override
    protected final void update( FFTimer timer, int entityId ) {
        EUnit unit = entitySystem.getComponent( entityId, EUnit.class );
        
        unit.incrementAnimationCount();
        if ( unit.getAnimationCount() > ANIMATION_DURATION ) {
            ETile tile = entitySystem.getComponent( entityId, ETile.class );
            int xpos = tile.getGridXPos();
            int ypos = tile.getGridYPos();
            entitySystem.delete( entityId );
            unit.getExplodeTo().handler.createOne( xpos, ypos );
            unit.resetAnimationCount();
        }
    }

}
