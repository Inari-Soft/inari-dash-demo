package com.inari.dash.game.tasks;

import com.inari.commons.geom.PositionF;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.functional.IntFunction;
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
import com.inari.firefly.control.task.Task;
import com.inari.firefly.controller.view.BorderedCameraController;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.TextureAsset;
import com.inari.firefly.graphics.sprite.SpriteAsset;
import com.inari.firefly.graphics.text.EText;
import com.inari.firefly.graphics.tile.NormalFastTileGridRenderer;
import com.inari.firefly.graphics.tile.TileGrid;
import com.inari.firefly.graphics.tile.TileGridSystem;
import com.inari.firefly.graphics.view.View;
import com.inari.firefly.libgdx.GdxFireflyApp;
import com.inari.firefly.scene.Scene;

public final class LoadCave extends Task {

    protected LoadCave( int id ) {
        super( id );
    }

    @Override
    public final void runTask() {
        CaveSystem caveSystem = context.getSystem( CaveSystem.SYSTEM_KEY );
        Configuration config = context.getContextComponent( Configuration.COMPONENT_NAME );

        caveSystem.reset();
        CaveData caveData = caveSystem.getCaveData();
        caveData.resetPlayerData();

        // load unit texture asset with cave colors
        TextureAsset unitTextureAsset = context.getSystemComponent( Asset.TYPE_KEY, CaveSystem.GAME_UNIT_TEXTURE_NAME, TextureAsset.class );
        IntFunction colorFunction = caveData.getColorFunction();
        context.addProperty( CaveSystem.COLOR_CONVERTER_KEY, colorFunction );
        unitTextureAsset.setDynamicAttribute( GdxFireflyApp.DynamicAttributes.TEXTURE_COLOR_CONVERTER_NAME, CaveSystem.COLOR_CONVERTER_KEY.id() );
        context.getSystem( AssetSystem.SYSTEM_KEY ).loadAsset( CaveSystem.GAME_UNIT_TEXTURE_NAME );
        
        // create init scene assets
        AssetSystem assetSystem = context.getSystem( AssetSystem.SYSTEM_KEY );
        
        if ( !assetSystem.hasAsset( CaveSystem.INTRO_TILE_SPRITE_NAME + 0 ) ) {
            for ( int i = 0; i < 3; i++ ) {
                int offset = 4 * i;
                context.getComponentBuilder( Asset.TYPE_KEY )
                    .set( SpriteAsset.NAME, CaveSystem.INTRO_TILE_SPRITE_NAME + i )
                    .set( SpriteAsset.TEXTURE_ASSET_ID, unitTextureAsset.index() )
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
            caveSystem.getPrototype( unitType ).load( context );
        }
        
        String caveDataString = caveData.getCaveDataString();
        int index = 0;
        for ( int y = 0; y < caveHeight; y++ ) {
            for ( int x = 0; x < caveWidth; x++ ) {
                String type = String.valueOf( caveDataString.charAt( index ) );
                UnitType unitType = CaveSystem.BDCFF_TYPES_MAP.get( type );
                if ( unitType != null ) {
                    caveSystem.createOne( x, y, type, unitType );
                    //unitType.handler.createOne( type, x, y );
                } else {
                    caveSystem.createOne( x, y, UnitType.SOLID_WALL );
                    //UnitType.SOLID_WALL.handler.createOne( x, y );
                }
                index++;
            }
        }
        
        BorderedCameraController cameraController = context.getSystemComponent( 
            Controller.TYPE_KEY, 
            CaveSystem.CAVE_CAMERA_CONTROLLER_NAME, 
            BorderedCameraController.class 
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
            .set( EEntity.ENTITY_NAME, CaveSystem.HEADER_VIEW_NAME )
            .set( ETransform.VIEW_ID, context.getSystemComponentId( View.TYPE_KEY, CaveSystem.HEADER_VIEW_NAME ) )
            .set( ETransform.POSITION, new PositionF( 8, 8 ) )
            .set( EText.FONT_ASSET_NAME, GameSystem.GAME_FONT_TEXTURE_NAME )
            .set( EText.TEXT, "%%%%%%%%%%%%%%%%%%%%%%%%" )
        .activate();
        
        // create new CaveController
        context.getComponentBuilder( Controller.TYPE_KEY )
            .set( Controller.NAME, CaveSystem.CAVE_CONTROLLER_NAME )
        .build( CaveController.class );
        
        // load cave init scene
        context.getComponentBuilder( Scene.TYPE_KEY )
            .set( Scene.NAME, CaveSystem.CAVE_INIT_SCENE_NAME )
            .set( Scene.VIEW_NAME, CaveSystem.CAVE_VIEW_NAME )
            .set( Scene.RUN_ONCE, false )
            .set( Scene.UPDATE_RESOLUTION, 40 )
        .build( CaveInitScene.class );
    }

}
