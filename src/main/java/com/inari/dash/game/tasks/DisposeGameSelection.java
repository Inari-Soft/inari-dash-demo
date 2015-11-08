package com.inari.dash.game.tasks;

import com.inari.dash.game.GameService;
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
        EntitySystem entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );
        ControllerSystem controllerSystem = context.getComponent( ControllerSystem.CONTEXT_KEY );
        
        controllerSystem.deleteController( GameService.GAME_SELECTION_CONTROLLER_NAME );

        entitySystem.delete( GameService.ENTITY_NAME_GAME_SELECTION_TITLE );
        entitySystem.delete( GameService.ENTITY_NAME_GAME_SELECTION );
        entitySystem.delete( GameService.ENTITY_NAME_CAVE_SELECTION_TITLE );
        entitySystem.delete( GameService.ENTITY_NAME_CAVE_SELECTION );
        entitySystem.delete( GameService.ENTITY_NAME_EXIT_TITLE );
        
    }

}
