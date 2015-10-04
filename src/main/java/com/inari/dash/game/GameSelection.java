package com.inari.dash.game;

import com.inari.firefly.Disposable;
import com.inari.firefly.Loadable;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.text.EText;
import com.inari.firefly.text.TextSystem;

public final class GameSelection implements Loadable, Disposable {
    
    private boolean loaded = false;
    
    GameSelection() {}
    
    @Override
    public final Disposable load( FFContext context ) {
        if ( loaded ) {
            return this;
        }

        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        EntitySystem entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );
        TextSystem textSystem = context.getComponent( TextSystem.CONTEXT_KEY );
        
        int rockfordTitleId = assetSystem.getAssetId( GameService.ORIGINAL_FONT_TEXTURE_KEY.group, GameService.ORIGINAL_FONT_TEXTURE_KEY.name + "_1_2" );
        entitySystem.getEntityBuilderWithAutoActivation()
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.XPOSITION, 320 )
            .set( ETransform.YPOSITION, 50 )
            .set( ETransform.SCALE_X, 4 )
            .set( ETransform.SCALE_Y, 4 )
            .set( ESprite.SPRITE_ID, rockfordTitleId )
        .buildAndNext()
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.XPOSITION, 50 )
            .set( ETransform.YPOSITION, 120 )
            .set( EText.FONT_ID, textSystem.getFontId( GameService.ORIGINAL_FONT_TEXTURE_KEY.name ) )
            .set( EText.TEXT, "%%%%%%%%%%%%%%%%%%%%" )
        .build();
        
        
        
        
        return this;
    }

    @Override
    public final void dispose( FFContext context ) {
        if ( !loaded ) {
            return;
        }
    }


}
