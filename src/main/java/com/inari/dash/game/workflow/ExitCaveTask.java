package com.inari.dash.game.workflow;

import com.inari.dash.game.GameService;
import com.inari.dash.game.cave.CaveService;
import com.inari.dash.game.selection.GameSelectionService;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;

public final class ExitCaveTask extends Task {
    
    public static final String NAME = "ExitCaveTask";

    protected ExitCaveTask( int id ) {
        super( id );
    }

    @Override
    public final void run( FFContext context ) {
        CaveService caveService = context.getComponent( CaveService.CONTEXT_KEY );
        GameService gameService = context.getComponent( GameService.CONTEXT_KEY );
        GameSelectionService gameSelectionService = context.getComponent( GameSelectionService.CONTEXT_KEY );
        
        caveService.disposeCave( context );
        caveService.dispose( context );
        
        gameSelectionService.load( context );
        gameService.playIntroSong();
    }

}
