package com.inari.dash.game.cave.unit.misc;

import java.util.Map;

import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.AspectSetBuilder;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitHandle;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.FFInitException;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.graphics.sprite.SpriteAsset;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.graphics.tile.TileGrid;
import com.inari.firefly.graphics.tile.TileSystemEvent;
import com.inari.firefly.graphics.tile.TileSystemEvent.Type;
import com.inari.firefly.system.FFContext;

public final class Sand extends UnitHandle {
    
    public static final String SAND_NAME = "sand";

    private int sandEntityId;

    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );

        initialized = true;
    }

    @Override
    public void loadCaveData( FFContext context ) {
        super.loadCaveData( context );
        
        assetSystem.getAssetBuilder()
            .set( SpriteAsset.NAME, SAND_NAME )
            .set( SpriteAsset.TEXTURE_ASSET_ID, assetSystem.getAssetId( CaveSystem.GAME_UNIT_TEXTURE_NAME ) )
            .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 32, 7 * 32, 32, 32 ) )
        .activate( SpriteAsset.class );

        sandEntityId = entitySystem.getEntityBuilder()
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveSystem.CAVE_VIEW_NAME ) )
            .set( ETile.MULTI_POSITION, true )
            .set( ETile.SPRITE_ID, assetSystem.getAssetInstanceId( SAND_NAME ) )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.ASPECTS, AspectSetBuilder.create( 
                UnitAspect.DESTRUCTIBLE, 
                UnitAspect.CONSUMABLE, 
                UnitAspect.WALKABLE ) 
            )
        .activate();
    }

    @Override
    public void disposeCaveData( FFContext context ) {
        super.disposeCaveData( context );
        entitySystem.delete( sandEntityId );
        sandEntityId = -1;
        assetSystem.deleteAsset( SAND_NAME );
    }

    @Override
    public final void dispose( FFContext context ) {
        assetSystem.disposeAsset( SAND_NAME );
    }

    @Override
    public final UnitType type() {
        return UnitType.SAND;
    }

    @Override
    public void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( ".", type() );
    }

    @Override
    public final int createOne( int xGridPos, int yGridPos ) {
        context.notify( 
            new TileSystemEvent( 
                Type.MULTIPOSITION_ADD, 
                context.getSystemComponentId( TileGrid.TYPE_KEY, CaveSystem.CAVE_TILE_GRID_NAME ),
                sandEntityId,
                new Position( xGridPos, yGridPos )
            )
        );
        
        return sandEntityId;
    }

    @Override
    public int getEntityId() {
        return sandEntityId;
    }

}
