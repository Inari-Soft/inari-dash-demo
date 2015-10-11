package com.inari.dash.game.workflow;

import com.inari.dash.game.GameService;
import com.inari.dash.game.selection.GameSelectionService;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;

public final class InitGameTask extends Task {
    
    public static final String NAME = "InitGameTask";

    public InitGameTask( int id ) {
        super( id );
    }

    @Override
    public final void run( FFContext context ) {
        GameService gameService = context.getComponent( GameService.CONTEXT_KEY );
        GameSelectionService gameSelectionService = context.getComponent( GameSelectionService.CONTEXT_KEY );
        
        gameService.loadGlobalAssets();
        gameSelectionService.load( context );
        gameService.playIntroSong();
    }

}
