package com.inari.dash.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.inari.dash.game.GameSystem.SelectionMode;
import com.inari.firefly.control.Controller;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFTimer;

public final class GameSelectionController extends Controller {

    private final GameSystem gameSystem;
    
    private final int delay = 10;
    private int changed = 0;

    protected GameSelectionController( int id, FFContext context ) {
        super( id );
        gameSystem = context.getSystem( GameSystem.CONTEXT_KEY );
    }

    @Override
    public final void update( FFTimer timer ) {
        if ( changed > 0 ) {
            changed--;
            return;
        }
        
        if ( Gdx.input.isKeyPressed( Input.Keys.DOWN ) || Gdx.input.isKeyPressed( Input.Keys.S ) ) {
            gameSystem.nextSelectionMode();
        } else if ( Gdx.input.isKeyPressed( Input.Keys.UP ) || Gdx.input.isKeyPressed( Input.Keys.W ) ) {
            gameSystem.peviousSelectionMode();
        } else if ( Gdx.input.isKeyPressed( Input.Keys.RIGHT ) || Gdx.input.isKeyPressed( Input.Keys.D ) ) {
            if ( gameSystem.getMode() == SelectionMode.GAME_SELECTION ) {
                gameSystem.nextGameSelection();
            } else if ( gameSystem.getMode() == SelectionMode.CAVE_SELECTION ) {
                gameSystem.nextCaveSelection();
            }
        } else if ( Gdx.input.isKeyPressed( Input.Keys.LEFT ) || Gdx.input.isKeyPressed( Input.Keys.A ) ) {
            if ( gameSystem.getMode() == SelectionMode.GAME_SELECTION ) {
                gameSystem.previousGameSelection();
            } else if ( gameSystem.getMode() == SelectionMode.CAVE_SELECTION ) {
                gameSystem.previousCaveSelection();
            }
        } else if ( Gdx.input.isKeyPressed( Input.Keys.ENTER ) ) {
            gameSystem.select();
        }
        
        changed = delay;
        gameSystem.update();
    }

    @Override
    public final void dispose( FFContext context ) {
        
    }

}
