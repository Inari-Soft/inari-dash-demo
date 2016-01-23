package com.inari.dash.game.cave.unit.misc;

import java.util.Map;

import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.AspectSetBuilder;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.FFInitException;
import com.inari.firefly.asset.AnimatedSpriteData;
import com.inari.firefly.asset.AnimatedTileAsset;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.prefab.EntityPrefab;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.system.FFContext;

public final class Explosion extends AbstractExplosionHandle {

    public static final String EXPLOSION_NAME = "explosion";
    
    private int prefabId;
    private int animationAssetId;

    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );
        initGeneralExplosion();

        initialized = true;
    }

    @Override
    public final void loadCaveData( FFContext context ) {
        super.loadCaveData( context );
        
        float updateRate = caveService.getUpdateRate();
        AnimatedSpriteData[] animationData = AnimatedSpriteData.create( 300 - (int) updateRate * 4, new Rectangle( 32, 0, 32, 32 ), 3, Direction.EAST );
        animationAssetId = assetSystem.getAssetBuilder()
            .set( AnimatedTileAsset.NAME, EXPLOSION_NAME )
            .set( AnimatedTileAsset.LOOPING, true )
            .set( AnimatedTileAsset.UPDATE_RESOLUTION, updateRate )
            .set( AnimatedTileAsset.TEXTURE_ASSET_ID, assetSystem.getAssetId( CaveSystem.GAME_UNIT_TEXTURE_NAME ) )
            .add( AnimatedTileAsset.ANIMATED_SPRITE_DATA, animationData )
        .activate( AnimatedTileAsset.class );
        int animatioControllerId = assetSystem.getAssetInstanceId( animationAssetId );
        
        prefabId = prefabSystem.getEntityPrefabBuilder()
            .add( EEntity.CONTROLLER_IDS, CONTROLLER_ID )
            .add( EEntity.CONTROLLER_IDS, animatioControllerId )
            .set( EntityPrefab.NAME, EXPLOSION_NAME )
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveSystem.CAVE_VIEW_NAME ) )
            .set( ETile.MULTI_POSITION, false )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.ASPECTS, AspectSetBuilder.create() )
        .build();
        prefabSystem.cacheComponents( prefabId, 100 );
    }

    @Override
    public void disposeCaveData( FFContext context ) {
        super.disposeCaveData( context );
        
        prefabSystem.deletePrefab( prefabId );
        assetSystem.deleteAsset( animationAssetId );
    }

    @Override
    public final UnitType type() {
        return UnitType.EXPLOSION;
    }

    @Override
    public final void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        // no
    }

    @Override
    public final int createOne( int xGridPos, int yGridPos ) {
        int entityId = prefabSystem.buildOne( prefabId );
        ETile tile = entitySystem.getComponent( entityId , ETile.TYPE_KEY );
        tile.setGridXPos( xGridPos );
        tile.setGridYPos( yGridPos );
        entitySystem.activateEntity( entityId );
        return entityId;
    }
    
    @Override
    public final void dispose( FFContext context ) {
        disposeGeneralExplosion();
    }

}
