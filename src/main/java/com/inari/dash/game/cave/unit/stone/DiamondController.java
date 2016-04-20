package com.inari.dash.game.cave.unit.stone;

import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.prototype.Prototype;

public final class DiamondController extends StoneController {

    protected DiamondController( int id ) {
        super( id );
    }

    @Override
    protected final int getSoundId() {
        return context.getSystemComponent( Prototype.TYPE_KEY, UnitType.DIAMOND.name(), Diamond.class ).getSoundId();
    }

}
