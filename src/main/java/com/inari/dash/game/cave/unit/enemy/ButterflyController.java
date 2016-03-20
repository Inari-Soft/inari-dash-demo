package com.inari.dash.game.cave.unit.enemy;

import com.inari.commons.GeomUtils;
import com.inari.commons.geom.Direction;

public final class ButterflyController extends FlyController {

    protected ButterflyController( int id ) {
        super( id );
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
