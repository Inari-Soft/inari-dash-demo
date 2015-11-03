package com.inari.dash.game.cave.unit.wall;

import java.util.Collection;
import java.util.Map;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.AspectSetBuilder;
import com.inari.dash.game.cave.CaveService;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitHandle;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.dash.game.cave.unit.wall.MagicWallAnimationController.State;
import com.inari.firefly.animation.AnimationSystem;
import com.inari.firefly.animation.sprite.SpriteAnimationBuilder;
import com.inari.firefly.animation.sprite.SpriteAnimationBuilder.SpriteAnimationHandler;
import com.inari.firefly.asset.AssetNameKey;
import com.inari.firefly.asset.AssetTypeKey;
import com.inari.firefly.control.EController;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.Entity;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.entity.EntityPrefab;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.sound.Sound;
import com.inari.firefly.sound.SoundAsset;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;

public final class MagicWall extends UnitHandle {
    
    public static final String MAGIC_WALL_NAME = "magicWall";
    public static final AssetNameKey MAGIC_WALL_SOUND_ASSEET_KEY = new AssetNameKey( CaveService.CAVE_SOUND_GROUP_NAME, MAGIC_WALL_NAME );

    private SpriteAnimationHandler spriteAnimationHandler;
    private int prefabId;
    private int controllerId;
    private int soundId;
    private int firstSpriteId;
    

    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );

        assetSystem.getAssetBuilderWithAutoLoad( SoundAsset.class )
            .set( SoundAsset.NAME, MAGIC_WALL_SOUND_ASSEET_KEY.name )
            .set( SoundAsset.ASSET_GROUP, MAGIC_WALL_SOUND_ASSEET_KEY.group )
            .set( SoundAsset.RESOURCE_NAME, "original/sound/" + MAGIC_WALL_NAME + ".wav" )
            .set( SoundAsset.STREAMING, false )
        .build();
        
        soundId = soundSystem.getSoundBuilder()
            .set( Sound.NAME, MAGIC_WALL_SOUND_ASSEET_KEY.name )
            .set( Sound.ASSET_ID, assetSystem.getAssetId( MAGIC_WALL_SOUND_ASSEET_KEY ) )
            .set( Sound.CHANNEL, 2 )
            .set( Sound.LOOPING, true )
        .build().getId();
        
        spriteAnimationHandler = new SpriteAnimationBuilder( context )
            .setGroup( CaveService.GAME_UNIT_TEXTURE_KEY.group )
            .setLooping( true )
            .setNamePrefix( MAGIC_WALL_NAME )
            .setTextureAssetKey( CaveService.GAME_UNIT_TEXTURE_KEY )
            .setStatedAnimationType( MagicWallAnimationController.class )
            .setState( State.INACTIVE.ordinal() )
            .addSpritesToAnimation( 0, new Rectangle( 3 * 32, 6 * 32, 32, 32 ), 1, true )
            .setState( State.ACTIVE.ordinal() )
            .addSpritesToAnimation( 0, new Rectangle( 4 * 32, 6 * 32, 32, 32 ), 4, true )
        .build();
        
        Collection<AssetTypeKey> allSpriteAssetKeys = spriteAnimationHandler.getAllSpriteAssetKeys();
        caveAssetsToReload.addAll( allSpriteAssetKeys );
        firstSpriteId = allSpriteAssetKeys.iterator().next().id;
    }

    @Override
    public final void loadCaveData( FFContext context ) {
        super.loadCaveData( context );
        
        MagicWallController controller = controllerSystem.getComponentBuilder( MagicWallController.class )
            .set( EntityController.NAME, MAGIC_WALL_NAME )
        .build();
        controllerId = controller.getId();
        float updateRate = caveService.getUpdateRate();
        controller.setUpdateResolution( updateRate );
        
        prefabId = prefabSystem.getEntityPrefabBuilder()
            .set( EController.CONTROLLER_IDS, new int[] { controllerId, spriteAnimationHandler.getControllerId() } )
            .set( EntityPrefab.NAME, MAGIC_WALL_NAME )
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveService.CAVE_VIEW_NAME ) )
            .set( ESprite.SPRITE_ID, firstSpriteId )
            .set( ETile.MULTI_POSITION, false )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.ASPECTS, AspectSetBuilder.create( 
                UnitAspect.DESTRUCTIBLE
            ) )
        .build().getId();
        prefabSystem.cacheComponents( prefabId, 50 );
        
        AnimationSystem animationSystem = context.getComponent( AnimationSystem.CONTEXT_KEY );
        MagicWallAnimationController animController = (MagicWallAnimationController) animationSystem.getAnimation( spriteAnimationHandler.getAnimationId() );
        controller.setMagicWallAnimationController( animController );
        animController.setMagicWallState( State.INACTIVE );
        spriteAnimationHandler.setFrameTime( State.INACTIVE.ordinal(), Integer.MAX_VALUE );
        spriteAnimationHandler.setFrameTime( State.ACTIVE.ordinal(), 100 - (int) updateRate * 4 );
    }
    
    @Override
    public final void disposeCaveData( FFContext context ) {
        super.disposeCaveData( context );

        controllerSystem.deleteController( controllerId );
        controllerId = -1;
        prefabSystem.deletePrefab( prefabId );
    }

    @Override
    public final UnitType type() {
        return UnitType.MAGIC_WALL;
    }

    @Override
    public final void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( "M", type() );
    }

    @Override
    public final int getSoundId() {
        return soundId;
    }

    @Override
    public final int createOne( int xGridPos, int yGridPos ) {
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
        prefabSystem.deletePrefab( MAGIC_WALL_NAME );
        soundSystem.deleteSound( soundId );
        assetSystem.deleteAsset( MAGIC_WALL_SOUND_ASSEET_KEY );
    }

}
