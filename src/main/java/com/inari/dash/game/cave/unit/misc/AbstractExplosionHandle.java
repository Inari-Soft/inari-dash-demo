package com.inari.dash.game.cave.unit.misc;

import com.inari.dash.game.cave.CaveSystem.SoundChannel;
import com.inari.dash.game.cave.unit.Unit;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.audio.Sound;
import com.inari.firefly.audio.SoundAsset;
import com.inari.firefly.control.Controller;
import com.inari.firefly.entity.EntityController;

public abstract class AbstractExplosionHandle  extends Unit {
    
    protected AbstractExplosionHandle( int id ) {
        super( id );
    }

    public static final String EXPLOSION_SOUND_ASSEET_NAME =  "explosionSound";
    private static boolean GENERAL_INIT_DONE = false;
    protected static int CONTROLLER_ID;
    private static int soundId;

    protected void initGeneralExplosion() {
        if ( GENERAL_INIT_DONE ) {
            return;
        }
        
        context.getComponentBuilder( Asset.TYPE_KEY )
            .set( SoundAsset.NAME, EXPLOSION_SOUND_ASSEET_NAME )
            .set( SoundAsset.RESOURCE_NAME, "original/sound/explosion.wav" )
            .set( SoundAsset.STREAMING, false )
        .activate( SoundAsset.class );
        
        soundId = context.getComponentBuilder( Sound.TYPE_KEY )
            .set( Sound.NAME, EXPLOSION_SOUND_ASSEET_NAME )
            .set( Sound.SOUND_ASSET_NAME, EXPLOSION_SOUND_ASSEET_NAME )
            .set( Sound.CHANNEL, SoundChannel.ROCK.ordinal() )
            .set( Sound.LOOPING, false )
        .build();

        CONTROLLER_ID = context.getComponentBuilder( Controller.TYPE_KEY )
            .set( EntityController.NAME, "ExplosionController" )
            .set( EntityController.UPDATE_RESOLUTION, getUpdateRate() )
        .build( ExplosionController.class );
        
        
        GENERAL_INIT_DONE = true;
    }
    
    protected void disposeGeneralExplosion() {
        if ( !GENERAL_INIT_DONE ) {
            return;
        }
        
        context.deleteSystemComponent( Sound.TYPE_KEY, EXPLOSION_SOUND_ASSEET_NAME );
        context.deleteSystemComponent( Asset.TYPE_KEY, EXPLOSION_SOUND_ASSEET_NAME );
        
        GENERAL_INIT_DONE = false;
    }

    @Override
    public final int getSoundId() {
        return soundId;
    }


}
