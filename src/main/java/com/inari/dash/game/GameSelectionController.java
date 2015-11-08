package com.inari.dash.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.inari.dash.game.GameService.SelectionMode;
import com.inari.firefly.control.Controller;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFTimer;

public final class GameSelectionController extends Controller {

    private final GameService gameService;
    
    private final int delay = 5;
    private int changed = 0;

    protected GameSelectionController( int id, FFContext context ) {
        super( id );
        gameService = context.getComponent( GameService.CONTEXT_KEY );
    }

    @Override
    public final void update( FFTimer timer ) {
        if ( changed > 0 ) {
            changed--;
            return;
        }
        
        if ( Gdx.input.isKeyPressed( Input.Keys.DOWN ) || Gdx.input.isKeyPressed( Input.Keys.S ) ) {
            gameService.nextSelectionMode();
        } else if ( Gdx.input.isKeyPressed( Input.Keys.UP ) || Gdx.input.isKeyPressed( Input.Keys.W ) ) {
            gameService.peviousSelectionMode();
        } else if ( Gdx.input.isKeyPressed( Input.Keys.RIGHT ) || Gdx.input.isKeyPressed( Input.Keys.D ) ) {
            if ( gameService.getMode() == SelectionMode.GAME_SELECTION ) {
                gameService.nextGameSelection();
            } else if ( gameService.getMode() == SelectionMode.CAVE_SELECTION ) {
                gameService.nextCaveSelection();
            }
        } else if ( Gdx.input.isKeyPressed( Input.Keys.LEFT ) || Gdx.input.isKeyPressed( Input.Keys.A ) ) {
            if ( gameService.getMode() == SelectionMode.GAME_SELECTION ) {
                gameService.previousGameSelection();
            } else if ( gameService.getMode() == SelectionMode.CAVE_SELECTION ) {
                gameService.previousCaveSelection();
            }
        } else if ( Gdx.input.isKeyPressed( Input.Keys.ENTER ) ) {
            gameService.select();
        }
        
        changed = delay;
        gameService.update();
    }

    @Override
    public final void dispose( FFContext context ) {
        
    }

}
