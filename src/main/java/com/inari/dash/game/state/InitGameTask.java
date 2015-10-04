package com.inari.dash.game.state;

import com.inari.dash.game.GameService;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;

public final class InitGameTask extends Task {

    public InitGameTask( int id ) {
        super( id );
    }

    @Override
    public final void run( FFContext context ) {
        GameService gameService = context.getComponent( GameService.CONTEXT_KEY );
        
        gameService.loadGlobalAssets();
        gameService.loadGameSelection();
        gameService.playIntroSong();
    }

}
