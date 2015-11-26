package com.inari.dash.game;

import com.inari.dash.game.GameSystem.SelectionMode;
import com.inari.firefly.state.StateChangeCondition;
import com.inari.firefly.state.Workflow;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFTimer;

public final class GameExitCondition implements StateChangeCondition {

    @Override
    public final boolean check( FFContext context, Workflow workflow, FFTimer timer ) {
        GameSystem selectionService = context.getSystem( GameSystem.CONTEXT_KEY );
        return ( selectionService.isSelected() && selectionService.getMode() == SelectionMode.EXIT );
    }

}
