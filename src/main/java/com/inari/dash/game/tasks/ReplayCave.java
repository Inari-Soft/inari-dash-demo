package com.inari.dash.game.tasks;

import com.inari.commons.event.IEventDispatcher;
import com.inari.dash.game.GameService.TaskName;
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
        IEventDispatcher eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
        eventDispatcher.notify( new TaskEvent( Type.RUN_TASK, TaskName.DISPOSE_CAVE.name() ) );
        eventDispatcher.notify( new TaskEvent( Type.RUN_TASK, TaskName.LOAD_CAVE.name() ) );
    }

}
