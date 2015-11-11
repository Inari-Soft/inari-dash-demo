package com.inari.dash.game;

import java.util.Collection;

import com.inari.commons.graphics.RGBColor;
import com.inari.commons.lang.TypedKey;
import com.inari.dash.Configuration;
import com.inari.dash.game.io.GameInfos;
import com.inari.dash.game.tasks.DisposeCave;
import com.inari.dash.game.tasks.DisposeGame;
import com.inari.dash.game.tasks.DisposeGameSelection;
import com.inari.dash.game.tasks.DisposePlay;
import com.inari.dash.game.tasks.LoadCave;
import com.inari.dash.game.tasks.LoadGame;
import com.inari.dash.game.tasks.LoadGameSelection;
import com.inari.dash.game.tasks.LoadPlay;
import com.inari.dash.game.tasks.NextCave;
import com.inari.dash.game.tasks.ReplayCave;
import com.inari.firefly.asset.AssetNameKey;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;
import com.inari.firefly.text.EText;

public final class GameService {
    
    public static final TypedKey<GameService> CONTEXT_KEY = TypedKey.create( "GameService", GameService.class );
    
    public enum TaskName {
        LOAD_GAME( LoadGame.class, true ),
        LOAD_GAME_SELECTION( LoadGameSelection.class ),
        LOAD_PLAY( LoadPlay.class ),
        LOAD_CAVE( LoadCave.class ),
        NEXT_CAVE( NextCave.class ),
        REPLAY_CAVE( ReplayCave.class ),
        DISPOSE_CAVE( DisposeCave.class ),
        DISPOSE_PLAY( DisposePlay.class ),
        DISPOSE_GAME_SELECTION( DisposeGameSelection.class ),
        DISPOSE_GAME( DisposeGame.class, true );
        
        public boolean removeAfterRun = false;
        public final Class<? extends Task> type;
        
        private TaskName( Class<? extends Task> type ) {
            this.type = type;
        }
        
        private TaskName( Class<? extends Task> type, boolean removeAfterRun ) {
            this.type = type;
            this.removeAfterRun = removeAfterRun;
        }
    }
    
    public enum StateName {
        GAME_SELECTION,
        CAVE_PLAY
    }
    
    public enum StateChangeName {
        GAME_INIT( null, StateName.GAME_SELECTION ),
        EXIT_GAME( StateName.GAME_SELECTION, null ),
        PLAY_CAVE( StateName.GAME_SELECTION, StateName.CAVE_PLAY ),
        EXIT_PLAY( StateName.CAVE_PLAY, StateName.GAME_SELECTION )
        ;
        public final StateName from;
        public final StateName to;
        private StateChangeName( StateName from, StateName to ) {
            this.from = from;
            this.to = to;
        }
    }
    
    
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
    
    public GameService( FFContext context, Configuration configuration, GameInfos gameInfos ) {
        entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );

        this.configuration = configuration;
        this.gameInfos = gameInfos;
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
            .setTintColor( ( mode == SelectionMode.GAME_SELECTION )? GameService.YELLOW_FONT_COLOR : GameService.WHITE_FONT_COLOR );
        entitySystem.getComponent( caveSelectionTitleId, EText.class )
            .setTintColor( ( mode == SelectionMode.CAVE_SELECTION )? GameService.YELLOW_FONT_COLOR : GameService.WHITE_FONT_COLOR );
        entitySystem.getComponent( exitTitleId, EText.class )
            .setTintColor( ( mode == SelectionMode.EXIT )? GameService.YELLOW_FONT_COLOR : GameService.WHITE_FONT_COLOR );
        
        entitySystem.getComponent( gameSelectionId, EText.class )
            .setText( getSelectedGame().getName().toCharArray() );
        entitySystem.getComponent( caveSelectionId, EText.class )
            .setText( String.valueOf( getSelectedCave() ).toCharArray() );
    }

    final void select() {
        selected = true;
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
