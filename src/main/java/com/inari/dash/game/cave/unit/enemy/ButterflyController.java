package com.inari.dash.game.cave.unit.enemy;

import com.inari.commons.GeomUtils;
import com.inari.commons.geom.Direction;
import com.inari.firefly.system.FFContext;

public final class ButterflyController extends FlyController {

    protected ButterflyController( int id, FFContext context ) {
        super( id, context );
    }

    @Override
    protected final Direction changeDirection( Direction dir ) {
        return GeomUtils.rotateLeft2( dir );
    }

    @Override
    protected final Direction getNewDirection( Direction dir ) {
        return GeomUtils.rotateRight2( dir );
    }

}
