package com.inari.dash.game.tasks;

import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;

public final class DisposeGame extends Task {

    public DisposeGame( int id ) {
        super( id );
    }

    @Override
    public final void run( FFContext context ) {
        context.exit();
    }

}
