package com.inari.dash.game;

import java.util.Collection;

import com.inari.commons.graphics.RGBColor;
import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.dash.Configuration;
import com.inari.dash.game.io.GameInfos;
import com.inari.firefly.asset.AssetNameKey;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.renderer.text.EText;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;
import com.inari.firefly.system.FFSystem;

public final class GameSystem implements FFSystem {
    
    public static final FFSystemTypeKey<GameSystem> SYSTEM_KEY = FFSystemTypeKey.create( GameSystem.class );

    public static final String TITLE_SONG_SOUND_NAME = "titleSongSound";
    public static final String GAME_WORKFLOW_NAME = "gameWorkflow";
    public static final String GAME_SELECTION_CONTROLLER_NAME = "GAME_SELECTION_CONTROLLER";
    
    public static final String ENTITY_NAME_GAME_SELECTION_TITLE = "GAME_SELECTION_TITLE";
    public static final String ENTITY_NAME_GAME_SELECTION = "GAME_SELECTION";
    public static final String ENTITY_NAME_CAVE_SELECTION_TITLE = "CAVE_SELECTION_TITLE";
    public static final String ENTITY_NAME_CAVE_SELECTION = "CAVE_SELECTION";
    public static final String ENTITY_NAME_EXIT_TITLE = "EXIT_TITLE";

    public static final RGBColor YELLOW_FONT_COLOR = new RGBColor( .98f, .9f, .16f, 1f );
    public static final RGBColor WHITE_FONT_COLOR = new RGBColor( 1, 1, 1, 1 );

    public static final AssetNameKey GAME_FONT_TEXTURE_KEY = new AssetNameKey( "gameFontTexturKey", "gameFontTexturKey" );
    public static final AssetNameKey INTRO_SONG_KEY = new AssetNameKey( "sounds", "INTRO_SONG" );
    
    public static enum SelectionMode {
        GAME_SELECTION,
        CAVE_SELECTION,
        EXIT
    }
    
    private Configuration configuration;
    private EntitySystem entitySystem;
    private GameInfos gameInfos;
    
    private SelectionMode mode = SelectionMode.GAME_SELECTION;
    private int selectedGameIndex = 0;
    private int selectedCave = 0;
    private boolean selected = false;

    public int gameSelectionTitleId = -1;
    public int gameSelectionId = -1;
    public int caveSelectionTitleId = -1;
    public int caveSelectionId = -1;
    public int exitTitleId = -1;

    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return SYSTEM_KEY;
    }

    @Override
    public final FFSystemTypeKey<GameSystem> systemTypeKey() {
        return SYSTEM_KEY;
    }
    
    @Override
    public final void init( FFContext context ) throws FFInitException {
        entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        configuration = context.getComponent( Configuration.CONTEXT_KEY );
        
        gameInfos = new GameInfos();
        gameInfos.load( context );
    }

    @Override
    public final void dispose( FFContext context ) {

    }
    
    private void initEntityIds() {
        if ( gameSelectionTitleId == -1 ) {
            gameSelectionTitleId = entitySystem.getEntityId( ENTITY_NAME_GAME_SELECTION_TITLE );
            gameSelectionId = entitySystem.getEntityId( ENTITY_NAME_GAME_SELECTION );
            caveSelectionTitleId = entitySystem.getEntityId( ENTITY_NAME_CAVE_SELECTION_TITLE );
            caveSelectionId = entitySystem.getEntityId( ENTITY_NAME_CAVE_SELECTION );
            exitTitleId = entitySystem.getEntityId( ENTITY_NAME_EXIT_TITLE );
        }
    }

    public final Configuration getConfiguration() {
        return configuration;
    }
    
    public final Collection<GameInfo> getGameInfos() {
        return gameInfos.getGameInfos();
    }
    
    public final GameInfo getSelectedGame() {
        return gameInfos.getGameInfos().get( selectedGameIndex );
    }

    public final int getSelectedCave() {
        return selectedCave;
    }
    
    public final boolean isSelected() {
        return selected;
    }

    public final SelectionMode getMode() {
        return mode;
    }
    
    final void update() {
        initEntityIds();
        
        entitySystem.getComponent( gameSelectionTitleId, EText.class )
            .setTintColor( ( mode == SelectionMode.GAME_SELECTION )? GameSystem.YELLOW_FONT_COLOR : GameSystem.WHITE_FONT_COLOR );
        entitySystem.getComponent( caveSelectionTitleId, EText.class )
            .setTintColor( ( mode == SelectionMode.CAVE_SELECTION )? GameSystem.YELLOW_FONT_COLOR : GameSystem.WHITE_FONT_COLOR );
        entitySystem.getComponent( exitTitleId, EText.class )
            .setTintColor( ( mode == SelectionMode.EXIT )? GameSystem.YELLOW_FONT_COLOR : GameSystem.WHITE_FONT_COLOR );
        
        entitySystem.getComponent( gameSelectionId, EText.class )
            .setText( getSelectedGame().getName().toCharArray() );
        entitySystem.getComponent( caveSelectionId, EText.class )
            .setText( String.valueOf( getSelectedCave() ).toCharArray() );
    }

    final void select() {
        selected = true;
    }
    
    public final void resetSelection() {
        selected = false;
    }
    
    final void nextSelectionMode() {
        switch ( mode ) {
            case GAME_SELECTION : {
                mode = SelectionMode.CAVE_SELECTION;
                break;
            }
            case CAVE_SELECTION : {
                mode = SelectionMode.EXIT;
                break;
            }
            case EXIT : {
                mode = SelectionMode.GAME_SELECTION;
                break;
            }
        }
    }
    
    final void peviousSelectionMode() {
        switch ( mode ) {
            case GAME_SELECTION : {
                mode = SelectionMode.EXIT;
                break;
            }
            case CAVE_SELECTION : {
                mode = SelectionMode.GAME_SELECTION;
                break;
            }
            case EXIT : {
                mode = SelectionMode.CAVE_SELECTION;
                break;
            }
        }
    }
    
    final void nextGameSelection() {
        selectedGameIndex++;
        int size = getGameInfos().size();
        if ( selectedGameIndex >= size ) {
            selectedGameIndex = size - 1;
        }
        selectedCave = 0;
    }
    
    final void previousGameSelection() {
        selectedGameIndex--;
        if ( selectedGameIndex < 0 ) {
            selectedGameIndex = 0;
        }
        selectedCave = 0;
    }
    
    final void nextCaveSelection() {
        selectedCave++;
        if ( selectedCave >= getSelectedGame().getCaves() ) {
            selectedCave = getSelectedGame().getCaves() - 1;
        }
    }
    
    final void previousCaveSelection() {
        selectedCave--;
        if ( selectedCave < 0 ) {
            selectedCave = 0;
        }
    }

}
