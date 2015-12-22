package com.inari.dash.game;

import com.inari.dash.game.GameSystem.SelectionMode;
import com.inari.firefly.state.StateChangeCondition;
import com.inari.firefly.state.Workflow;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.external.FFTimer;

public final class StartGameCondition implements StateChangeCondition {

    @Override
    public final boolean check( FFContext context, Workflow workflow, FFTimer timer ) {
        GameSystem gameSystem = context.getSystem( GameSystem.SYSTEM_KEY );
        SelectionMode mode = gameSystem.getMode();
        return ( gameSystem.isSelected() && ( mode == SelectionMode.CAVE_SELECTION || mode == SelectionMode.GAME_SELECTION ) );
    }

}
