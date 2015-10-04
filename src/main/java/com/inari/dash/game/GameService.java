package com.inari.dash.game;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.TypedKey;
import com.inari.commons.lang.list.DynArray;
import com.inari.dash.GlobalData;
import com.inari.dash.game.state.InitGameTask;
import com.inari.dash.game.unit.IUnitType;
import com.inari.dash.game.unit.UnitHandle;
import com.inari.firefly.app.FFApplicationManager;
import com.inari.firefly.asset.AssetNameKey;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.renderer.TextureAsset;
import com.inari.firefly.renderer.sprite.SpriteAsset;
import com.inari.firefly.sound.Sound;
import com.inari.firefly.sound.SoundAsset;
import com.inari.firefly.sound.SoundSystem;
import com.inari.firefly.sound.event.SoundEvent;
import com.inari.firefly.state.State;
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
    
    public static final String GAME_WORKFLOW_NAME = "gameWorkflow";
    public static final String GAME_SELECTION_STATE_NAME = "gameSelection";
    public static final String PLAY_CAVE_STATE_NAME = "playCave";
    public static final String GAME_OVER_STATE = "gameOver";
    public static final String SELECT_GAME_STATE_CHANGE_NAME = "selectGame";
    public static final String NEXT_CAVE_STATE_CHANGE_NAME = "nextCave";
    public static final String GAME_OVER_STATE_CHANGE = "showGameOver";
    public static final String BACK_TO_GAME_SELECTION = "backToGameSelection";
    
    
    
    public static final String HEADER_VIEW_NAME = "HeaderView";
    public static final String GAME_VIEW_NAME = "GameView";

    public static final AssetNameKey ORIGINAL_FONT_TEXTURE_KEY = new AssetNameKey( "originalFont", "fontTexture" );
    public static final AssetNameKey GAME_UNIT_TEXTURE_KEY = new AssetNameKey( "gameUnitTexturKey", "gameUnitTexturKey" );
    public static final AssetNameKey GAME_FONT_TEXTURE_KEY = new AssetNameKey( "gameFontTexturKey", "gameFontTexturKey" );
    public static final AssetNameKey INTRO_SONG_KEY = new AssetNameKey( "sounds", "INTRO_SONG" );
    
    private FFContext context;
    private GlobalData globalData;
    private AssetSystem assetSystem;
    private TextSystem textSystem;
    private SoundSystem soundSystem;
    private IEventDispatcher eventDispatcher;
    private StateSystem stateSystem;
    private TaskSystem taskSystem;

    private final GameSelection gameSelection;
    private final DynArray<UnitHandle> unitHandler;

    
    public GameService() {
        unitHandler = new DynArray<UnitHandle>();
        gameSelection = new GameSelection();
    }
    
    @Override
    public void init( FFContext context ) throws FFInitException {
        this.context = context;
        globalData = new GlobalData();
        assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        textSystem = context.getComponent( TextSystem.CONTEXT_KEY );
        soundSystem = context.getComponent( SoundSystem.CONTEXT_KEY );
        eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
        stateSystem = context.getComponent( StateSystem.CONTEXT_KEY );
        taskSystem = context.getComponent( TaskSystem.CONTEXT_KEY );
        context.putComponent( CONTEXT_KEY, this );
        
        
        Task initTask = taskSystem.getTaskBuilder( InitGameTask.class )
            .set( Task.REMOVE_AFTER_RUN, true )
            .build();
        
        Workflow gameWorkflow = stateSystem.getWorkflowBuilder()
            .set( Workflow.NAME, GAME_WORKFLOW_NAME )
            .set( Workflow.START_STATE_NAME, GAME_SELECTION_STATE_NAME )
            .set( Workflow.INIT_TASK_ID, initTask.getId() )
        .build();
        
        stateSystem.getStateBuilder()
            .set( State.NAME, GAME_SELECTION_STATE_NAME )
            .set( State.WORKFLOW_ID, gameWorkflow.getId() )
        .build();
        
        stateSystem.activateWorkflow( gameWorkflow.getId() );
    }
    
    @Override
    public final void handlePause( FFContext context ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public final void handleResume( FFContext context ) {
        // TODO Auto-generated method stub
        
    }
    
    public final void loadGlobalAssets() {
        createAndLoadOriginalFont();
        // load intro music
        assetSystem.getAssetBuilder( SoundAsset.class )
            .set( SoundAsset.NAME, INTRO_SONG_KEY.name )
            .set( SoundAsset.ASSET_GROUP, INTRO_SONG_KEY.group )
            .set( SoundAsset.STREAMING, true )
            .set( SoundAsset.RESOURCE_NAME, globalData.titleSongResource )
        .build();
    }
    


    public final void loadGameSelection() {
        gameSelection.load( context );
    }
    
    public final void disposeGameSelection() {
        gameSelection.dispose( context );
    }
    
    private final String titleSongSoundName = "titleSongSound";
    public final void playIntroSong() {
        assetSystem.loadAsset( INTRO_SONG_KEY );
        Sound introSong = soundSystem.getSoundBuilder()
            .set( Sound.ASSET_ID, assetSystem.getAssetTypeKey( INTRO_SONG_KEY ).id )
            .set( Sound.VOLUME, 100 )
            .set( Sound.LOOPING, true )
            .set( Sound.NAME, titleSongSoundName )
        .build();
        eventDispatcher.notify( new SoundEvent( introSong.index(), SoundEvent.Type.PLAY_SOUND ) );
    }
    
    public final void disposeGlobalAssets() {
        // TODO Auto-generated method stub
        
    }

    public final UnitHandle getUnitHandle( IUnitType type ) {
        return unitHandler.get( type.type() );
    }

    @Override
    public void dispose( FFContext context ) {
        // TODO Auto-generated method stub
        
    }
    
    
    private void createAndLoadOriginalFont() {
        assetSystem.getAssetBuilderWithAutoLoad( TextureAsset.class )
            .set( TextureAsset.NAME, ORIGINAL_FONT_TEXTURE_KEY.name )
            .set( TextureAsset.ASSET_GROUP, ORIGINAL_FONT_TEXTURE_KEY.group )
            .set( TextureAsset.RESOURCE_NAME, globalData.fontTextureResource )
            .set( TextureAsset.TEXTURE_WIDTH, globalData.fontTextureWidth )
            .set( TextureAsset.TEXTURE_HEIGHT, globalData.fontTextureHeight )
        .build();

    
        Font font = textSystem.getFontBuilder()
            .set( Font.NAME, ORIGINAL_FONT_TEXTURE_KEY.name )
            .set( Font.CHAR_WIDTH, globalData.charWidth )
            .set( Font.CHAR_HEIGHT, globalData.charHeight )
            .set( Font.CHAR_SPACE, 5 )
            .set( Font.LINE_SPACE, 5 )
        .build();
    
        int textureAssetId = assetSystem.getAssetTypeKey( ORIGINAL_FONT_TEXTURE_KEY ).id;
        Rectangle textureRegion = new Rectangle( 0, 0, globalData.charWidth, globalData.charHeight );
        for ( int y = 0; y < globalData.fontChars.length; y++ ) {
            for ( int x = 0; x < globalData.fontChars[ y ].length; x++ ) {
                textureRegion.x = x * globalData.charWidth;
                textureRegion.y = y * globalData.charHeight;
                
                SpriteAsset charSpriteAsset = assetSystem.getAssetBuilderWithAutoLoad( SpriteAsset.class )
                    .set( SpriteAsset.TEXTURE_ID, textureAssetId )
                    .set( SpriteAsset.TEXTURE_REGION, textureRegion )
                    .set( SpriteAsset.ASSET_GROUP, ORIGINAL_FONT_TEXTURE_KEY.group )
                    .set( SpriteAsset.NAME, ORIGINAL_FONT_TEXTURE_KEY.name + "_" + x + "_"+ y )
                .build();
                
                font.setCharSpriteMapping( globalData.fontChars[ y ][ x ], charSpriteAsset.getId() );
            }
        }
    }


}
