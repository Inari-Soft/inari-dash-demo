package com.inari.dash.game.tasks;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.convert.IntValueConverter;
import com.inari.dash.Configuration;
import com.inari.dash.game.GameSystem;
import com.inari.dash.game.cave.CaveController;
import com.inari.dash.game.cave.CaveData;
import com.inari.dash.game.cave.CaveInitScene;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.control.Controller;
import com.inari.firefly.controller.view.SimpleCameraController;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.TextureAsset;
import com.inari.firefly.graphics.sprite.SpriteAsset;
import com.inari.firefly.graphics.text.EText;
import com.inari.firefly.graphics.tile.NormalFastTileGridRenderer;
import com.inari.firefly.graphics.tile.TileGrid;
import com.inari.firefly.graphics.tile.TileGridSystem;
import com.inari.firefly.libgdx.GdxFirefly;
import com.inari.firefly.scene.Scene;
import com.inari.firefly.system.view.View;
import com.inari.firefly.system.view.ViewSystem;
import com.inari.firefly.task.Task;

public final class LoadCave extends Task {

    protected LoadCave( int id ) {
        super( id );
    }

    @Override
    public final void runTask() {
        CaveSystem caveSystem = context.getSystem( CaveSystem.SYSTEM_KEY );
        Configuration config = context.getContextComponent( Configuration.CONTEXT_KEY );

        caveSystem.reset();
        CaveData caveData = caveSystem.getCaveData();
        caveData.resetPlayerData();

        // load unit texture asset with cave colors
        TextureAsset unitTextureAsset = context.getSystemComponent( Asset.TYPE_KEY, CaveSystem.GAME_UNIT_TEXTURE_NAME, TextureAsset.class );
        IntValueConverter colorFilter = caveData.getColorConverter();
        context.addProperty( CaveSystem.COLOR_CONVERTER_KEY, colorFilter );
        unitTextureAsset.setDynamicAttribute( GdxFirefly.DynamicAttributes.TEXTURE_COLOR_CONVERTER_NAME, CaveSystem.COLOR_CONVERTER_KEY.id() );
        context.getSystem( AssetSystem.SYSTEM_KEY ).loadAsset( CaveSystem.GAME_UNIT_TEXTURE_NAME );
        
        // create init scene assets
        AssetSystem assetSystem = context.getSystem( AssetSystem.SYSTEM_KEY );
        
        if ( !assetSystem.hasAsset( CaveSystem.INTRO_TILE_SPRITE_NAME + 0 ) ) {
            for ( int i = 0; i < 3; i++ ) {
                int offset = 4 * i;
                context.getComponentBuilder( Asset.TYPE_KEY )
                    .set( SpriteAsset.NAME, CaveSystem.INTRO_TILE_SPRITE_NAME + i )
                    .set( SpriteAsset.TEXTURE_ASSET_ID, unitTextureAsset.getId() )
                    .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 32 + offset, ( 6 * 32 ) + offset, 16, 16 ) )
                .build( SpriteAsset.class );
            }
        }

        // create tileGrid and cave entities
        int caveViewId = context.getSystemComponentId( View.TYPE_KEY, CaveSystem.CAVE_VIEW_NAME );
        int caveWidth = caveData.getCaveWidth();
        int caveHeight = caveData.getCaveHeight();
        context.getComponentBuilder( TileGrid.TYPE_KEY )
            .set( TileGrid.NAME, CaveSystem.CAVE_TILE_GRID_NAME )
            .set( TileGrid.VIEW_ID, caveViewId )
            .set( TileGrid.WIDTH, caveWidth )
            .set( TileGrid.HEIGHT, caveHeight )
            .set( TileGrid.CELL_WIDTH, config.unitWidth )
            .set( TileGrid.CELL_HEIGHT, config.unitHeight )
            .set( TileGrid.RENDERER_ID, context.getSystem( TileGridSystem.SYSTEM_KEY ).getRendererId( NormalFastTileGridRenderer.NAME ) )
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
        
        SimpleCameraController cameraController = context.getSystemComponent( 
            Controller.TYPE_KEY, 
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
        
        // create header text entity
        context.getComponentBuilder( EntitySystem.Entity.ENTITY_TYPE_KEY )
            .set( ETransform.VIEW_ID, context.getSystemComponentId( View.TYPE_KEY, CaveSystem.HEADER_VIEW_NAME ) )
            .set( ETransform.XPOSITION, 8 )
            .set( ETransform.YPOSITION, 8 )
            .set( EText.FONT_ID, context.getSystem( AssetSystem.SYSTEM_KEY ).getAssetId( GameSystem.GAME_FONT_TEXTURE_NAME ) )
            .set( EText.TEXT, caveSystem.getHeaderText() )
        .activate();
        
        // create new CaveController
        context.getComponentBuilder( Controller.TYPE_KEY )
            .set( Controller.NAME, CaveSystem.CAVE_CONTROLLER_NAME )
        .build( CaveController.class );
        
        // load cave init scene
        context.getComponentBuilder( Scene.TYPE_KEY )
            .set( Scene.NAME, CaveSystem.CAVE_INIT_SCENE_NAME )
            .set( Scene.VIEW_ID, context.getSystem( ViewSystem.SYSTEM_KEY ).getViewId( CaveSystem.CAVE_VIEW_NAME ) )
            .set( Scene.RUN_ONCE, false )
            .set( Scene.UPDATE_RESOLUTION, 40 )
        .build( CaveInitScene.class );
    }

}
