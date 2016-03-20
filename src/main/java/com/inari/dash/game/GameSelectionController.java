package com.inari.dash.game;

import com.inari.dash.game.GameSystem.SelectionMode;
import com.inari.firefly.FFInitException;
import com.inari.firefly.control.Controller;
import com.inari.firefly.system.external.FFInput;
import com.inari.firefly.system.external.FFInput.ButtonType;
import com.inari.firefly.system.external.FFTimer;

public final class GameSelectionController extends Controller {

    private GameSystem gameSystem;
    private FFInput input;

    protected GameSelectionController( int id ) {
        super( id );
    }

    @Override
    public final void init() throws FFInitException {
        super.init();
        
        gameSystem = context.getSystem( GameSystem.SYSTEM_KEY );
        input = context.getInput();
    }

    @Override
    public final void update( FFTimer timer ) {
        if ( input.isPressed( ButtonType.DOWN ) ) {
            gameSystem.nextSelectionMode();
        } else if ( input.isPressed( ButtonType.UP ) ) {
            gameSystem.peviousSelectionMode();
        } else if ( input.isPressed( ButtonType.RIGHT ) ) {
            if ( gameSystem.getMode() == SelectionMode.GAME_SELECTION ) {
                gameSystem.nextGameSelection();
            } else if ( gameSystem.getMode() == SelectionMode.CAVE_SELECTION ) {
                gameSystem.nextCaveSelection();
            }
        } else if ( input.isPressed( ButtonType.LEFT ) ) {
            if ( gameSystem.getMode() == SelectionMode.GAME_SELECTION ) {
                gameSystem.previousGameSelection();
            } else if ( gameSystem.getMode() == SelectionMode.CAVE_SELECTION ) {
                gameSystem.previousCaveSelection();
            }
        } else if ( input.isPressed( ButtonType.ENTER ) ) {
            gameSystem.select();
        }
        
        gameSystem.update();
    }

}
