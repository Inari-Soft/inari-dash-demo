package com.inari.dash.game.tasks;

import com.inari.dash.Configuration;
import com.inari.dash.game.GameSystem;
import com.inari.dash.game.tasks.InitGameWorkflow.TaskName;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.renderer.text.Font;
import com.inari.firefly.renderer.text.TextSystem;
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
        TextSystem textSystem = context.getSystem( TextSystem.SYSTEM_KEY );
        SoundSystem soundSystem = context.getSystem( SoundSystem.SYSTEM_KEY );
        Configuration configuration = context.getContextComponent( Configuration.CONTEXT_KEY );
        
        // create and load font
        textSystem.getFontBuilder()
            .set( Font.NAME, GameSystem.GAME_FONT_TEXTURE_KEY.name )
            .set( Font.FONT_TEXTURE_RESOURCE_NAME, configuration.fontTextureResource )
            .set( Font.CHAR_TEXTURE_MAP, configuration.fontChars )
            .set( Font.CHAR_WIDTH, configuration.charWidth )
            .set( Font.CHAR_HEIGHT, configuration.charHeight )
            .set( Font.CHAR_SPACE, 0 )
            .set( Font.LINE_SPACE, 5 )
            .set( Font.DEFAULT_CHAR, '%' )
        .activate();
        // create and load intro music
        assetSystem.getAssetBuilder()
            .set( SoundAsset.NAME, GameSystem.INTRO_SONG_KEY.name )
            .set( SoundAsset.ASSET_GROUP, GameSystem.INTRO_SONG_KEY.group )
            .set( SoundAsset.STREAMING, false )
            .set( SoundAsset.RESOURCE_NAME, configuration.titleSongResource )
        .activate( SoundAsset.class );
        soundSystem.getSoundBuilder()
            .set( Sound.ASSET_ID, assetSystem.getAssetTypeKey( GameSystem.INTRO_SONG_KEY ).id )
            .set( Sound.VOLUME, 10 )
            .set( Sound.LOOPING, true )
            .set( Sound.NAME, GameSystem.TITLE_SONG_SOUND_NAME )
        .build();
    }

}
