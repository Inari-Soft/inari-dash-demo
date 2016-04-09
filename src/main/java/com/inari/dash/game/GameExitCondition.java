package com.inari.dash.game;

import com.inari.dash.game.GameSystem.SelectionMode;
import com.inari.firefly.system.Condition;
import com.inari.firefly.system.FFContext;

public final class GameExitCondition extends Condition {

    @Override
    public final boolean check( FFContext context ) {
        GameSystem selectionService = context.getSystem( GameSystem.SYSTEM_KEY );
        return ( selectionService.isSelected() && selectionService.getMode() == SelectionMode.EXIT );
    }

}
