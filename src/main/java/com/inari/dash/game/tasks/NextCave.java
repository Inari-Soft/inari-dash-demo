package com.inari.dash.game.tasks;

import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.tasks.InitGameWorkflow.TaskName;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;
import com.inari.firefly.task.event.TaskEvent;
import com.inari.firefly.task.event.TaskEvent.Type;

public final class NextCave extends Task {

    protected NextCave( int id ) {
        super( id );
    }

    @Override
    public final void run( FFContext context ) {
        CaveSystem caveSystem = context.getSystem( CaveSystem.CONTEXT_KEY );
        
        context.notify( new TaskEvent( Type.RUN_TASK, TaskName.DISPOSE_CAVE.name() ) );
        caveSystem.getGameData().nextCave();
        context.notify( new TaskEvent( Type.RUN_TASK, TaskName.LOAD_CAVE.name() ) );
    }

}
