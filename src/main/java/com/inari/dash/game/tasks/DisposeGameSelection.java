package com.inari.dash.game.tasks;

import com.inari.dash.game.GameSystem;
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;

public final class DisposeGameSelection extends Task {

    protected DisposeGameSelection( int id ) {
        super( id );
    }

    @Override
    public void run( FFContext context ) {
        EntitySystem entitySystem = context.getSystem( EntitySystem.CONTEXT_KEY );
        ControllerSystem controllerSystem = context.getSystem( ControllerSystem.CONTEXT_KEY );
        GameSystem gameSystem = context.getSystem( GameSystem.CONTEXT_KEY );
        
        gameSystem.resetSelection();
        
        controllerSystem.deleteController( GameSystem.GAME_SELECTION_CONTROLLER_NAME );

        entitySystem.delete( GameSystem.ENTITY_NAME_GAME_SELECTION_TITLE );
        entitySystem.delete( GameSystem.ENTITY_NAME_GAME_SELECTION );
        entitySystem.delete( GameSystem.ENTITY_NAME_CAVE_SELECTION_TITLE );
        entitySystem.delete( GameSystem.ENTITY_NAME_CAVE_SELECTION );
        entitySystem.delete( GameSystem.ENTITY_NAME_EXIT_TITLE );
        
    }

}
