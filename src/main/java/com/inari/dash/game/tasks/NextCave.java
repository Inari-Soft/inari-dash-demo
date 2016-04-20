package com.inari.dash.game.tasks;

import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.tasks.InitGameWorkflow.TaskName;
import com.inari.firefly.control.task.Task;
import com.inari.firefly.control.task.TaskSystemEvent;
import com.inari.firefly.control.task.TaskSystemEvent.Type;

public final class NextCave extends Task {

    protected NextCave( int id ) {
        super( id );
    }

    @Override
    public final void runTask() {
        CaveSystem caveSystem = context.getSystem( CaveSystem.SYSTEM_KEY );
        
        context.notify( new TaskSystemEvent( Type.RUN_TASK, TaskName.DISPOSE_CAVE.name() ) );
        caveSystem.getGameData().nextCave();
        context.notify( new TaskSystemEvent( Type.RUN_TASK, TaskName.LOAD_CAVE.name() ) );
    }

}
