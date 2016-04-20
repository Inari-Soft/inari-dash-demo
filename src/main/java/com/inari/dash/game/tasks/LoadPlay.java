package com.inari.dash.game.tasks;

import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.graphics.RGBColor;
import com.inari.dash.Configuration;
import com.inari.dash.game.GameData;
import com.inari.dash.game.GameInfo;
import com.inari.dash.game.GameSystem;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.CaveSystem.CaveSoundKey;
import com.inari.dash.game.cave.CaveSystem.SoundChannel;
import com.inari.dash.game.cave.unit.Unit;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.dash.game.cave.unit.action.UnitActionType;
import com.inari.dash.game.io.BDCFFGameDataLoader;
import com.inari.dash.game.tasks.InitGameWorkflow.TaskName;
import com.inari.firefly.FFInitException;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.audio.AudioSystemEvent;
import com.inari.firefly.audio.Sound;
import com.inari.firefly.audio.SoundAsset;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.control.Controller;
import com.inari.firefly.control.action.Action;
import com.inari.firefly.control.task.Task;
import com.inari.firefly.control.task.TaskSystemEvent;
import com.inari.firefly.control.task.TaskSystemEvent.Type;
import com.inari.firefly.controller.view.CameraPivot;
import com.inari.firefly.controller.view.SimpleCameraController;
import com.inari.firefly.graphics.TextureAsset;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.graphics.view.View;
import com.inari.firefly.graphics.view.ViewSystem;
import com.inari.firefly.prototype.Prototype;
import com.inari.firefly.system.FFContext;

public final class LoadPlay extends Task {

    public LoadPlay( int id ) {
        super( id );
    }

    @Override
    public final void runTask() {
        GameSystem gameService = context.getSystem( GameSystem.SYSTEM_KEY );
        
        // stop playing title song
        context.notify( new AudioSystemEvent( GameSystem.TITLE_SONG_SOUND_NAME, AudioSystemEvent.Type.STOP_PLAYING ) );
        // dispose game selection screen
        context.notify( new TaskSystemEvent( Type.RUN_TASK, TaskName.DISPOSE_GAME_SELECTION.name() ) );
        // get selection data
        GameInfo selectedGame = gameService.getSelectedGame();
        int selectedCave = gameService.getSelectedCave();
        // load selected game data
        GameData gameData = ( new BDCFFGameDataLoader() ).load( selectedGame.getGameConfigResource() );
        gameData.setCave( selectedCave );
        context.setContextComponent( gameData );
        // create CaveService
        context.loadSystem( CaveSystem.SYSTEM_KEY );
        // load the selected cave
        initCaveAndUnits( context );

        context.notify( new TaskSystemEvent( Type.RUN_TASK, TaskName.LOAD_CAVE.name() ) );
    }
    
    private void initCaveAndUnits( FFContext context ) {
        Configuration config = context.getContextComponent( Configuration.COMPONENT_NAME );
        ViewSystem viewSystem = context.getSystem( ViewSystem.SYSTEM_KEY );
        AssetSystem assetSystem = context.getSystem( AssetSystem.SYSTEM_KEY );
        
        int screenWidth = context.getScreenWidth();
        int screenHeight = context.getScreenHeight();

        CameraPivot playerPivot = createPlayerPivot( config.unitWidth, config.unitHeight );
        context.getComponentBuilder( Controller.TYPE_KEY )
            .set( Controller.NAME, CaveSystem.CAVE_CAMERA_CONTROLLER_NAME )
            .set( Controller.UPDATE_RESOLUTION, 60 )
            .set( SimpleCameraController.PIVOT, playerPivot )
            .set( SimpleCameraController.H_ON_THRESHOLD, 150 )
            .set( SimpleCameraController.H_OFF_THRESHOLD, 250 )
            .set( SimpleCameraController.V_ON_THRESHOLD, 150 )
            .set( SimpleCameraController.V_OFF_THRESHOLD, 250 )
            .set( SimpleCameraController.H_VELOCITY, 3 )
            .set( SimpleCameraController.V_VELOCITY, 3 )
        .build( SimpleCameraController.class );
        
        viewSystem.getViewBuilder()
            .set( View.NAME, CaveSystem.HEADER_VIEW_NAME )
            .set( View.LAYERING_ENABLED, false )
            .set( View.BOUNDS, new Rectangle( 0, 0, screenWidth, CaveSystem.HEADER_VIEW_HEIGHT ) )
            .set( View.WORLD_POSITION, new Position( 0, 0 ) )
            .set( View.CLEAR_COLOR, new RGBColor( 0, 0, 0, 1 ) )
        .activate();
        viewSystem.getViewBuilder()
            .set( View.NAME, CaveSystem.CAVE_VIEW_NAME )
            .set( View.LAYERING_ENABLED, false )
            .set( View.BOUNDS, new Rectangle( 20, CaveSystem.HEADER_VIEW_HEIGHT, screenWidth - 40, screenHeight - CaveSystem.HEADER_VIEW_HEIGHT - 20 ) )
            .set( View.WORLD_POSITION, new Position( 0, 0 ) )
            .set( View.CONTROLLER_NAME, CaveSystem.CAVE_CAMERA_CONTROLLER_NAME ) 
            .set( View.CLEAR_COLOR, new RGBColor( 0, 0, 0, 1 ) )
        .activate();
        
        // create global cave assets and sounds
        context.getComponentBuilder( Asset.TYPE_KEY )
            .set( TextureAsset.NAME, CaveSystem.GAME_UNIT_TEXTURE_NAME )
            .set( TextureAsset.RESOURCE_NAME, config.unitTextureResource )
         .build( TextureAsset.class );
        
        for ( CaveSoundKey caveSoundKey : CaveSoundKey.values() ) {
            int soundAssetId = assetSystem.getAssetBuilder()
                .set( SoundAsset.NAME, caveSoundKey.name() )
                .set( SoundAsset.RESOURCE_NAME, caveSoundKey.fileName )
                .set( SoundAsset.STREAMING, false )
            .activate( SoundAsset.class );
            
            context.getComponentBuilder( Sound.TYPE_KEY )
                .set( Sound.NAME, caveSoundKey.name() )
                .set( Sound.SOUND_ASSET_ID, soundAssetId )
                .set( Sound.LOOPING, caveSoundKey.looping )
                .set( Sound.CHANNEL, SoundChannel.CAVE.ordinal() )
            .build();
        }
        
        // create unit actions
        for ( UnitActionType actionType : UnitActionType.values() ) {
            if ( actionType.getActionTypeClass() != null ) {
                context.getComponentBuilder( Action.TYPE_KEY )
                    .set( Action.NAME, actionType.name() )
                .build( actionType.index(), actionType.getActionTypeClass() );
            }
        }
        
        ComponentBuilder unitPrototypeBuilder = context.getComponentBuilder( Prototype.TYPE_KEY );
        // create and initialize all units
        for ( UnitType unitType : UnitType.values() ) {
            unitPrototypeBuilder
                .set( Prototype.NAME, unitType.name() )
            .buildAndNext( unitType.ordinal(), unitType.unitType );
            context
                .getSystemComponent( Prototype.TYPE_KEY, unitType.ordinal(), Unit.class )
                .initBDCFFTypesMap( CaveSystem.BDCFF_TYPES_MAP );
        }
    }

    public CameraPivot createPlayerPivot( final int unitWidth, final int unitHeight ) {
        final Position tmpPos = new Position();
        
        return new CameraPivot() {
            
            private ETile playerTile = null;
            private CaveSystem caveSystem;

            @Override
            public final void init( FFContext context ) throws FFInitException {
                caveSystem = context.getSystem( CaveSystem.SYSTEM_KEY );
                playerTile = context.getEntityComponent( caveSystem.getPrototype( UnitType.ROCKFORD ).getEntityId(), ETile.TYPE_KEY );
            }

            @Override
            public final Position getPivot() {
                if ( caveSystem.updateCamera() ) {
                    tmpPos.x = unitWidth * playerTile.getGridXPos();
                    tmpPos.y = unitHeight * playerTile.getGridYPos();
                }
                return tmpPos;
            }
        };
    } 

}
