package com.inari.dash.game;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.graphics.RGBColor;
import com.inari.commons.lang.TypedKey;
import com.inari.dash.Configuration;
import com.inari.dash.game.workflow.ExitGameTask;
import com.inari.dash.game.workflow.GameExitCondition;
import com.inari.dash.game.workflow.InitCaveTask;
import com.inari.dash.game.workflow.InitGameTask;
import com.inari.dash.game.workflow.StartGameCondition;
import com.inari.firefly.app.FFApplicationManager;
import com.inari.firefly.asset.AssetNameKey;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.sound.Sound;
import com.inari.firefly.sound.SoundAsset;
import com.inari.firefly.sound.SoundSystem;
import com.inari.firefly.sound.event.SoundEvent;
import com.inari.firefly.state.State;
import com.inari.firefly.state.StateChange;
import com.inari.firefly.state.StateSystem;
import com.inari.firefly.state.Workflow;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;
import com.inari.firefly.task.Task;
import com.inari.firefly.task.TaskSystem;
import com.inari.firefly.text.Font;
import com.inari.firefly.text.TextSystem;

public final class GameService implements FFApplicationManager {
    
    public static final TypedKey<GameService> CONTEXT_KEY = TypedKey.create( "GameService", GameService.class );
    
    public static final RGBColor YELLOW_FONT_COLOR = new RGBColor( .98f, .9f, .16f, 1f );
    public static final RGBColor WHITE_FONT_COLOR = new RGBColor( 1, 1, 1, 1 );
    
    public static final String GAME_WORKFLOW_NAME = "gameWorkflow";
    public enum StateName {
        GAME_SELECTION,
        CAVE_PLAY,
        GAME_OVER
    }
    
    public enum StateChangeName {
        GAME_INIT( null, StateName.GAME_SELECTION ),
        EXIT_GAME( StateName.GAME_SELECTION, null ),
        PLAY_CAVE( StateName.GAME_SELECTION, StateName.CAVE_PLAY ),
        EXIT_PLAY( StateName.CAVE_PLAY, StateName.GAME_SELECTION ),
        DIED( StateName.CAVE_PLAY, StateName.CAVE_PLAY ),
        NEXT_CAVE( StateName.CAVE_PLAY, StateName.CAVE_PLAY ),
        GAME_OVER( StateName.CAVE_PLAY, StateName.GAME_OVER ),
        GAME_OVER_TO_SELECTION( StateName.GAME_OVER, StateName.GAME_SELECTION ),
        
        ;
        public final StateName from;
        public final StateName to;
        private StateChangeName( StateName from, StateName to ) {
            this.from = from;
            this.to = to;
        }
    }

    public static final AssetNameKey GAME_FONT_TEXTURE_KEY = new AssetNameKey( "gameFontTexturKey", "gameFontTexturKey" );
    public static final AssetNameKey INTRO_SONG_KEY = new AssetNameKey( "sounds", "INTRO_SONG" );
    
    private Configuration configuration;
    private AssetSystem assetSystem;
    private TextSystem textSystem;
    private SoundSystem soundSystem;
    private IEventDispatcher eventDispatcher;
    private StateSystem stateSystem;
    private TaskSystem taskSystem;

    
    @Override
    public void init( FFContext context ) throws FFInitException {
        configuration = new Configuration();
        assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        textSystem = context.getComponent( TextSystem.CONTEXT_KEY );
        soundSystem = context.getComponent( SoundSystem.CONTEXT_KEY );
        eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
        stateSystem = context.getComponent( StateSystem.CONTEXT_KEY );
        taskSystem = context.getComponent( TaskSystem.CONTEXT_KEY );
        context.putComponent( CONTEXT_KEY, this );
        
        
        taskSystem.getTaskBuilder( InitGameTask.class )
            .set( Task.NAME, InitGameTask.NAME )
            .set( Task.REMOVE_AFTER_RUN, true )
        .buildAndNext( ExitGameTask.class )
            .set( Task.NAME, ExitGameTask.NAME )
            .set( Task.REMOVE_AFTER_RUN, true )
        .buildAndNext( InitCaveTask.class )
            .set( Task.NAME, InitCaveTask.NAME )
            .set( Task.REMOVE_AFTER_RUN, false )
        .build();
        
        int workflowId = stateSystem.getWorkflowBuilder()
            .set( Workflow.NAME, GAME_WORKFLOW_NAME )
            .set( Workflow.START_STATE_NAME, StateName.GAME_SELECTION.name() )
            .set( Workflow.INIT_TASK_ID, taskSystem.getTaskId( InitGameTask.NAME ) )
        .build().getId();
        
        stateSystem.getStateBuilder()
            .set( State.NAME, StateName.GAME_SELECTION.name() )
            .set( State.WORKFLOW_ID, workflowId )
        .buildAndNext()
            .set( State.NAME, StateName.CAVE_PLAY.name() )
            .set( State.WORKFLOW_ID, workflowId )
        .buildAndNext()
            .set( State.NAME, StateName.GAME_OVER.name() )
            .set( State.WORKFLOW_ID, workflowId )
        .build();
        
        stateSystem.getStateChangeBuilder()
            .set( StateChange.NAME, StateChangeName.EXIT_GAME.name() )
            .set( StateChange.WORKFLOW_ID, workflowId )
            .set( StateChange.CONDITION_TYPE_NAME, GameExitCondition.class.getName() )
            .set( StateChange.FORM_STATE_ID, stateSystem.getStateId( StateName.GAME_SELECTION.name() ) )
            .set( StateChange.TASK_ID, taskSystem.getTaskId( ExitGameTask.NAME ) )
        .buildAndNext()
            .set( StateChange.NAME, StateChangeName.PLAY_CAVE.name() )
            .set( StateChange.WORKFLOW_ID, workflowId )
            .set( StateChange.CONDITION_TYPE_NAME, StartGameCondition.class.getName() )
            .set( StateChange.FORM_STATE_ID, stateSystem.getStateId( StateName.GAME_SELECTION.name() ) )
            .set( StateChange.TO_STATE_ID, stateSystem.getStateId( StateName.CAVE_PLAY.name() ) )
            .set( StateChange.TASK_ID, taskSystem.getTaskId( InitCaveTask.NAME ) )
        .build();
            
        
        stateSystem.activateWorkflow( workflowId );
    }
    
    @Override
    public final void handlePause( FFContext context ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public final void handleResume( FFContext context ) {
        // TODO Auto-generated method stub
        
    }
    
    public final Configuration getConfiguration() {
        return configuration;
    }
    
    public final void loadGlobalAssets() {
        // create and load font
        textSystem.getFontBuilderWithAutoLoad()
            .set( Font.NAME, GAME_FONT_TEXTURE_KEY.name )
            .set( Font.FONT_TEXTURE_RESOURCE_NAME, configuration.fontTextureResource )
            .set( Font.CHAR_TEXTURE_MAP, configuration.fontChars )
            .set( Font.CHAR_WIDTH, configuration.charWidth )
            .set( Font.CHAR_HEIGHT, configuration.charHeight )
            .set( Font.CHAR_SPACE, 0 )
            .set( Font.LINE_SPACE, 5 )
            .set( Font.DEFAULT_CHAR, '%' )
        .build();
        // create and load intro music
        assetSystem.getAssetBuilder( SoundAsset.class )
            .set( SoundAsset.NAME, INTRO_SONG_KEY.name )
            .set( SoundAsset.ASSET_GROUP, INTRO_SONG_KEY.group )
            .set( SoundAsset.STREAMING, true )
            .set( SoundAsset.RESOURCE_NAME, configuration.titleSongResource )
        .build();
        assetSystem.loadAsset( INTRO_SONG_KEY );
    }

    public final void playIntroSong() {
        Sound introSong = soundSystem.getSoundBuilder()
            .set( Sound.ASSET_ID, assetSystem.getAssetTypeKey( INTRO_SONG_KEY ).id )
            .set( Sound.VOLUME, 10 )
            .set( Sound.LOOPING, true )
            .set( Sound.NAME, "titleSongSound" )
        .build();
        eventDispatcher.notify( new SoundEvent( introSong.index(), SoundEvent.Type.PLAY_SOUND ) );
    }
    
    public final void stopIntroSong() {
        eventDispatcher.notify( new SoundEvent( soundSystem.getSound( "titleSongSound" ).getId(), SoundEvent.Type.STOP_PLAYING ) );
    }

    @Override
    public void dispose( FFContext context ) {
        configuration = null;
        textSystem.clear();
        taskSystem.clear();
        stateSystem.clear();
        soundSystem.clear();
        assetSystem.clear();
    }

}
