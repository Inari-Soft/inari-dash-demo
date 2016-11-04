package com.inari.dash.game.cave;

import java.util.BitSet;

import com.inari.commons.geom.PositionF;
import com.inari.commons.geom.Rectangle;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.audio.AudioSystemEvent;
import com.inari.firefly.audio.AudioSystemEvent.Type;
import com.inari.firefly.graphics.sprite.ESprite;
import com.inari.firefly.graphics.sprite.SpriteAsset;
import com.inari.firefly.graphics.view.View;
import com.inari.firefly.graphics.view.ViewSystem;
import com.inari.firefly.scene.Scene;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FireFlyApp;
import com.inari.firefly.system.external.FFTimer.UpdateScheduler;

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
            String assetName = CaveSystem.INTRO_TILE_SPRITE_NAME + i;
            assetSystem.disposeAsset( assetName );
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
            String assetName = CaveSystem.INTRO_TILE_SPRITE_NAME + i;
            assetSystem.loadAsset( assetName );
            spriteData[ i ] = assetSystem.getAssetAs( assetName, SpriteAsset.class ).getSpriteId();
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

        context.notify( new AudioSystemEvent( CaveSystem.CaveSoundKey.COVER.name(), Type.PLAY_SOUND ) );
    }

    @Override
    public void update( long tick ) {
        int removed = 0;
        while ( removed < 8 && !introTiles.isEmpty() ) {
            int nextIndex = FireFlyApp.RANDOM.nextInt( size );
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
        PositionF worldPosition = caveView.getWorldPosition();
        for ( int y = 0; y < height; y++ ) {
            for ( int x = 0; x < width; x++ ) {
                if ( introTiles.get( x * y ) ) {
                    context.getGraphics().renderSprite( tmpSprite, x * 16 + worldPosition.x , y * 16 + worldPosition.y );
                }
            }
        }
    }

}
