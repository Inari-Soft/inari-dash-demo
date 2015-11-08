package com.inari.dash.game.tasks;

import com.inari.commons.event.IEventDispatcher;
import com.inari.dash.Configuration;
import com.inari.dash.game.GameService;
import com.inari.dash.game.GameService.TaskName;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.sound.Sound;
import com.inari.firefly.sound.SoundAsset;
import com.inari.firefly.sound.SoundSystem;
import com.inari.firefly.sound.event.SoundEvent;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;
import com.inari.firefly.task.event.TaskEvent;
import com.inari.firefly.task.event.TaskEvent.Type;
import com.inari.firefly.text.Font;
import com.inari.firefly.text.TextSystem;

public final class LoadGame extends Task {

    public LoadGame( int id ) {
        super( id );
    }

    @Override
    public final void run( FFContext context ) {
        IEventDispatcher eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
        
        loadGlobalAssets( context );
        
        eventDispatcher.notify( new TaskEvent( Type.RUN_TASK, TaskName.LOAD_GAME_SELECTION.name() ) ); 
        eventDispatcher.notify( new SoundEvent( GameService.TITLE_SONG_SOUND_NAME, SoundEvent.Type.PLAY_SOUND ) );
    }
    
    public final void loadGlobalAssets( FFContext context ) {
        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        TextSystem textSystem = context.getComponent( TextSystem.CONTEXT_KEY );
        SoundSystem soundSystem = context.getComponent( SoundSystem.CONTEXT_KEY );
        Configuration configuration = context.getComponent( GameService.CONTEXT_KEY ).getConfiguration();
        
        // create and load font
        textSystem.getFontBuilderWithAutoLoad()
            .set( Font.NAME, GameService.GAME_FONT_TEXTURE_KEY.name )
            .set( Font.FONT_TEXTURE_RESOURCE_NAME, configuration.fontTextureResource )
            .set( Font.CHAR_TEXTURE_MAP, configuration.fontChars )
            .set( Font.CHAR_WIDTH, configuration.charWidth )
            .set( Font.CHAR_HEIGHT, configuration.charHeight )
            .set( Font.CHAR_SPACE, 0 )
            .set( Font.LINE_SPACE, 5 )
            .set( Font.DEFAULT_CHAR, '%' )
        .build();
        // create and load intro music
        assetSystem.getAssetBuilder( SoundAsset.class )
            .set( SoundAsset.NAME, GameService.INTRO_SONG_KEY.name )
            .set( SoundAsset.ASSET_GROUP, GameService.INTRO_SONG_KEY.group )
            .set( SoundAsset.STREAMING, false )
            .set( SoundAsset.RESOURCE_NAME, configuration.titleSongResource )
        .build();
        assetSystem.loadAsset( GameService.INTRO_SONG_KEY );
        soundSystem.getSoundBuilder()
            .set( Sound.ASSET_ID, assetSystem.getAssetTypeKey( GameService.INTRO_SONG_KEY ).id )
            .set( Sound.VOLUME, 10 )
            .set( Sound.LOOPING, true )
            .set( Sound.NAME, GameService.TITLE_SONG_SOUND_NAME )
        .build();
    }

}
