package com.inari.dash.game.cave.unit.wall;

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

public class BrickWallHandle extends UnitHandle {
    
    public static final String BRICK_WALL_NAME = "brickWall";
    public static final AssetNameKey BRICK_WALL_SPRITE_ASSET_KEY = new AssetNameKey( CaveService.GAME_UNIT_TEXTURE_KEY.group, BRICK_WALL_NAME );
    

    private int brickWallEntityId;

    
    @Override
    public void init( FFContext context ) throws FFInitException {
        super.init( context );
        
        assetSystem.getAssetBuilder( SpriteAsset.class )
            .set( SpriteAsset.NAME, BRICK_WALL_SPRITE_ASSET_KEY.name )
            .set( SpriteAsset.ASSET_GROUP, BRICK_WALL_SPRITE_ASSET_KEY.group )
            .set( SpriteAsset.TEXTURE_ID, assetSystem.getAssetId( CaveService.GAME_UNIT_TEXTURE_KEY ) )
            .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 3 * 32, 6 * 32, 32, 32 ) )
        .build();
        super.caveAssetsToReload.add( assetSystem.getAssetTypeKey( BRICK_WALL_SPRITE_ASSET_KEY ) );

        initialized = true;
    }
    
    @Override
    public void loadCaveData( FFContext context ) {
        super.loadCaveData( context );
        
        brickWallEntityId = entitySystem.getEntityBuilderWithAutoActivation()
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveService.CAVE_VIEW_NAME ) )
            .set( ETile.MULTI_POSITION, true )
            .set( ESprite.SPRITE_ID, assetSystem.getAssetId( BRICK_WALL_SPRITE_ASSET_KEY ) )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.ASPECTS, AspectSetBuilder.create( 
                UnitAspect.ASLOPE, 
                UnitAspect.DESTRUCTIBLE
            ) )
        .build().getId();
    }

    @Override
    public void disposeCaveData( FFContext context ) {
        super.disposeCaveData( context );
        entitySystem.delete( brickWallEntityId );
        brickWallEntityId = -1;
    }

    @Override
    public UnitType type() {
        return UnitType.BRICK_WALL;
    }

    @Override
    public void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( "w", type() );
    }

    @Override
    public int createOne( int xGridPos, int yGridPos ) {
        ETile tile = entitySystem.getComponent( brickWallEntityId, ETile.class );
        tile.getGridPositions().add( new Position( xGridPos, yGridPos ) );
        caveService.setEntityId( brickWallEntityId, xGridPos, yGridPos );
        return brickWallEntityId;
    }
    
    @Override
    public final void dispose( FFContext context ) {
        assetSystem.disposeAsset( BRICK_WALL_SPRITE_ASSET_KEY );
    }

}
