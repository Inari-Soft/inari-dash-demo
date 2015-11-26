package com.inari.dash.game.tasks;

import com.inari.dash.game.tasks.InitGameWorkflow.TaskName;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;
import com.inari.firefly.task.event.TaskEvent;
import com.inari.firefly.task.event.TaskEvent.Type;

public final class ReplayCave extends Task {

    protected ReplayCave( int id ) {
        super( id );
    }

    @Override
    public final void run( FFContext context ) {
        context.notify( new TaskEvent( Type.RUN_TASK, TaskName.DISPOSE_CAVE.name() ) );
        context.notify( new TaskEvent( Type.RUN_TASK, TaskName.LOAD_CAVE.name() ) );
    }

}
