package com.inari.dash.game.cave.unit.misc;

import java.util.Map;

import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.AspectSetBuilder;
import com.inari.dash.game.cave.CaveService;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitHandle;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.asset.AssetNameKey;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.sprite.SpriteAsset;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;

public final class Sand extends UnitHandle {
    
    public static final String SAND_NAME = "sand";
    public static final AssetNameKey SAND_SPRITE_ASSET_KEY = new AssetNameKey( CaveService.GAME_UNIT_TEXTURE_KEY.group, SAND_NAME );

    private int sandEntityId;

    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );

        assetSystem.getAssetBuilder( SpriteAsset.class )
            .set( SpriteAsset.NAME, SAND_SPRITE_ASSET_KEY.name )
            .set( SpriteAsset.ASSET_GROUP, SAND_SPRITE_ASSET_KEY.group )
            .set( SpriteAsset.TEXTURE_ID, assetSystem.getAssetId( CaveService.GAME_UNIT_TEXTURE_KEY ) )
            .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 32, 7 * 32, 32, 32 ) )
        .build();
        super.caveAssetsToReload.add( assetSystem.getAssetTypeKey( SAND_SPRITE_ASSET_KEY ) );
        
        initialized = true;
    }

    @Override
    public void loadCaveData( FFContext context ) {
        super.loadCaveData( context );

        sandEntityId = entitySystem.getEntityBuilderWithAutoActivation()
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveService.CAVE_VIEW_NAME ) )
            .set( ETile.MULTI_POSITION, true )
            .set( ESprite.SPRITE_ID, assetSystem.getAssetId( SAND_SPRITE_ASSET_KEY ) )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.ASPECTS, AspectSetBuilder.create( 
                UnitAspect.DESTRUCTIBLE, 
                UnitAspect.CONSUMABLE, 
                UnitAspect.WALKABLE ) 
            )
        .build().getId();
    }

    @Override
    public void disposeCaveData( FFContext context ) {
        super.disposeCaveData( context );
        entitySystem.delete( sandEntityId );
        sandEntityId = -1;
    }

    @Override
    public final void dispose( FFContext context ) {
        assetSystem.disposeAsset( SAND_SPRITE_ASSET_KEY );
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
        ETile tile = entitySystem.getComponent( sandEntityId, ETile.class );
        tile.getGridPositions().add( new Position( xGridPos, yGridPos ) );
        caveService.setEntityId( sandEntityId, xGridPos, yGridPos );
        return sandEntityId;
    }

    @Override
    public int getEntityId() {
        return sandEntityId;
    }

}
