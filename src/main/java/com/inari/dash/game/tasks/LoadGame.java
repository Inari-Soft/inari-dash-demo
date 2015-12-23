package com.inari.dash.game.tasks;

import com.inari.dash.Configuration;
import com.inari.dash.game.GameSystem;
import com.inari.dash.game.tasks.InitGameWorkflow.TaskName;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.renderer.text.FontAsset;
import com.inari.firefly.sound.Sound;
import com.inari.firefly.sound.SoundAsset;
import com.inari.firefly.sound.SoundSystem;
import com.inari.firefly.sound.event.SoundEvent;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;
import com.inari.firefly.task.event.TaskEvent;
import com.inari.firefly.task.event.TaskEvent.Type;

public final class LoadGame extends Task {

    public LoadGame( int id ) {
        super( id );
    }

    @Override
    public final void run( FFContext context ) {
        
        loadGlobalAssets( context );
        
        context.notify( new TaskEvent( Type.RUN_TASK, TaskName.LOAD_GAME_SELECTION.name() ) ); 
        context.notify( new SoundEvent( GameSystem.TITLE_SONG_SOUND_NAME, SoundEvent.Type.PLAY_SOUND ) );
    }
    
    public final void loadGlobalAssets( FFContext context ) {
        AssetSystem assetSystem = context.getSystem( AssetSystem.SYSTEM_KEY );
        SoundSystem soundSystem = context.getSystem( SoundSystem.SYSTEM_KEY );
        Configuration configuration = context.getContextComponent( Configuration.CONTEXT_KEY );
        
        // create and load font
        assetSystem.getAssetBuilder()
            .set( FontAsset.NAME, GameSystem.GAME_FONT_TEXTURE_NAME )
            .set( FontAsset.TEXTURE_RESOURCE_NAME, configuration.fontTextureResource )
            .set( FontAsset.CHAR_TEXTURE_MAP, configuration.fontChars )
            .set( FontAsset.CHAR_WIDTH, configuration.charWidth )
            .set( FontAsset.CHAR_HEIGHT, configuration.charHeight )
            .set( FontAsset.CHAR_SPACE, 0 )
            .set( FontAsset.LINE_SPACE, 5 )
            .set( FontAsset.DEFAULT_CHAR, '%' )
        .activateAndNext( FontAsset.class )
            // create and load intro music
            .set( SoundAsset.NAME, GameSystem.INTRO_SONG_NAME )
            .set( SoundAsset.STREAMING, false )
            .set( SoundAsset.RESOURCE_NAME, configuration.titleSongResource )
        .activate( SoundAsset.class );
        
        SoundAsset soundAsset = assetSystem.getAssetAs( GameSystem.INTRO_SONG_NAME, SoundAsset.class );
        soundSystem.getSoundBuilder()
            .set( Sound.SOUND_ASSET_ID, soundAsset.getId() )
            .set( Sound.VOLUME, 10 )
            .set( Sound.LOOPING, true )
            .set( Sound.NAME, GameSystem.TITLE_SONG_SOUND_NAME )
        .build();
    }

}
