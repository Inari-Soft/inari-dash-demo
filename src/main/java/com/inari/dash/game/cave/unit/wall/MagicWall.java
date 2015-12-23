package com.inari.dash.game.cave.unit.wall;

import java.util.Map;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.AspectSetBuilder;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.CaveSystem.SoundChannel;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitHandle;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.dash.game.cave.unit.wall.MagicWallAnimationController.State;
import com.inari.firefly.FFInitException;
import com.inari.firefly.animation.AnimationSystem;
import com.inari.firefly.animation.sprite.SpriteAnimationBuilder;
import com.inari.firefly.animation.sprite.SpriteAnimationBuilder.SpriteAnimationHandler;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.entity.EntityPrefab;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.sound.Sound;
import com.inari.firefly.sound.SoundAsset;
import com.inari.firefly.sound.event.SoundEvent;
import com.inari.firefly.sound.event.SoundEvent.Type;
import com.inari.firefly.system.FFContext;

public final class MagicWall extends UnitHandle {
    
    public static final String MAGIC_WALL_NAME = "magicWall";

    private SpriteAnimationHandler spriteAnimationHandler;
    private int prefabId;
    private int controllerId;
    private int soundId;
    

    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );

        String soundName = MAGIC_WALL_NAME + "_sound";
        int soundAssetId = assetSystem.getAssetBuilder()
            .set( SoundAsset.NAME, soundName )
            .set( SoundAsset.RESOURCE_NAME, "original/sound/" + MAGIC_WALL_NAME + ".wav" )
            .set( SoundAsset.STREAMING, false )
        .activate( SoundAsset.class );
        
        soundId = soundSystem.getSoundBuilder()
            .set( Sound.NAME, soundName )
            .set( Sound.SOUND_ASSET_ID, soundAssetId )
            .set( Sound.CHANNEL, SoundChannel.MAGIC_WALL.ordinal() )
            .set( Sound.LOOPING, true )
        .build();
    }

    @Override
    public final void loadCaveData( FFContext context ) {
        super.loadCaveData( context );
        
        spriteAnimationHandler = new SpriteAnimationBuilder( context )
            .setLooping( true )
            .setNamePrefix( MAGIC_WALL_NAME )
            .setTextureAssetName( CaveSystem.GAME_UNIT_TEXTURE_NAME )
            .setStatedAnimationType( MagicWallAnimationController.class )
            .setState( State.INACTIVE.ordinal() )
            .addSpritesToAnimation( 0, new Rectangle( 3 * 32, 6 * 32, 32, 32 ), 1, true )
            .setState( State.ACTIVE.ordinal() )
            .addSpritesToAnimation( 0, new Rectangle( 4 * 32, 6 * 32, 32, 32 ), 4, true )
        .build();
        
        controllerId = controllerSystem.getControllerBuilder()
            .set( EntityController.NAME, MAGIC_WALL_NAME )
        .build( MagicWallController.class );
        MagicWallController controller = controllerSystem.getControllerAs( 
            controllerId,
            MagicWallController.class
        );
        float updateRate = caveService.getUpdateRate();
        controller.setUpdateResolution( updateRate );
        
        prefabId = prefabSystem.getEntityPrefabBuilder()
            .set( EntityPrefab.NAME, MAGIC_WALL_NAME )
            .add( EEntity.CONTROLLER_IDS, controllerId )
            .add( EEntity.CONTROLLER_IDS, spriteAnimationHandler.getControllerId() )
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveSystem.CAVE_VIEW_NAME ) )
            .set( ESprite.SPRITE_ID, spriteAnimationHandler.getStartSpriteId() )
            .set( ETile.MULTI_POSITION, false )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.ASPECTS, AspectSetBuilder.create( 
                UnitAspect.DESTRUCTIBLE
            ) )
        .build();
        prefabSystem.cacheComponents( prefabId, 50 );
        
        AnimationSystem animationSystem = context.getSystem( AnimationSystem.SYSTEM_KEY );
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
        spriteAnimationHandler.dispose( context );
        context.notify( new SoundEvent( soundId, Type.STOP_PLAYING ) );
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
        int entityId = prefabSystem.buildOne( prefabId );
        ETile tile = entitySystem.getComponent( entityId , ETile.TYPE_KEY );
        tile.setGridXPos( xGridPos );
        tile.setGridYPos( yGridPos );
        entitySystem.activateEntity( entityId );
        return entityId;
    }
    
    @Override
    public final void dispose( FFContext context ) {
        prefabSystem.deletePrefab( MAGIC_WALL_NAME );
        soundSystem.deleteSound( soundId );
        assetSystem.deleteAsset( MAGIC_WALL_NAME + "_sound" );
    }

}
