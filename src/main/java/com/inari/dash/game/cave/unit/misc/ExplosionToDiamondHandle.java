package com.inari.dash.game.cave.unit.misc;

import java.util.Collection;
import java.util.Map;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.AspectSetBuilder;
import com.inari.dash.game.cave.CaveService;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.animation.sprite.SpriteAnimationBuilder;
import com.inari.firefly.animation.sprite.SpriteAnimationBuilder.SpriteAnimationHandler;
import com.inari.firefly.asset.AssetTypeKey;
import com.inari.firefly.control.EController;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.Entity;
import com.inari.firefly.entity.EntityPrefab;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;

public class ExplosionToDiamondHandle extends AbstractExplosionHandle {
    
    public static final String EXPLOSION_NAME = "explosionToDiamond";
    
    private int prefabId;
    private SpriteAnimationHandler spriteAnimationHandler;

    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );
        initGeneralExplosion();
        
        spriteAnimationHandler = new SpriteAnimationBuilder( context )
            .setGroup( CaveService.GAME_UNIT_TEXTURE_KEY.group )
            .setLooping( true )
            .setNamePrefix( EXPLOSION_NAME )
            .setTextureAssetKey( CaveService.GAME_UNIT_TEXTURE_KEY )
            .addSpritesToAnimation( 0, new Rectangle( 2 * 32, 7 * 32, 32, 32 ), 6, true )
        .build();
        
        Collection<AssetTypeKey> allSpriteAssetKeys = spriteAnimationHandler.getAllSpriteAssetKeys();
        caveAssetsToReload.addAll( allSpriteAssetKeys );
    
        prefabId = prefabSystem.getEntityPrefabBuilder()
            .set( EController.CONTROLLER_IDS, new int[] { CONTROLLER_ID, spriteAnimationHandler.getControllerId() } )
            .set( EntityPrefab.NAME, EXPLOSION_NAME )
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveService.CAVE_VIEW_NAME ) )
            .set( ETransform.LAYER_ID, 0 )
            .set( ESprite.SPRITE_ID, allSpriteAssetKeys.iterator().next().id )
            .set( ETile.MULTI_POSITION, false )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.ASPECTS, AspectSetBuilder.create( UnitAspect.MASSIVE ) )
        .build().getId();
        prefabSystem.cacheComponents( prefabId, 100 );
        
        initialized = true;
    }
    
    @Override
    public final void loadCaveData( FFContext context ) {
        super.loadCaveData( context );
        float updateRate = caveService.getUpdateRate();
        spriteAnimationHandler.setFrameTime( 200 - (int) updateRate * 4 );
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
        Entity entity = prefabSystem.buildOne( prefabId );
        int entityId = entity.getId();
        ETile tile = entitySystem.getComponent( entityId , ETile.class );
        tile.setGridXPos( xGridPos );
        tile.setGridYPos( yGridPos );
        entitySystem.activate( entityId );
        return entityId;
    }

    @Override
    public final void dispose( FFContext context ) {
        spriteAnimationHandler.dispose( context );
        prefabSystem.deletePrefab( prefabId );
        disposeGeneralExplosion();
    }

}
