package com.inari.dash.game.cave.unit.misc;

import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.firefly.animation.sprite.StatedSpriteAnimation;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.system.FFContext;

public final class ExitAnimationController extends StatedSpriteAnimation {
    
    private final EntitySystem entitySystem;

    public ExitAnimationController( int id, FFContext context ) {
        super( id, context );
        entitySystem = context.getSystem( EntitySystem.CONTEXT_KEY );
    }

    @Override
    public final int getState( int entityId ) {
        EUnit exitUnit = entitySystem.getComponent( entityId, EUnit.class );
        return ( exitUnit.has( UnitAspect.ACTIVE ) )? 
            Exit.State.OPEN.ordinal() : Exit.State.CLOSED.ordinal();
    }

}
