package com.inari.dash.game.cave;

import java.util.BitSet;

import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.firefly.asset.AssetNameKey;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.scene.Scene;
import com.inari.firefly.sound.event.SoundEvent;
import com.inari.firefly.sound.event.SoundEvent.Type;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFTimer.UpdateScheduler;
import com.inari.firefly.system.FireFly;
import com.inari.firefly.system.view.View;
import com.inari.firefly.system.view.ViewSystem;

public class CaveInitScene extends Scene {
    
    private int width;
    private int height;
    private int size;
    
    private UpdateScheduler animationTimer;
    private BitSet introTiles;
    
    private ESprite tmpSprite = new ESprite();
    private int[] spriteData = new int[ 4 ];
    
    private FFContext context;
    private View caveView;

    protected CaveInitScene( int id ) {
        super( id );
    }

    @Override
    public void dispose( FFContext context ) {
        AssetSystem assetSystem = context.getSystem( AssetSystem.SYSTEM_KEY );
        for ( int i = 0; i < 3; i++ ) {
            AssetNameKey assetKey = new AssetNameKey( CaveSystem.GAME_UNIT_TEXTURE_KEY.group, CaveSystem.INTRO_TILE_SPRITE_NAME + i );
            assetSystem.disposeAsset( assetKey );
        }
    }

    @Override
    public void run( FFContext context ) {
        super.run( context );
        this.context = context;

        ViewSystem viewSystem = context.getSystem( ViewSystem.SYSTEM_KEY );
        CaveData caveData = context.getSystem( CaveSystem.SYSTEM_KEY ).getCaveData();
        AssetSystem assetSystem = context.getSystem( AssetSystem.SYSTEM_KEY );
        for ( int i = 0; i < 3; i++ ) {
            AssetNameKey assetKey = new AssetNameKey( CaveSystem.GAME_UNIT_TEXTURE_KEY.group, CaveSystem.INTRO_TILE_SPRITE_NAME + i );
            spriteData[ i ] = assetSystem.getAssetId( assetKey );
            assetSystem.loadAsset( assetKey );
        }
        spriteData[ 3 ] = 0;
        tmpSprite.setSpriteId( spriteData[ spriteData[ 3 ] ] );
        
        animationTimer = context.getTimer().createUpdateScheduler( 10 );

        caveView  = viewSystem.getView( CaveSystem.CAVE_VIEW_NAME );
        Rectangle viewBounds = caveView.getBounds();
        
        width = caveData.getCaveWidth() * 2;
        height = caveData.getCaveHeight() * 2;
        if ( viewBounds.width / 16 < width ) {
            width = viewBounds.width / 16 + 2;
        } 
        if ( viewBounds.height / 16 < height ) {
            height = viewBounds.height / 16 + 2;
        }
        
        size = width * height;
        introTiles = new BitSet( size );
        for ( int i = 0; i < size; i++ ) {
            introTiles.set( i );
        }

        context.notify( new SoundEvent( CaveSystem.CaveSoundKey.COVER.id, Type.PLAY_SOUND ) );
    }

    @Override
    public void update( long tick ) {
        int removed = 0;
        while ( removed < 8 && !introTiles.isEmpty() ) {
            int nextIndex = FireFly.RANDOM.nextInt( size );
            if ( introTiles.get( nextIndex ) ) {
                introTiles.flip( nextIndex );
                removed++;
            }
        }
        
        if ( animationTimer.needsUpdate() ) {
            spriteData[ 3 ]++;
            if ( spriteData[ 3 ] >= 3 ) {
                spriteData[ 3 ] = 0;
            }
            tmpSprite.setSpriteId( spriteData[ spriteData[ 3 ] ] );
        }
    }

    @Override
    public void render() {
        Position worldPosition = caveView.getWorldPosition();
        for ( int y = 0; y < height; y++ ) {
            for ( int x = 0; x < width; x++ ) {
                if ( introTiles.get( x * y ) ) {
                    context.getSystemInterface().renderSprite( tmpSprite, x * 16 + worldPosition.x , y * 16 + worldPosition.y );
                }
            }
        }
    }

}
