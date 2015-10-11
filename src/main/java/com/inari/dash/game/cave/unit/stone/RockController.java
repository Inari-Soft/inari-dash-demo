package com.inari.dash.game.cave.unit.stone;

import com.inari.firefly.system.FFContext;

public class RockController extends StoneController {
    
    private int soundId = -1;

    public RockController( int id, FFContext context ) {
        super( id, context );
    }

    @Override
    protected int getSoundId() {
        if ( soundId < 0 ) {
            soundId = soundSystem.getSoundId( RockHandle.ROCK_SOUND_ASSEET_KEY.name );
        }
        return soundId;
    }

}
