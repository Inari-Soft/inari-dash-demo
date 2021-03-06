package com.inari.dash.game.tasks;

import com.inari.dash.Configuration;
import com.inari.dash.game.GameSystem;
import com.inari.dash.game.tasks.InitGameWorkflow.TaskName;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.audio.AudioSystem;
import com.inari.firefly.audio.AudioSystemEvent;
import com.inari.firefly.audio.Sound;
import com.inari.firefly.audio.SoundAsset;
import com.inari.firefly.control.task.Task;
import com.inari.firefly.control.task.TaskSystemEvent;
import com.inari.firefly.control.task.TaskSystemEvent.Type;
import com.inari.firefly.graphics.text.FontAsset;
import com.inari.firefly.system.FFContext;

public final class LoadGame extends Task {
    
    private boolean globalAssetsLoaded = false;

    public LoadGame( int id ) {
        super( id );
    }

    @Override
    public final void runTask() {
        
        if ( !globalAssetsLoaded ) {
            loadGlobalAssets( context );
        }
        
        context.notify( new TaskSystemEvent( Type.RUN_TASK, TaskName.LOAD_GAME_SELECTION.name() ) ); 
        context.notify( new AudioSystemEvent( GameSystem.TITLE_SONG_SOUND_NAME, AudioSystemEvent.Type.PLAY_SOUND ) );
    }
    
    public final void loadGlobalAssets( FFContext context ) {
        AssetSystem assetSystem = context.getSystem( AssetSystem.SYSTEM_KEY );
        AudioSystem soundSystem = context.getSystem( AudioSystem.SYSTEM_KEY );
        Configuration configuration = context.getContextComponent( Configuration.COMPONENT_NAME );
        
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
            .set( Sound.SOUND_ASSET_ID, soundAsset.index() )
            .set( Sound.VOLUME, 10 )
            .set( Sound.LOOPING, true )
            .set( Sound.NAME, GameSystem.TITLE_SONG_SOUND_NAME )
        .build();
        
        globalAssetsLoaded = true;
    }

}
