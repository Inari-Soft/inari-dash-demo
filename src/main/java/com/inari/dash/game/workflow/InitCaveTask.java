package com.inari.dash.game.workflow;

import com.inari.dash.game.GameData;
import com.inari.dash.game.GameInfo;
import com.inari.dash.game.GameService;
import com.inari.dash.game.cave.CaveService;
import com.inari.dash.game.io.BDCFFGameDataLoader;
import com.inari.dash.game.selection.GameSelectionService;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;

public final class InitCaveTask extends Task {
    
    public static final String NAME = "InitCaveTask";

    public InitCaveTask( int id ) {
        super( id );
    }

    @Override
    public final void run( FFContext context ) {
        GameService gameService = context.getComponent( GameService.CONTEXT_KEY );
        gameService.stopIntroSong();
        
        CaveService caveService = new CaveService();
        context.putComponent( CaveService.CONTEXT_KEY, caveService );
        caveService.init( context );
        
        GameSelectionService selection = context.getComponent( GameSelectionService.CONTEXT_KEY );
        GameInfo selectedGame = selection.getSelectedGame();
        int selectedCave = selection.getSelectedCave();
        GameData gameData = ( new BDCFFGameDataLoader() ).load( selectedGame.getGameConfigResource() );
        gameData.setCave( selectedCave );
        
        caveService.loadCave( context, gameData );
    }

}
