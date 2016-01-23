package com.inari.dash.game.tasks;

import com.inari.dash.game.tasks.InitGameWorkflow.TaskName;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;
import com.inari.firefly.task.TaskSystemEvent;
import com.inari.firefly.task.TaskSystemEvent.Type;

public final class ReplayCave extends Task {

    protected ReplayCave( int id ) {
        super( id );
    }

    @Override
    public final void run( FFContext context ) {
        context.notify( new TaskSystemEvent( Type.RUN_TASK, TaskName.DISPOSE_CAVE.name() ) );
        context.notify( new TaskSystemEvent( Type.RUN_TASK, TaskName.LOAD_CAVE.name() ) );
    }

}
