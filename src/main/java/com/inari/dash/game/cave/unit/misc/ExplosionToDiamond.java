package com.inari.dash.game.cave.unit.misc;

import java.util.Map;

import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Rectangle;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.FFInitException;
import com.inari.firefly.asset.AnimatedSpriteAsset;
import com.inari.firefly.asset.AnimatedSpriteData;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityPrefab;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.system.FFContext;

public class ExplosionToDiamond extends AbstractExplosionHandle {
    
    public static final String EXPLOSION_NAME = "explosionToDiamond";
    
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
        AnimatedSpriteData[] animationData = AnimatedSpriteData.create( 200 - (int) updateRate * 4, new Rectangle( 2 * 32, 7 * 32, 32, 32 ), 6, Direction.EAST );
        animationAssetId = assetSystem.getAssetBuilder()
            .set( AnimatedSpriteAsset.NAME, EXPLOSION_NAME )
            .set( AnimatedSpriteAsset.LOOPING, true )
            .set( AnimatedSpriteAsset.UPDATE_RESOLUTION, updateRate )
            .set( AnimatedSpriteAsset.TEXTURE_ASSET_ID, assetSystem.getAssetId( CaveSystem.GAME_UNIT_TEXTURE_NAME ) )
            .set( AnimatedSpriteAsset.ANIMATED_SPRITE_DATA, animationData )
        .activate( AnimatedSpriteAsset.class );
        int animatioControllerId = assetSystem.getAssetInstaceId( animationAssetId );
        
        prefabId = prefabSystem.getEntityPrefabBuilder()
            .add( EEntity.CONTROLLER_IDS, CONTROLLER_ID )
            .add( EEntity.CONTROLLER_IDS, animatioControllerId )
            .set( EntityPrefab.NAME, EXPLOSION_NAME )
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveSystem.CAVE_VIEW_NAME ) )
            .set( ETransform.LAYER_ID, 0 )
            .set( ETile.MULTI_POSITION, false )
            .set( EUnit.UNIT_TYPE, type() )
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
    public UnitType type() {
        return UnitType.EXPLOSION_TO_DIAMOND;
    }

    @Override
    public void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        // no
    }

    @Override
    public int createOne( int xGridPos, int yGridPos ) {
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
