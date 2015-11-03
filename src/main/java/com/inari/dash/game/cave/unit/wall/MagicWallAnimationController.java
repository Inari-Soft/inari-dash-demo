package com.inari.dash.game.cave.unit.wall;

import com.inari.firefly.animation.sprite.StatedSpriteAnimation;
import com.inari.firefly.system.FFContext;

public final class MagicWallAnimationController extends StatedSpriteAnimation {
    
    public enum State {
        INACTIVE,
        ACTIVE
    }
    
    private State state = State.INACTIVE;

    public MagicWallAnimationController( int id, FFContext context ) {
        super( id, context );
    }

    public final void setMagicWallState( State state ) {
        this.state = state;
    }

    @Override
    public final int getState( int entityId ) {
        return state.ordinal();
    }

}
