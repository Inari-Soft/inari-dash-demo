package com.inari.dash.game.workflow;

import com.inari.dash.game.selection.GameSelectionService;
import com.inari.dash.game.selection.GameSelectionService.Mode;
import com.inari.firefly.state.StateChangeCondition;
import com.inari.firefly.state.Workflow;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFTimer;

public final class StartGameCondition implements StateChangeCondition {

    @Override
    public final boolean check( FFContext context, Workflow workflow, FFTimer timer ) {
        GameSelectionService selectionService = context.getComponent( GameSelectionService.CONTEXT_KEY );
        Mode mode = selectionService.getMode();
        return ( selectionService.isSelected() && ( mode == Mode.CAVE_SELECTION || mode == Mode.GAME_SELECTION ) );
    }

}
