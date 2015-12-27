package com.inari.dash.game.cave.unit.rockford;

import java.util.Map;

import com.inari.commons.geom.Rectangle;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.CaveSystem.SoundChannel;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitHandle;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.dash.game.cave.unit.rockford.RFUnit.RFState;
import com.inari.firefly.FFInitException;
import com.inari.firefly.animation.sprite.SpriteAnimationBuilder;
import com.inari.firefly.animation.sprite.SpriteAnimationBuilder.SpriteAnimationHandler;
import com.inari.firefly.audio.Sound;
import com.inari.firefly.audio.SoundAsset;
import com.inari.firefly.control.Controller;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.graphics.sprite.ESprite;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.system.FFContext;

public final class Rockford extends UnitHandle {
    
    public static final String ROCKFORD_NAME = "rockford";
    public static final String ROCKFORD_SPRITE_ASSET_NAME = ROCKFORD_NAME + "_sprite";
    public static final String ROCKFORD_SPACE_SOUND_ASSEET_NAME = ROCKFORD_NAME + "_space";
    public static final String ROCKFORD_SAND_SOUND_ASSEET_NAME = ROCKFORD_NAME + "_sand";
    public static final String ROCKFORD_COLLECT_SOUND_ASSEET_NAME = ROCKFORD_NAME + "_collect";
    
    private SpriteAnimationHandler spriteAnimationHandler;
    private int controllerId;
    private int rfEntityId;
    
    int spaceSoundId;
    int sandSoundId;
    int inSoundId;
    int collectSoundId;
    
    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );

        createSounds();
        
        initialized = true;
    }

    private void createSounds() {
        int soundAssetIdSpace = assetSystem.getAssetBuilder()
            .set( SoundAsset.NAME, ROCKFORD_SPACE_SOUND_ASSEET_NAME )
            .set( SoundAsset.RESOURCE_NAME, "original/sound/walkSpace.wav" )
            .set( SoundAsset.STREAMING, false )
        .activate( SoundAsset.class );
        int soundAssetIdSand = assetSystem.getAssetBuilder()
            .set( SoundAsset.NAME, ROCKFORD_SAND_SOUND_ASSEET_NAME )
            .set( SoundAsset.RESOURCE_NAME, "original/sound/walkSand.wav" )
            .set( SoundAsset.STREAMING, false )
        .activate( SoundAsset.class );
        int soundAssetIdCollect = assetSystem.getAssetBuilder()
            .set( SoundAsset.NAME, ROCKFORD_COLLECT_SOUND_ASSEET_NAME )
            .set( SoundAsset.RESOURCE_NAME, "original/sound/collectDiamond.wav" )
            .set( SoundAsset.STREAMING, false )
        .activate( SoundAsset.class );
        
        spaceSoundId = soundSystem.getSoundBuilder()
            .set( Sound.NAME, ROCKFORD_SPACE_SOUND_ASSEET_NAME )
            .set( Sound.SOUND_ASSET_ID, soundAssetIdSpace )
            .set( Sound.LOOPING, false )
        .build();
        sandSoundId = soundSystem.getSoundBuilder()
            .set( Sound.NAME, ROCKFORD_SAND_SOUND_ASSEET_NAME )
            .set( Sound.SOUND_ASSET_ID, soundAssetIdSand )
            .set( Sound.LOOPING, false )
        .build();
        collectSoundId = soundSystem.getSoundBuilder()
            .set( Sound.NAME, ROCKFORD_COLLECT_SOUND_ASSEET_NAME )
            .set( Sound.SOUND_ASSET_ID, soundAssetIdCollect )
            .set( Sound.LOOPING, false )
            .set( Sound.CHANNEL, SoundChannel.COLLECT.ordinal() )
        .build();
        inSoundId = soundSystem.getSoundId( CaveSystem.CaveSoundKey.CRACK.name() );
    }

    @Override
    public final void loadCaveData( FFContext context ) {
        super.loadCaveData( context );
        
        spriteAnimationHandler = new SpriteAnimationBuilder( context )
            .setLooping( true )
            .setNamePrefix( ROCKFORD_NAME )
            .setTextureAssetName( CaveSystem.GAME_UNIT_TEXTURE_NAME )
            .setStatedAnimationType( RFSpriteAnimation.class )
            .setState( RFState.ENTERING.ordinal() )
            .addSpritesToAnimation( 0, new Rectangle( 32, 6 * 32, 32, 32 ), 2, true )
            .setState( RFState.APPEARING.ordinal() )
            .addSpritesToAnimation( 0, new Rectangle( 32, 0, 32, 32 ), 3, true )
            .setState( RFState.IDLE.ordinal() )
            .addSpritesToAnimation( 0, new Rectangle( 0, 0, 32, 32 ), 1, true )
            .setState( RFState.IDLE_BLINKING.ordinal() )
            .addSpritesToAnimation( 0, new Rectangle( 0, 32, 32, 32 ), 8, true )
            .setState( RFState.IDLE_FRETFUL.ordinal() )
            .addSpritesToAnimation( 0, new Rectangle( 0, 2 * 32, 32, 32 ), 8, true )
            .setState( RFState.LEFT.ordinal() )
            .addSpritesToAnimation( 0, new Rectangle( 0, 4 * 32, 32, 32 ), 8, true )
            .setState( RFState.RIGHT.ordinal() )
            .addSpritesToAnimation( 0, new Rectangle( 0, 5 * 32, 32, 32 ), 8, true )
        .build();
        
        float updateRate = caveService.getUpdateRate();
        controllerId = controllerSystem.getControllerBuilder()
            .set( EntityController.NAME, ROCKFORD_NAME )
            .set( Controller.UPDATE_RESOLUTION, updateRate )
        .build( RFController.class );
        
        spriteAnimationHandler.setFrameTime( RFState.ENTERING.ordinal(), 400 - (int) updateRate * 4 );
        spriteAnimationHandler.setFrameTime( RFState.APPEARING.ordinal(), 300 - (int) updateRate * 4 );
        spriteAnimationHandler.setFrameTime( RFState.IDLE.ordinal(), Integer.MAX_VALUE );
        spriteAnimationHandler.setFrameTime( RFState.IDLE_BLINKING.ordinal(), 100 - (int) updateRate * 4 );
        spriteAnimationHandler.setFrameTime( RFState.IDLE_FRETFUL.ordinal(), 100 - (int) updateRate * 4 );
        spriteAnimationHandler.setFrameTime( RFState.LEFT.ordinal(), 60 - (int) updateRate * 4 );
        spriteAnimationHandler.setFrameTime( RFState.RIGHT.ordinal(), 60 - (int) updateRate * 4 );
    }
    
    

    @Override
    public void disposeCaveData( FFContext context ) {
        super.disposeCaveData( context );
        controllerSystem.deleteController( controllerId );
        rfEntityId = -1;
    }

    @Override
    public final UnitType type() {
        return UnitType.ROCKFORD;
    }

    @Override
    public final void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( "P", type() );
        bdcffMap.put( "P1", type() );
    }

    @Override
    public final int createOne( int xGridPos, int yGridPos ) {
        rfEntityId = entitySystem.getEntityBuilder()
            .add( EEntity.CONTROLLER_IDS, controllerId )
            .add( EEntity.CONTROLLER_IDS, spriteAnimationHandler.getControllerId() )
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveSystem.CAVE_VIEW_NAME ) )
            .set( ESprite.SPRITE_ID, spriteAnimationHandler.getStartSpriteId() )
            .set( ETile.GRID_X_POSITION, xGridPos )
            .set( ETile.GRID_Y_POSITION, yGridPos )
            .set( EUnit.UNIT_TYPE, type() )
            .set( RFUnit.STATE, RFState.ENTERING )
        .activate();
        return rfEntityId;
    }

    @Override
    public final int getEntityId() {
        return rfEntityId;
    }

    @Override
    public final void dispose( FFContext context ) {
        spriteAnimationHandler.dispose( context );
        RFController rfController = (RFController) controllerSystem.getController( controllerId );
        if ( rfController != null ) {
            soundSystem.deleteSound( spaceSoundId );
            soundSystem.deleteSound( sandSoundId );
            soundSystem.deleteSound( collectSoundId );
        }
        controllerSystem.deleteController( controllerId );
        entitySystem.delete( rfEntityId );
    }

}
