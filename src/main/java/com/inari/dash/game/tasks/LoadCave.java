package com.inari.dash.game.tasks;

import com.inari.commons.geom.Rectangle;
import com.inari.dash.Configuration;
import com.inari.dash.game.GameService;
import com.inari.dash.game.cave.CaveController;
import com.inari.dash.game.cave.CaveService;
import com.inari.dash.game.cave.CaveService.AmoebaData;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.control.Controller;
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.filter.IColorFilter;
import com.inari.firefly.libgdx.GDXConfiguration;
import com.inari.firefly.renderer.TextureAsset;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.renderer.tile.TileGrid;
import com.inari.firefly.renderer.tile.TileGrid.TileRenderMode;
import com.inari.firefly.renderer.tile.TileGridSystem;
import com.inari.firefly.system.FFContext;
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
        
        caveService.reset();

        TileGridSystem tileGridSystem = context.getComponent( TileGridSystem.CONTEXT_KEY );
        GameService gameService = context.getComponent( GameService.CONTEXT_KEY );
        Configuration config = gameService.getConfiguration();
        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        TextSystem textSystem = context.getComponent( TextSystem.CONTEXT_KEY );
        ControllerSystem controllerSystem = context.getComponent( ControllerSystem.CONTEXT_KEY );
        
        caveData = gameData.getCurrentCave();
        amoebaData = new AmoebaData( caveData );

        // load unit texture asset with cave colors
        TextureAsset unitTextureAsset = assetSystem.getAsset( GAME_UNIT_TEXTURE_KEY, TextureAsset.class );
        IColorFilter colorFilter = caveData.getColorFilter();
        context.putComponent( COLOR_FILTER_KEY, colorFilter );
        unitTextureAsset.setDynamicAttribute( GDXConfiguration.DynamicAttributes.TEXTURE_COLOR_FILTER_NAME, COLOR_FILTER_KEY.id() );
        assetSystem.loadAsset( GAME_UNIT_TEXTURE_KEY );

        // create tileGrid and cave entities
        int caveWidth = caveData.getCaveWidth();
        int caveHeight = caveData.getCaveHeight();
        tileGrid = tileGridSystem.getTileGridBuilder()
            .set( TileGrid.NAME, CAVE_TILE_GRID_NAME )
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
                UnitType unitType = BDCFF_TYPES_MAP.get( type );
                if ( unitType != null ) {
                    int entityId = unitType.handler.createOne( type, x, y );
                    if ( unitType == UnitType.ROCKFORD ) {
                        ETile playerTile = entitySystem.getComponent( entityId, ETile.class );
                        EUnit playerUnit = entitySystem.getComponent( entityId, EUnit.class );
                        playerPivot.setPlayerData( playerTile, playerUnit );
                    }
                } else {
                    UnitType.SOLID_WALL.handler.createOne( x, y );
                }
                index++;
            }
        }
        
        cameraController.setSnapToBounds( 
            new Rectangle( 
                0, 0, 
                tileGrid.getCellWidth() * caveData.getCaveWidth(), 
                tileGrid.getCellHeight() * caveData.getCaveHeight() 
            ) 
        );
        
        entitySystem.getEntityBuilderWithAutoActivation()
            .set( ETransform.VIEW_ID, headerViewId )
            .set( ETransform.XPOSITION, 8 )
            .set( ETransform.YPOSITION, 8 )
            .set( EText.FONT_ID, textSystem.getFontId( GameService.GAME_FONT_TEXTURE_KEY.name ) )
            .set( EText.TEXT, headerText )
        .build();
        
        controllerSystem.getControllerBuilder( CaveController.class )
            .set( Controller.NAME, CAVE_CONTROLLER_NAME )
        .build();
    }
    
  

}
