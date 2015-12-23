package com.inari.dash.game.cave.unit.misc;

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
import com.inari.firefly.FFInitException;
import com.inari.firefly.animation.sprite.SpriteAnimationBuilder;
import com.inari.firefly.animation.sprite.SpriteAnimationBuilder.SpriteAnimationHandler;
import com.inari.firefly.control.Controller;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.sound.Sound;
import com.inari.firefly.sound.SoundAsset;
import com.inari.firefly.sound.event.SoundEvent;
import com.inari.firefly.sound.event.SoundEvent.Type;
import com.inari.firefly.system.FFContext;

public final class Amoeba extends UnitHandle {
    
    public static final String AMOEBA_NAME = "amoeba";
    public static final String MOEBA_SOUND_ASSEET_NAME = AMOEBA_NAME + "_sound";
    private static final int UPDATE_TIME_FACTOR = 2;
    
    private int controllerId;
    private SpriteAnimationHandler spriteAnimationHandler;
    private int amoebaEntityId;
    private int soundId;

    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );
        
        // sound
        int soundAssetId = assetSystem.getAssetBuilder()
            .set( SoundAsset.NAME, MOEBA_SOUND_ASSEET_NAME )
            .set( SoundAsset.RESOURCE_NAME, "original/sound/amoeba.wav" )
            .set( SoundAsset.STREAMING, false )
        .activate( SoundAsset.class );
        
        soundId = soundSystem.getSoundBuilder()
            .set( Sound.NAME, MOEBA_SOUND_ASSEET_NAME )
            .set( Sound.SOUND_ASSET_ID, soundAssetId )
            .set( Sound.CHANNEL, SoundChannel.AMOEBA.ordinal() )
            .set( Sound.LOOPING, true )
        .build();

        initialized = true;
    }

    @Override
    public final void loadCaveData( FFContext context ) {
        super.loadCaveData( context );
        
        // sprite animation
        spriteAnimationHandler = new SpriteAnimationBuilder( context )
            .setLooping( true )
            .setNamePrefix( AMOEBA_NAME )
            .setTextureAssetName( CaveSystem.GAME_UNIT_TEXTURE_NAME )
            .addSpritesToAnimation( 0, new Rectangle( 0, 8 * 32, 32, 32 ), 8, true )
        .build();
        
        // controller
        controllerId = controllerSystem.getControllerBuilder()
            .set( EntityController.NAME, AMOEBA_NAME )
        .build( AmoebaController.class );
        Controller controller = controllerSystem.getController( controllerId );
        
        float updateRate = caveService.getUpdateRate();
        controller.setUpdateResolution( UPDATE_TIME_FACTOR );
        spriteAnimationHandler.setFrameTime( 60 - (int) updateRate * 4 );
        
        amoebaEntityId = entitySystem.getEntityBuilder()
            .add( EEntity.CONTROLLER_IDS, controllerId )
            .add( EEntity.CONTROLLER_IDS, spriteAnimationHandler.getControllerId() )
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveSystem.CAVE_VIEW_NAME ) )
            .set( ESprite.SPRITE_ID, spriteAnimationHandler.getStartSpriteId() )
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
        spriteAnimationHandler.dispose( context );
        context.notify( new SoundEvent( soundId, Type.STOP_PLAYING ) );
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
        ETile tile = entitySystem.getComponent( amoebaEntityId, ETile.TYPE_KEY );
        if ( tile.getGridPositions().isEmpty() ) {
            EUnit unit = entitySystem.getComponent( amoebaEntityId, EUnit.TYPE_KEY );
            unit.setAspect( UnitAspect.ACTIVE );
        }
        tile.getGridPositions().add( new Position( xGridPos, yGridPos ) );
        caveService.setEntityId( amoebaEntityId, xGridPos, yGridPos );
        
        return amoebaEntityId;
    }
    
    @Override
    public final void dispose( FFContext context ) {
        soundSystem.deleteSound( soundId );
        controllerSystem.deleteController( controllerId );
    }

}
