package com.inari.dash.game.cave.unit.wall;

import java.util.Map;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.AspectSetBuilder;
import com.inari.dash.game.cave.CaveService;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitHandle;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.asset.AssetNameKey;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.Entity;
import com.inari.firefly.entity.EntityPrefab;
import com.inari.firefly.entity.EntityPrefabSystem;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.sprite.SpriteAsset;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;
import com.inari.firefly.system.view.ViewSystem;

public final class SolidWallHandle extends UnitHandle {
    
    public static final AssetNameKey SOLID_WALL_SPRITE_ASSET_KEY = new AssetNameKey( "units", "solidWall" );
    public static final String SOLID_WALL_PREFAB_NAME = "SOLID_WALL_PREFAB";
    
    private EntitySystem entitySystem;
    private EntityPrefabSystem prefabSystem;
    private int prefabId;

    @Override
    public final void init( FFContext context ) throws FFInitException {
        entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );
        prefabSystem = context.getComponent( EntityPrefabSystem.CONTEXT_KEY );
        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        ViewSystem viewSystem = context.getComponent( ViewSystem.CONTEXT_KEY );
        
        super.caveAssetsToReload.add( SOLID_WALL_SPRITE_ASSET_KEY );
        assetSystem.getAssetBuilder( SpriteAsset.class )
            .set( SpriteAsset.NAME, SOLID_WALL_SPRITE_ASSET_KEY.name )
            .set( SpriteAsset.ASSET_GROUP, SOLID_WALL_SPRITE_ASSET_KEY.group )
            .set( SpriteAsset.TEXTURE_ID, assetSystem.getAssetId( CaveService.GAME_UNIT_TEXTURE_KEY ) )
            .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 32, 6 * 32, 32, 32 ) )
        .build();
        
        prefabId = prefabSystem.getEntityPrefabBuilder()
            .set( EntityPrefab.NAME, SOLID_WALL_PREFAB_NAME )
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveService.CAVE_VIEW_NAME ) )
            .set( ETransform.LAYER_ID, 0 )
            .set( ETile.GRID_X_POSITION, 0 )
            .set( ETile.GRID_Y_POSITION, 0 )
            .set( ESprite.SPRITE_ID, assetSystem.getAssetId( SOLID_WALL_SPRITE_ASSET_KEY ) )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.ASPECTS, AspectSetBuilder.create( UnitAspect.MASSIVE ) )
        .build().getId();
        prefabSystem.cacheComponents( prefabId, 100 );
        
        initialized = true;
    }

    @Override
    public final void dispose( FFContext context ) {
        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        
        prefabSystem.deletePrefab( SOLID_WALL_PREFAB_NAME );
        assetSystem.disposeAsset( SOLID_WALL_SPRITE_ASSET_KEY );
    }

    @Override
    public final UnitType type() {
        return UnitType.SOLID_WALL;
    }

    @Override
    public final void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( "W", type() );
    }

    @Override
    public final void createOne( int xGridPos, int yGridPos ) {
        Entity entity = prefabSystem.buildOne( prefabId );
        ETile tile = entitySystem.getComponent(  entity.getId(), ETile.class );
        tile.setGridXPos( xGridPos );
        tile.setGridYPos( yGridPos );
        entitySystem.activate( entity.getId() );
    }

}
