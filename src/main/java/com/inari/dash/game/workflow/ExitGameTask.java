package com.inari.dash.game.workflow;

import com.badlogic.gdx.Gdx;
import com.inari.dash.game.selection.GameSelectionService;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;

public final class ExitGameTask extends Task {
    
    public static final String NAME = "ExitGameTask";

    public ExitGameTask( int id ) {
        super( id );
    }

    @Override
    public final void run( FFContext context ) {
        GameSelectionService gameSelectionService = context.getComponent( GameSelectionService.CONTEXT_KEY );
        gameSelectionService.dispose( context );
        Gdx.app.exit();
    }

}
