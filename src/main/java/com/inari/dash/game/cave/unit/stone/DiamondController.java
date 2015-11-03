package com.inari.dash.game.cave.unit.stone;

import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.system.FFContext;

public class DiamondController extends StoneController {
    
    private final Diamond diamondHandle;

    protected DiamondController( int id, FFContext context ) {
        super( id, context );
        diamondHandle = (Diamond) UnitType.DIAMOND.handler;
    }

    @Override
    protected int getSoundId() {
        return diamondHandle.getSoundId();
    }



}
