package com.inari.dash.game.cave.unit.misc;

import java.util.Collection;
import java.util.Map;

import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.AspectSetBuilder;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.CaveSystem.SoundChannel;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitHandle;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.animation.sprite.SpriteAnimationBuilder;
import com.inari.firefly.animation.sprite.SpriteAnimationBuilder.SpriteAnimationHandler;
import com.inari.firefly.asset.AssetNameKey;
import com.inari.firefly.asset.AssetTypeKey;
import com.inari.firefly.control.Controller;
import com.inari.firefly.control.EController;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.sound.Sound;
import com.inari.firefly.sound.SoundAsset;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;

public final class Amoeba extends UnitHandle {
    
    public static final String AMOEBA_NAME = "amoeba";
    public static final AssetNameKey MOEBA_SPRITE_ASSET_KEY = new AssetNameKey( CaveSystem.GAME_UNIT_TEXTURE_KEY.group, AMOEBA_NAME );
    public static final AssetNameKey MOEBA_SOUND_ASSEET_KEY = new AssetNameKey( CaveSystem.CAVE_SOUND_GROUP_NAME, AMOEBA_NAME );
    private static final int UPDATE_TIME_FACTOR = 2;
    
    private int controllerId;
    private SpriteAnimationHandler spriteAnimationHandler;
    private int amoebaEntityId;
    private int firstSpriteId;
    private int soundId;

    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );
        
        // sound
        assetSystem.getAssetBuilder()
            .set( SoundAsset.NAME, MOEBA_SOUND_ASSEET_KEY.name )
            .set( SoundAsset.ASSET_GROUP, MOEBA_SOUND_ASSEET_KEY.group )
            .set( SoundAsset.RESOURCE_NAME, "original/sound/amoeba.wav" )
            .set( SoundAsset.STREAMING, false )
        .activate( SoundAsset.class );
        soundId = soundSystem.getSoundBuilder()
            .set( Sound.NAME, MOEBA_SOUND_ASSEET_KEY.name )
            .set( Sound.ASSET_ID, assetSystem.getAssetId( MOEBA_SOUND_ASSEET_KEY ) )
            .set( Sound.CHANNEL, SoundChannel.AMOEBA.ordinal() )
            .set( Sound.LOOPING, true )
        .build();
        
        // sprite animation
        spriteAnimationHandler = new SpriteAnimationBuilder( context )
            .setGroup( CaveSystem.GAME_UNIT_TEXTURE_KEY.group )
            .setLooping( true )
            .setNamePrefix( AMOEBA_NAME )
            .setTextureAssetKey( CaveSystem.GAME_UNIT_TEXTURE_KEY )
            .addSpritesToAnimation( 0, new Rectangle( 0, 8 * 32, 32, 32 ), 8, true )
        .build();
        Collection<AssetTypeKey> allSpriteAssetKeys = spriteAnimationHandler.getAllSpriteAssetKeys();
        caveAssetsToReload.addAll( allSpriteAssetKeys );
        firstSpriteId = allSpriteAssetKeys.iterator().next().id;
        
        
        
        initialized = true;
    }

    @Override
    public final void loadCaveData( FFContext context ) {
        super.loadCaveData( context );
        
        // controller
        controllerId = controllerSystem.getControllerBuilder()
            .set( EntityController.NAME, AMOEBA_NAME )
        .build( AmoebaController.class );
        Controller controller = controllerSystem.getController( controllerId );
        
        float updateRate = caveService.getUpdateRate();
        controller.setUpdateResolution( UPDATE_TIME_FACTOR );
        spriteAnimationHandler.setFrameTime( 60 - (int) updateRate * 4 );
        
        amoebaEntityId = entitySystem.getEntityBuilder()
            .set( EController.CONTROLLER_IDS, new int[] { controllerId, spriteAnimationHandler.getControllerId() } )
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveSystem.CAVE_VIEW_NAME ) )
            .set( ESprite.SPRITE_ID, firstSpriteId )
            .set( ETile.MULTI_POSITION, true )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.ASPECTS, AspectSetBuilder.create( 
                UnitAspect.DESTRUCTIBLE
            ) )
        .activate();
    }

    @Override
    public final void disposeCaveData( FFContext context ) {
        super.disposeCaveData( context );
        controllerSystem.deleteController( controllerId );
        entitySystem.delete( amoebaEntityId );
    }

    @Override
    public final UnitType type() {
        return UnitType.AMOEBA;
    }

    @Override
    public final void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( "a", type() );
    }

    @Override
    public final int getSoundId() {
        return soundId;
    }

    @Override
    public final int createOne( int xGridPos, int yGridPos ) {
        ETile tile = entitySystem.getComponent( amoebaEntityId, ETile.class );
        if ( tile.getGridPositions().isEmpty() ) {
            EUnit unit = entitySystem.getComponent( amoebaEntityId, EUnit.class );
            unit.setAspect( UnitAspect.ACTIVE );
        }
        tile.getGridPositions().add( new Position( xGridPos, yGridPos ) );
        caveService.setEntityId( amoebaEntityId, xGridPos, yGridPos );
        
        return amoebaEntityId;
    }
    
    @Override
    public final void dispose( FFContext context ) {
        spriteAnimationHandler.dispose( context );
        soundSystem.deleteSound( soundId );
        controllerSystem.deleteController( controllerId );
    }

}
