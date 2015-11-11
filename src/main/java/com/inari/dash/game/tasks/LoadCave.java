package com.inari.dash.game.tasks;

import com.inari.commons.geom.Rectangle;
import com.inari.dash.Configuration;
import com.inari.dash.game.GameService;
import com.inari.dash.game.cave.CaveController;
import com.inari.dash.game.cave.CaveData;
import com.inari.dash.game.cave.CaveService;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.control.Controller;
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.controller.view.SimpleCameraController;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.filter.IColorFilter;
import com.inari.firefly.libgdx.GDXConfiguration;
import com.inari.firefly.renderer.TextureAsset;
import com.inari.firefly.renderer.tile.TileGrid;
import com.inari.firefly.renderer.tile.TileGrid.TileRenderMode;
import com.inari.firefly.renderer.tile.TileGridSystem;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.view.ViewSystem;
import com.inari.firefly.task.Task;
import com.inari.firefly.text.EText;
import com.inari.firefly.text.TextSystem;

public final class LoadCave extends Task {

    protected LoadCave( int id ) {
        super( id );
    }

    @Override
    public final void run( FFContext context ) {
        CaveService caveService = context.getComponent( CaveService.CONTEXT_KEY );
        TileGridSystem tileGridSystem = context.getComponent( TileGridSystem.CONTEXT_KEY );
        GameService gameService = context.getComponent( GameService.CONTEXT_KEY );
        Configuration config = gameService.getConfiguration();
        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        TextSystem textSystem = context.getComponent( TextSystem.CONTEXT_KEY );
        ControllerSystem controllerSystem = context.getComponent( ControllerSystem.CONTEXT_KEY );
        EntitySystem entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );
        ViewSystem viewSystem = context.getComponent( ViewSystem.CONTEXT_KEY );
        int caveViewId = viewSystem.getViewId( CaveService.CAVE_VIEW_NAME );
        
        caveService.reset();
        
        CaveData caveData = caveService.getCaveData();

        // load unit texture asset with cave colors
        TextureAsset unitTextureAsset = assetSystem.getAsset( CaveService.GAME_UNIT_TEXTURE_KEY, TextureAsset.class );
        IColorFilter colorFilter = caveData.getColorFilter();
        context.putComponent( CaveService.COLOR_FILTER_KEY, colorFilter );
        unitTextureAsset.setDynamicAttribute( GDXConfiguration.DynamicAttributes.TEXTURE_COLOR_FILTER_NAME, CaveService.COLOR_FILTER_KEY.id() );
        assetSystem.loadAsset( CaveService.GAME_UNIT_TEXTURE_KEY );

        // create tileGrid and cave entities
        int caveWidth = caveData.getCaveWidth();
        int caveHeight = caveData.getCaveHeight();
        tileGridSystem.getTileGridBuilder()
            .set( TileGrid.NAME, CaveService.CAVE_TILE_GRID_NAME )
            .set( TileGrid.VIEW_ID, caveViewId )
            .set( TileGrid.WIDTH, caveWidth )
            .set( TileGrid.HEIGHT, caveHeight )
            .set( TileGrid.CELL_WIDTH, config.unitWidth )
            .set( TileGrid.CELL_HEIGHT, config.unitHeight )
            .set( TileGrid.RENDER_MODE, TileRenderMode.FAST_RENDERING )
        .build();
        
        // load all units
        for ( UnitType unitType : UnitType.values() ) {
            if ( unitType.handler != null ) {
                unitType.handler.loadCaveData( context );
            }
        }
        
        String caveDataString = caveData.getCaveDataString();
        int index = 0;
        for ( int y = 0; y < caveHeight; y++ ) {
            for ( int x = 0; x < caveWidth; x++ ) {
                String type = String.valueOf( caveDataString.charAt( index ) );
                UnitType unitType = CaveService.BDCFF_TYPES_MAP.get( type );
                if ( unitType != null ) {
                    unitType.handler.createOne( type, x, y );
                } else {
                    UnitType.SOLID_WALL.handler.createOne( x, y );
                }
                index++;
            }
        }
        
        SimpleCameraController cameraController = controllerSystem.getControllerAs( 
            CaveService.CAVE_CAMERA_CONTROLLER_NAME, 
            SimpleCameraController.class 
        );
        cameraController.setSnapToBounds( 
            new Rectangle( 
                0, 0, 
                gameService.getConfiguration().unitWidth * caveData.getCaveWidth(), 
                gameService.getConfiguration().unitHeight * caveData.getCaveHeight() 
            ) 
        );
        cameraController.getPivot().init( context );
        
        entitySystem.getEntityBuilderWithAutoActivation()
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveService.HEADER_VIEW_NAME ) )
            .set( ETransform.XPOSITION, 8 )
            .set( ETransform.YPOSITION, 8 )
            .set( EText.FONT_ID, textSystem.getFontId( GameService.GAME_FONT_TEXTURE_KEY.name ) )
            .set( EText.TEXT, caveService.getHeaderText() )
        .build();
        
        // create new CaveController
        controllerSystem.getControllerBuilder( CaveController.class )
            .set( Controller.NAME, CaveService.CAVE_CONTROLLER_NAME )
        .build();
    }
    
  

}
