package com.inari.dash.game.tasks;

import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.tasks.InitGameWorkflow.TaskName;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;
import com.inari.firefly.task.TaskSystemEvent;
import com.inari.firefly.task.TaskSystemEvent.Type;

public final class NextCave extends Task {

    protected NextCave( int id ) {
        super( id );
    }

    @Override
    public final void run( FFContext context ) {
        CaveSystem caveSystem = context.getSystem( CaveSystem.SYSTEM_KEY );
        
        context.notify( new TaskSystemEvent( Type.RUN_TASK, TaskName.DISPOSE_CAVE.name() ) );
        caveSystem.getGameData().nextCave();
        context.notify( new TaskSystemEvent( Type.RUN_TASK, TaskName.LOAD_CAVE.name() ) );
    }

}
