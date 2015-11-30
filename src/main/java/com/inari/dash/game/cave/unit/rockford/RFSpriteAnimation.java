package com.inari.dash.game.cave.unit.rockford;

import com.inari.firefly.animation.sprite.StatedSpriteAnimation;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.system.FFContext;

public final class RFSpriteAnimation extends StatedSpriteAnimation {
    
    private final EntitySystem entitySystem;

    public RFSpriteAnimation( int id, FFContext context ) {
        super( id, context );
        entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
    }

    @Override
    public int getState( int entityId ) {
        RFUnit unit = entitySystem.getComponent( entityId, RFUnit.TYPE_KEY );
        return unit.getState().ordinal();
    }

}
