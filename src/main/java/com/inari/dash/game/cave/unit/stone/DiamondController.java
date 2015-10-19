package com.inari.dash.game.cave.unit.stone;

import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.system.FFContext;

public class DiamondController extends StoneController {
    
    private final DiamondHandle diamondHandle;

    protected DiamondController( int id, FFContext context ) {
        super( id, context );
        diamondHandle = (DiamondHandle) UnitType.DIAMOND.handler;
    }

    @Override
    protected int getSoundId() {
        return diamondHandle.getSoundId();
    }



}
