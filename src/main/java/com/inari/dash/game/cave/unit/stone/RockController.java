package com.inari.dash.game.cave.unit.stone;

import com.inari.dash.game.cave.unit.UnitType;

public final class RockController extends StoneController {

    protected RockController( int id ) {
        super( id );
    }

    @Override
    protected final int getSoundId() {
        return UnitType.ROCK.handler.getSoundId();
    }
}
