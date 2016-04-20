package com.inari.dash.game.tasks;

import com.inari.dash.game.tasks.InitGameWorkflow.TaskName;
import com.inari.firefly.control.task.Task;
import com.inari.firefly.control.task.TaskSystemEvent;
import com.inari.firefly.control.task.TaskSystemEvent.Type;

public final class ReplayCave extends Task {

    protected ReplayCave( int id ) {
        super( id );
    }

    @Override
    public final void runTask() {
        context.notify( new TaskSystemEvent( Type.RUN_TASK, TaskName.DISPOSE_CAVE.name() ) );
        context.notify( new TaskSystemEvent( Type.RUN_TASK, TaskName.LOAD_CAVE.name() ) );
    }

}
