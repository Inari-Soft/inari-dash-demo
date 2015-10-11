package com.inari.dash.game.cave;

import com.inari.firefly.control.Controller;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFTimer;

public final class HeaderController extends Controller {
    
    private CaveService caveService;

    public HeaderController( int id, FFContext context ) {
        super( id );
        caveService = context.getComponent( CaveService.CONTEXT_KEY );
    }
    
    @Override
    public final void update( FFTimer timer ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public final void dispose( FFContext context ) {
        caveService = null;
    }

}
