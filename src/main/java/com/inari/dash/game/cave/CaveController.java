package com.inari.dash.game.cave;

import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFContextInitiable;
import com.inari.firefly.system.FFInitException;
import com.inari.firefly.system.UpdateEvent;
import com.inari.firefly.system.UpdateEventListener;

public final class CaveController implements FFContextInitiable, UpdateEventListener {
    
    private CaveService caveService;

    @Override
    public final void init( FFContext context ) throws FFInitException {
        caveService = context.getComponent( CaveService.CONTEXT_KEY );
        
    }
    
    @Override
    public final void dispose( FFContext context ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public final void update( UpdateEvent event ) {
        // TODO Auto-generated method stub
        
    }

    

    

}
