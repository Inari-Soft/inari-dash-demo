package com.inari.dash.game.cave.unit.enemy;

import com.inari.commons.GeomUtils;
import com.inari.commons.geom.Direction;
import com.inari.firefly.system.FFContext;

public final class FireflyController extends FlyController {

    protected FireflyController( int id, FFContext context ) {
        super( id, context );
    }

    @Override
    protected Direction changeDirection( Direction dir ) {
        return GeomUtils.rotateRight2( dir );
    }

    @Override
    protected Direction getNewDirection( Direction dir ) {
        return GeomUtils.rotateLeft2( dir );
    }

}
