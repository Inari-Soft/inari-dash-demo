package com.inari.dash.game.tasks;

import com.inari.commons.geom.Rectangle;
import com.inari.dash.Configuration;
import com.inari.dash.game.GameSystem;
import com.inari.dash.game.cave.CaveController;
import com.inari.dash.game.cave.CaveData;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.control.Controller;
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.controller.view.SimpleCameraController;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.filter.IColorFilter;
import com.inari.firefly.libgdx.GdxFirefly;
import com.inari.firefly.renderer.TextureAsset;
import com.inari.firefly.renderer.text.EText;
import com.inari.firefly.renderer.text.TextSystem;
import com.inari.firefly.renderer.tile.TileGrid;
import com.inari.firefly.renderer.tile.TileGrid.TileRenderMode;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.view.ViewSystem;
import com.inari.firefly.task.Task;

public final class LoadCave extends Task {

    protected LoadCave( int id ) {
        super( id );
    }

    @Override
    public final void run( FFContext context ) {
        CaveSystem caveSystem = context.getSystem( CaveSystem.CONTEXT_KEY );
        Configuration config = context.getComponent( Configuration.CONTEXT_KEY );
        AssetSystem assetSystem = context.getSystem( AssetSystem.CONTEXT_KEY );
        TextSystem textSystem = context.getSystem( TextSystem.CONTEXT_KEY );
        ControllerSystem controllerSystem = context.getSystem( ControllerSystem.CONTEXT_KEY );
        EntitySystem entitySystem = context.getSystem( EntitySystem.CONTEXT_KEY );
        ViewSystem viewSystem = context.getSystem( ViewSystem.CONTEXT_KEY );

        caveSystem.reset();
        CaveData caveData = caveSystem.getCaveData();
        caveData.resetPlayerData();

        // load unit texture asset with cave colors
        TextureAsset unitTextureAsset = assetSystem.getAsset( CaveSystem.GAME_UNIT_TEXTURE_KEY, TextureAsset.class );
        IColorFilter colorFilter = caveData.getColorFilter();
        context.addProperty( CaveSystem.COLOR_FILTER_KEY, colorFilter );
        unitTextureAsset.setDynamicAttribute( GdxFirefly.DynamicAttributes.TEXTURE_COLOR_FILTER_NAME, CaveSystem.COLOR_FILTER_KEY.id() );
        assetSystem.loadAsset( CaveSystem.GAME_UNIT_TEXTURE_KEY );

        // create tileGrid and cave entities
        int caveViewId = viewSystem.getViewId( CaveSystem.CAVE_VIEW_NAME );
        int caveWidth = caveData.getCaveWidth();
        int caveHeight = caveData.getCaveHeight();
        context.getComponentBuilder( TileGrid.TYPE_KEY )
            .set( TileGrid.NAME, CaveSystem.CAVE_TILE_GRID_NAME )
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
                UnitType unitType = CaveSystem.BDCFF_TYPES_MAP.get( type );
                if ( unitType != null ) {
                    unitType.handler.createOne( type, x, y );
                } else {
                    UnitType.SOLID_WALL.handler.createOne( x, y );
                }
                index++;
            }
        }
        
        SimpleCameraController cameraController = controllerSystem.getControllerAs( 
            CaveSystem.CAVE_CAMERA_CONTROLLER_NAME, 
            SimpleCameraController.class 
        );
        cameraController.setSnapToBounds( 
            new Rectangle( 
                0, 0, 
                config.unitWidth * caveData.getCaveWidth(), 
                config.unitHeight * caveData.getCaveHeight() 
            ) 
        );
        cameraController.getPivot().init( context );
        
        entitySystem.getEntityBuilderWithAutoActivation()
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveSystem.HEADER_VIEW_NAME ) )
            .set( ETransform.XPOSITION, 8 )
            .set( ETransform.YPOSITION, 8 )
            .set( EText.FONT_ID, textSystem.getFontId( GameSystem.GAME_FONT_TEXTURE_KEY.name ) )
            .set( EText.TEXT, caveSystem.getHeaderText() )
        .build();
        
        // create new CaveController
        context.getComponentBuilder( Controller.TYPE_KEY )
            .set( Controller.NAME, CaveSystem.CAVE_CONTROLLER_NAME )
        .build( CaveController.class );
    }
    
  

}
