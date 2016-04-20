package com.inari.dash.game.tasks;

import com.inari.firefly.control.task.Task;

public final class DisposeGame extends Task {

    public DisposeGame( int id ) {
        super( id );
    }

    @Override
    public final void runTask()  {
        context.exit();
    }

}
