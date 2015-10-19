package com.inari.dash.game.cave.unit.stone;

import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.system.FFContext;

public final class RockController extends StoneController {

    protected RockController( int id, FFContext context ) {
        super( id, context );
    }

    @Override
    protected final int getSoundId() {
        return UnitType.ROCK.handler.getSoundId();
    }
}
