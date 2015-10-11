package com.inari.dash.game.selection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.inari.dash.game.selection.GameSelectionService.Mode;
import com.inari.firefly.control.Controller;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFTimer;

public final class GameSelectionController extends Controller {

    private final GameSelectionService gameSelectionService;
    
    private int delay = 10;
    private int changed = 0;

    protected GameSelectionController( int id, FFContext context ) {
        super( id );
        gameSelectionService = context.getComponent( GameSelectionService.CONTEXT_KEY );
    }

    @Override
    public final void update( FFTimer timer ) {
        if ( changed > 0 ) {
            changed--;
            return;
        }
        
        if ( Gdx.input.isKeyPressed( Input.Keys.DOWN ) || Gdx.input.isKeyPressed( Input.Keys.S ) ) {
            gameSelectionService.nextSelectionMode();
        } else if ( Gdx.input.isKeyPressed( Input.Keys.UP ) || Gdx.input.isKeyPressed( Input.Keys.W ) ) {
            gameSelectionService.peviousSelectionMode();
        } else if ( Gdx.input.isKeyPressed( Input.Keys.RIGHT ) || Gdx.input.isKeyPressed( Input.Keys.D ) ) {
            if ( gameSelectionService.getMode() == Mode.GAME_SELECTION ) {
                gameSelectionService.nextGameSelection();
            } else if ( gameSelectionService.getMode() == Mode.CAVE_SELECTION ) {
                gameSelectionService.nextCaveSelection();
            }
        } else if ( Gdx.input.isKeyPressed( Input.Keys.LEFT ) || Gdx.input.isKeyPressed( Input.Keys.A ) ) {
            if ( gameSelectionService.getMode() == Mode.GAME_SELECTION ) {
                gameSelectionService.previousGameSelection();
            } else if ( gameSelectionService.getMode() == Mode.CAVE_SELECTION ) {
                gameSelectionService.previousCaveSelection();
            }
        } else if ( Gdx.input.isKeyPressed( Input.Keys.ENTER ) ) {
            gameSelectionService.select();
        }
        
        changed = delay;
        gameSelectionService.update();
    }

    @Override
    public final void dispose( FFContext context ) {
        
    }

}
