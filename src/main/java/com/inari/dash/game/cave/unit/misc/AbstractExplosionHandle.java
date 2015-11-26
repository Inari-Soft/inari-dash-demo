package com.inari.dash.game.cave.unit.misc;

import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.unit.UnitHandle;
import com.inari.firefly.asset.AssetNameKey;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.sound.Sound;
import com.inari.firefly.sound.SoundAsset;
import com.inari.firefly.system.FFContext;

public abstract class AbstractExplosionHandle  extends UnitHandle {
    
    public static final AssetNameKey EXPLOSION_SOUND_ASSEET_KEY = new AssetNameKey( CaveSystem.CAVE_SOUND_GROUP_NAME, "explosion" );
    private static boolean GENERAL_INIT_DONE = false;
    protected static int CONTROLLER_ID;
    private static int soundId;
    
    
    
    protected void initGeneralExplosion() {
        if ( GENERAL_INIT_DONE ) {
            return;
        }
        
        assetSystem.getAssetBuilderWithAutoLoad()
            .set( SoundAsset.NAME, EXPLOSION_SOUND_ASSEET_KEY.name )
            .set( SoundAsset.ASSET_GROUP, EXPLOSION_SOUND_ASSEET_KEY.group )
            .set( SoundAsset.RESOURCE_NAME, "original/sound/explosion.wav" )
            .set( SoundAsset.STREAMING, false )
        .build( SoundAsset.class );
        
        soundId = soundSystem.getSoundBuilder()
            .set( Sound.NAME, EXPLOSION_SOUND_ASSEET_KEY.name )
            .set( Sound.ASSET_ID, assetSystem.getAssetId( EXPLOSION_SOUND_ASSEET_KEY ) )
            .set( Sound.CHANNEL, 2 )
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
        
        soundSystem.deleteSound( soundSystem.getSoundId( EXPLOSION_SOUND_ASSEET_KEY.name ) );
        assetSystem.deleteAsset( EXPLOSION_SOUND_ASSEET_KEY );
        
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
