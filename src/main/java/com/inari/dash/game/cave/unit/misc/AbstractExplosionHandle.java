package com.inari.dash.game.cave.unit.misc;

import com.inari.dash.game.cave.CaveSystem.SoundChannel;
import com.inari.dash.game.cave.unit.UnitHandle;
import com.inari.firefly.audio.Sound;
import com.inari.firefly.audio.SoundAsset;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.system.FFContext;

public abstract class AbstractExplosionHandle  extends UnitHandle {
    
    public static final String EXPLOSION_SOUND_ASSEET_NAME =  "explosionSound";
    private static boolean GENERAL_INIT_DONE = false;
    protected static int CONTROLLER_ID;
    private static int soundId;
    
    
    
    protected void initGeneralExplosion() {
        if ( GENERAL_INIT_DONE ) {
            return;
        }
        
        int soundAssetId = assetSystem.getAssetBuilder()
            .set( SoundAsset.NAME, EXPLOSION_SOUND_ASSEET_NAME )
            .set( SoundAsset.RESOURCE_NAME, "original/sound/explosion.wav" )
            .set( SoundAsset.STREAMING, false )
        .activate( SoundAsset.class );
        
        soundId = soundSystem.getSoundBuilder()
            .set( Sound.NAME, EXPLOSION_SOUND_ASSEET_NAME )
            .set( Sound.SOUND_ASSET_ID, soundAssetId )
            .set( Sound.CHANNEL, SoundChannel.ROCK.ordinal() )
            .set( Sound.LOOPING, false )
        .build();

        CONTROLLER_ID = controllerSystem.getControllerBuilder()
            .set( EntityController.NAME, "ExplosionController" )
        .build( ExplosionController.class );
        
        GENERAL_INIT_DONE = true;
    }
    
    protected void disposeGeneralExplosion() {
        if ( !GENERAL_INIT_DONE ) {
            return;
        }
        
        soundSystem.deleteSound( soundSystem.getSoundId( EXPLOSION_SOUND_ASSEET_NAME ) );
        assetSystem.deleteAsset( EXPLOSION_SOUND_ASSEET_NAME );
        
        GENERAL_INIT_DONE = false;
    }
    
    @Override
    public void loadCaveData( FFContext context ) {
        super.loadCaveData( context );
        controllerSystem.getController( CONTROLLER_ID ).setUpdateResolution( 
            caveService.getUpdateRate() 
        );
    }

    @Override
    public int getSoundId() {
        return soundId;
    }


}
