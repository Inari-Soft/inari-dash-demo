package com.inari.dash.game.cave.unit.action;

import com.inari.commons.geom.Rectangle;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.graphics.sprite.ESprite;
import com.inari.firefly.graphics.sprite.SpriteAsset;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.UpdateEvent;
import com.inari.firefly.system.UpdateEventListener;

public final class FlashAction extends UnitAction {
    
    private static final String FLASH_SPRITE_ASSET_NAME = CaveSystem.GAME_UNIT_TEXTURE_NAME + "_flash";
    
    private final FFContext context;

    protected FlashAction( int id, FFContext context ) {
        super( id );
        this.context = context;
    }

    @Override
    public final void performAction( int entityId ) {
        context.registerListener( UpdateEvent.class, new FlashAnimation( context ) );
    }

    private final class FlashAnimation implements UpdateEventListener {
        
        private int flashSpriteId;
        private int spaceSpriteId;
        private int tick = 0;

        private FlashAnimation( FFContext context ) {
            AssetSystem assetSystem = context.getSystem( AssetSystem.SYSTEM_KEY );
            assetSystem.getAssetBuilder()
                .set( SpriteAsset.NAME, FLASH_SPRITE_ASSET_NAME )
                .set( SpriteAsset.TEXTURE_ASSET_ID, assetSystem.getAssetId( CaveSystem.GAME_UNIT_TEXTURE_NAME ) )
                .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 4 * 32, 0, 32, 32 ) )
            .activate( SpriteAsset.class );
            flashSpriteId = assetSystem.getAssetInstanceId( FLASH_SPRITE_ASSET_NAME );
        }

        @Override
        public void update( UpdateEvent event ) {
            int spaceEntityId = UnitType.SPACE.handler.getEntityId();
            if ( tick == 0 ) {
                ESprite spaceSprite = entitySystem.getComponent( spaceEntityId, ESprite.TYPE_KEY );
                spaceSpriteId = spaceSprite.getSpriteId();
                spaceSprite.setSpriteId( flashSpriteId );

            } 
            
            tick++;

            if ( tick > 6 ) {
                ESprite spaceSprite = entitySystem.getComponent( spaceEntityId, ESprite.TYPE_KEY );
                spaceSprite.setSpriteId( spaceSpriteId );
                // self remove
                AssetSystem assetSystem = context.getSystem( AssetSystem.SYSTEM_KEY );
                assetSystem.deleteAsset( FLASH_SPRITE_ASSET_NAME );
                context.disposeListener( UpdateEvent.class, this );
            }
        }
    }

}
