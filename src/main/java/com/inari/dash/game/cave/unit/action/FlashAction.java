package com.inari.dash.game.cave.unit.action;

import com.inari.commons.geom.Rectangle;
import com.inari.dash.game.cave.CaveService;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.asset.AssetNameKey;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.sprite.SpriteAsset;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.UpdateEvent;
import com.inari.firefly.system.UpdateEventListener;

public final class FlashAction extends UnitAction {
    
    private static final String FLASH_NAME = "flash";
    private static final AssetNameKey FLASH_SPRITE_ASSET = new AssetNameKey( FLASH_NAME, CaveService.GAME_UNIT_TEXTURE_KEY.group );
    
    private final FFContext context;

    protected FlashAction( int id, FFContext context ) {
        super( id );
        this.context = context;
    }

    @Override
    public final void performAction( int entityId ) {
        context.getComponent( FFContext.EVENT_DISPATCHER )
            .register( UpdateEvent.class, new FlashAnimation( context ) );
    }

    private final class FlashAnimation implements UpdateEventListener {
        
        private int flashSpriteId;
        private int spaceSpriteId;
        private int tick = 0;

        private FlashAnimation( FFContext context ) {
            AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
            flashSpriteId = assetSystem.getAssetBuilderWithAutoLoad( SpriteAsset.class )
                .set( SpriteAsset.NAME, FLASH_SPRITE_ASSET.name )
                .set( SpriteAsset.ASSET_GROUP, FLASH_SPRITE_ASSET.group )
                .set( SpriteAsset.TEXTURE_ID, assetSystem.getAssetId( CaveService.GAME_UNIT_TEXTURE_KEY ) )
                .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 4 * 32, 0, 32, 32 ) )
            .build().getId();
        }

        @Override
        public void update( UpdateEvent event ) {
            int spaceEntityId = UnitType.SPACE.handler.getEntityId();
            if ( tick == 0 ) {
                ESprite spaceSprite = entitySystem.getComponent( spaceEntityId, ESprite.class );
                spaceSpriteId = spaceSprite.getSpriteId();
                spaceSprite.setSpriteId( flashSpriteId );

            } 
            
            tick++;

            if ( tick > 6 ) {
                ESprite spaceSprite = entitySystem.getComponent( spaceEntityId, ESprite.class );
                spaceSprite.setSpriteId( spaceSpriteId );
                // self remove
                AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
                assetSystem.deleteAsset( FLASH_SPRITE_ASSET );
                context.getComponent( FFContext.EVENT_DISPATCHER )
                    .unregister( UpdateEvent.class, this );
            }
        }
    }

}
