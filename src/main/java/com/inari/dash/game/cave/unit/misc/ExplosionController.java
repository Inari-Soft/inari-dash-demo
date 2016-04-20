package com.inari.dash.game.cave.unit.misc;

import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitController;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.system.external.FFTimer;

public final class ExplosionController extends UnitController {

    private static final int ANIMATION_DURATION = 6;

    protected ExplosionController( int id ) {
        super( id );
    }

    @Override
    protected final void update( FFTimer timer, int entityId ) {
        EUnit unit = context.getEntityComponent( entityId, EUnit.TYPE_KEY );
        
        unit.incrementAnimationCount();
        if ( unit.getAnimationCount() > ANIMATION_DURATION ) {
            ETile tile = context.getEntityComponent( entityId, ETile.TYPE_KEY );
            int xpos = tile.getGridXPos();
            int ypos = tile.getGridYPos();
            UnitType explodeToType = unit.getChangeTo();
            context.deleteEntity( entityId );
            getUnit( explodeToType ).createOne( xpos, ypos );
        }
    }

}
