package com.inari.dash.game.tasks;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.graphics.RGBColor;
import com.inari.dash.Configuration;
import com.inari.dash.game.GameData;
import com.inari.dash.game.GameInfo;
import com.inari.dash.game.GameService;
import com.inari.dash.game.GameService.TaskName;
import com.inari.dash.game.cave.CaveService;
import com.inari.dash.game.cave.CaveService.CaveSoundKey;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.dash.game.cave.unit.action.UnitActionType;
import com.inari.dash.game.io.BDCFFGameDataLoader;
import com.inari.firefly.action.Action;
import com.inari.firefly.action.ActionSystem;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.control.Controller;
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.controller.view.CameraPivot;
import com.inari.firefly.controller.view.SimpleCameraController;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.renderer.TextureAsset;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.sound.Sound;
import com.inari.firefly.sound.SoundAsset;
import com.inari.firefly.sound.SoundSystem;
import com.inari.firefly.sound.event.SoundEvent;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;
import com.inari.firefly.system.LowerSystemFacade;
import com.inari.firefly.system.view.View;
import com.inari.firefly.system.view.ViewSystem;
import com.inari.firefly.task.Task;
import com.inari.firefly.task.event.TaskEvent;
import com.inari.firefly.task.event.TaskEvent.Type;

public final class LoadPlay extends Task {

    public LoadPlay( int id ) {
        super( id );
    }

    @Override
    public final void run( FFContext context ) {
        GameService gameService = context.getComponent( GameService.CONTEXT_KEY );
        IEventDispatcher eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
        
        // stop playing title song
        eventDispatcher.notify( new SoundEvent( GameService.TITLE_SONG_SOUND_NAME, SoundEvent.Type.STOP_PLAYING ) );
        // dispose game selection screen
        eventDispatcher.notify( new TaskEvent( Type.RUN_TASK, TaskName.DISPOSE_GAME_SELECTION.name() ) );
        // get selection data
        GameInfo selectedGame = gameService.getSelectedGame();
        int selectedCave = gameService.getSelectedCave();
        // load selected game data
        GameData gameData = ( new BDCFFGameDataLoader() ).load( selectedGame.getGameConfigResource() );
        gameData.setCave( selectedCave );
        context.putComponent( GameData.CONTEXT_KEY, gameData );
        // create CaveService
        CaveService caveService = new CaveService( context );
        context.putComponent( CaveService.CONTEXT_KEY, caveService );
        // load the selected cave
        initCaveAndUnits( context );
        eventDispatcher.notify( new TaskEvent( Type.RUN_TASK, TaskName.LOAD_CAVE.name() ) );
    }
    
    private void initCaveAndUnits( FFContext context ) {
        GameService gameService = context.getComponent( GameService.CONTEXT_KEY );
        Configuration config = gameService.getConfiguration();
        LowerSystemFacade lowerSystemFacade = context.getComponent( FFContext.LOWER_SYSTEM_FACADE );
        ViewSystem viewSystem = context.getComponent( ViewSystem.CONTEXT_KEY );
        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        ControllerSystem controllerSystem = context.getComponent( ControllerSystem.CONTEXT_KEY );
        ActionSystem actionSystem = context.getComponent( ActionSystem.CONTEXT_KEY );
        SoundSystem soundSystem = context.getComponent( SoundSystem.CONTEXT_KEY );
        
        int screenWidth = lowerSystemFacade.getScreenWidth();
        int screenHeight = lowerSystemFacade.getScreenHeight();

        CameraPivot playerPivot = createPlayerPivot( config.unitWidth, config.unitHeight );
        Controller cameraController = controllerSystem.getControllerBuilder( SimpleCameraController.class )
            .set( Controller.NAME, CaveService.CAVE_CAMERA_CONTROLLER_NAME )
            .set( Controller.UPDATE_RESOLUTION, 60 )
            .set( SimpleCameraController.PIVOT, playerPivot )
            .set( SimpleCameraController.H_ON_THRESHOLD, 150 )
            .set( SimpleCameraController.H_OFF_THRESHOLD, 250 )
            .set( SimpleCameraController.V_ON_THRESHOLD, 150 )
            .set( SimpleCameraController.V_OFF_THRESHOLD, 250 )
            .set( SimpleCameraController.H_VELOCITY, 3 )
            .set( SimpleCameraController.V_VELOCITY, 3 )
        .build();
        
        viewSystem.getViewBuilderWithAutoActivation()
            .set( View.NAME, CaveService.HEADER_VIEW_NAME )
            .set( View.LAYERING_ENABLED, false )
            .set( View.BOUNDS, new Rectangle( 0, 0, screenWidth, CaveService.HEADER_VIEW_HEIGHT ) )
            .set( View.WORLD_POSITION, new Position( 0, 0 ) )
            .set( View.CLEAR_COLOR, new RGBColor( 0, 0, 0, 1 ) )
        .build().getId();
        viewSystem.getViewBuilderWithAutoActivation()
            .set( View.NAME, CaveService.CAVE_VIEW_NAME )
            .set( View.LAYERING_ENABLED, false )
            .set( View.BOUNDS, new Rectangle( 20, CaveService.HEADER_VIEW_HEIGHT, screenWidth - 40, screenHeight - CaveService.HEADER_VIEW_HEIGHT - 20 ) )
            .set( View.WORLD_POSITION, new Position( 0, 0 ) )
            .set( View.CONTROLLER_IDS, new int[]{ cameraController.getId() } ) 
            .set( View.CLEAR_COLOR, new RGBColor( 0, 0, 0, 1 ) )
        .build().getId();
        
        // create global cave assets and sounds
        assetSystem.getAssetBuilder( TextureAsset.class )
            .set( TextureAsset.NAME, CaveService.GAME_UNIT_TEXTURE_KEY.name )
            .set( TextureAsset.ASSET_GROUP, CaveService.GAME_UNIT_TEXTURE_KEY.group )
            .set( TextureAsset.RESOURCE_NAME, config.unitTextureResource )
            .set( TextureAsset.TEXTURE_WIDTH, config.unitTextureWidth )
            .set( TextureAsset.TEXTURE_HEIGHT, config.unitTextureHeight )
         .build();
        
        for ( CaveSoundKey caveSoundKey : CaveSoundKey.values() ) {
            assetSystem.getAssetBuilderWithAutoLoad( SoundAsset.class )
                .set( SoundAsset.NAME, caveSoundKey.assetKey.name )
                .set( SoundAsset.ASSET_GROUP, caveSoundKey.assetKey.group )
                .set( SoundAsset.RESOURCE_NAME, caveSoundKey.fileName )
                .set( SoundAsset.STREAMING, false )
            .build( caveSoundKey.id );
            soundSystem.getSoundBuilder()
                .set( Sound.NAME, caveSoundKey.assetKey.name )
                .set( Sound.ASSET_ID, caveSoundKey.id )
                .set( Sound.LOOPING, caveSoundKey.looping )
                .set( Sound.CHANNEL, 4 )
            .build( caveSoundKey.id );
        }
        
        // create unit actions
        for ( UnitActionType actionType : UnitActionType.values() ) {
            if ( actionType.getActionTypeClass() != null ) {
                actionSystem.getActionBuilder( actionType.getActionTypeClass() )
                    .set( Action.NAME, actionType.name() )
                .build( actionType.index() );
            }
        }
        
        // create and initialize all units
        for ( UnitType unitType : UnitType.values() ) {
            if ( unitType.handler != null ) {
                unitType.handler.init( context );
                unitType.handler.initBDCFFTypesMap( CaveService.BDCFF_TYPES_MAP );
            }
        }
    }

    public CameraPivot createPlayerPivot( final int unitWidth, final int unitHeight ) {
        final Position tmpPos = new Position();
        
        return new CameraPivot() {
            
            private ETile playerTile = null;

            @Override
            public final void init( FFContext context ) throws FFInitException {
                EntitySystem entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );
                playerTile = entitySystem.getComponent( UnitType.ROCKFORD.getHandle().getEntityId(), ETile.class );
            }

            @Override
            public final Position getPivot() {
                if ( playerTile != null ) {
                    tmpPos.x = unitWidth * playerTile.getGridXPos();
                    tmpPos.y = unitHeight * playerTile.getGridYPos();
                    return tmpPos;
                }
                return null;
            }
        };
    } 

}
