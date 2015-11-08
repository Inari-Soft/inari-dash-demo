package com.inari.dash.game.tasks;

import com.inari.dash.game.cave.CaveService;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;

public final class DisposeCave extends Task {

    protected DisposeCave( int id ) {
        super( id );
    }

    @Override
    public final void run( FFContext context ) {
        CaveService caveService = context.getComponent( CaveService.CONTEXT_KEY );
        
        caveService.disposeCave( context );
        caveService.dispose( context );
    }

}
