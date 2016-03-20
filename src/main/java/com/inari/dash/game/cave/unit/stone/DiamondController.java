package com.inari.dash.game.cave.unit.stone;

import com.inari.dash.game.cave.unit.UnitType;

public final class DiamondController extends StoneController {
    
    private final Diamond diamondHandle;

    protected DiamondController( int id ) {
        super( id );
        diamondHandle = (Diamond) UnitType.DIAMOND.handler;
    }

    @Override
    protected final int getSoundId() {
        return diamondHandle.getSoundId();
    }

}
