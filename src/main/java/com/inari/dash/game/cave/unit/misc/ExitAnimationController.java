package com.inari.dash.game.cave.unit.misc;

import com.inari.dash.game.cave.CaveService;
import com.inari.dash.game.cave.CaveService.CaveState;
import com.inari.firefly.animation.sprite.StatedSpriteAnimation;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFTimer;

public final class ExitAnimationController extends StatedSpriteAnimation{
    
    private final CaveService caveService;

    public ExitAnimationController( int id, FFContext context ) {
        super( id, context );
        caveService = context.getComponent( CaveService.CONTEXT_KEY );
    }
    
    

    @Override
    public void update( FFTimer timer ) {
        // TODO Auto-generated method stub
        super.update( timer );
    }



    @Override
    public final int getState( int entityId ) {
        return ( caveService.getCaveState() == CaveState.EXIT_OPEN )? 
            ExitHandle.State.OPEN.ordinal() : ExitHandle.State.CLOSED.ordinal();
    }

}
