package com.inari.dash.game;

import com.inari.dash.game.GameSystem.SelectionMode;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.utils.Condition;

public final class StartGameCondition extends Condition {

    @Override
    public final boolean check( FFContext context ) {
        GameSystem gameSystem = context.getSystem( GameSystem.SYSTEM_KEY );
        SelectionMode mode = gameSystem.getMode();
        return ( gameSystem.isSelected() && ( mode == SelectionMode.CAVE_SELECTION || mode == SelectionMode.GAME_SELECTION ) );
    }

}
