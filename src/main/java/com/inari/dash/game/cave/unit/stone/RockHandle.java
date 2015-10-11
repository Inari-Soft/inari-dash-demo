package com.inari.dash.game.cave.unit.stone;

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
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.control.EController;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.Entity;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.entity.EntityPrefab;
import com.inari.firefly.entity.EntityPrefabSystem;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.sprite.SpriteAsset;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.sound.Sound;
import com.inari.firefly.sound.SoundAsset;
import com.inari.firefly.sound.SoundSystem;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;
import com.inari.firefly.system.view.ViewSystem;

public final class RockHandle extends UnitHandle {
    
    public static final AssetNameKey ROCK_SPRITE_ASSET_KEY = new AssetNameKey( "units", "rock" );
    public static final AssetNameKey ROCK_SOUND_ASSEET_KEY = new AssetNameKey( "sounds", "rock" );
    public static final String ROCK_PREFAB_NAME = "ROCK_PREFAB";
    
    
    private EntitySystem entitySystem;
    private EntityPrefabSystem prefabSystem;
    private int prefabId;

    @Override
    public final void init( FFContext context ) throws FFInitException {
        entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );
        prefabSystem = context.getComponent( EntityPrefabSystem.CONTEXT_KEY );
        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        ViewSystem viewSystem = context.getComponent( ViewSystem.CONTEXT_KEY );
        SoundSystem soundSystem = context.getComponent( SoundSystem.CONTEXT_KEY );
        ControllerSystem controllerSystem = context.getComponent( ControllerSystem.CONTEXT_KEY );
        
        super.caveAssetsToReload.add( ROCK_SPRITE_ASSET_KEY );
        assetSystem.getAssetBuilder( SpriteAsset.class )
            .set( SpriteAsset.NAME, ROCK_SPRITE_ASSET_KEY.name )
            .set( SpriteAsset.ASSET_GROUP, ROCK_SPRITE_ASSET_KEY.group )
            .set( SpriteAsset.TEXTURE_ID, assetSystem.getAssetId( CaveService.GAME_UNIT_TEXTURE_KEY ) )
            .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 0, 7 * 32, 32, 32 ) )
        .build();
        assetSystem.getAssetBuilderWithAutoLoad( SoundAsset.class )
            .set( SoundAsset.NAME, ROCK_SOUND_ASSEET_KEY.name )
            .set( SoundAsset.ASSET_GROUP, ROCK_SOUND_ASSEET_KEY.group )
            .set( SoundAsset.RESOURCE_NAME, "original/sound/stone.wav" )
            .set( SoundAsset.STREAMING, false )
        .build();
        
        soundSystem.getSoundBuilder()
            .set( Sound.NAME, ROCK_SOUND_ASSEET_KEY.name )
            .set( Sound.ASSET_ID, assetSystem.getAssetId( ROCK_SOUND_ASSEET_KEY ) )
            .set( Sound.VOLUME, 10 )
            .set( Sound.LOOPING, false )
        .build();
        
        int stoneControllerId = controllerSystem.getComponentBuilder( RockController.class )
            .set( EntityController.NAME, "RockController" )
        .build().getId();
        
        prefabId = prefabSystem.getEntityPrefabBuilder()
            .set( EController.CONTROLLER_IDS, new int[] { stoneControllerId } )
            .set( EntityPrefab.NAME, ROCK_PREFAB_NAME )
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveService.CAVE_VIEW_NAME ) )
            .set( ETransform.LAYER_ID, 0 )
            .set( ETile.GRID_X_POSITION, 0 )
            .set( ETile.GRID_Y_POSITION, 0 )
            .set( ESprite.SPRITE_ID, assetSystem.getAssetId( ROCK_SPRITE_ASSET_KEY ) )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.ASPECTS, AspectSetBuilder.create( UnitAspect.DESTRUCTIBLE, UnitAspect.STONE, UnitAspect.ASLOPE, UnitAspect.MASSIVE ) )
        .build().getId();
        prefabSystem.cacheComponents( prefabId, 200 );
        
        initialized = true;
    }

    @Override
    public final void dispose( FFContext context ) {
        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        
        prefabSystem.deletePrefab( ROCK_PREFAB_NAME );
        assetSystem.disposeAsset( ROCK_SPRITE_ASSET_KEY );
    }

    @Override
    public final UnitType type() {
        return UnitType.ROCK;
    }

    @Override
    public final void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( "r", type() );
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
