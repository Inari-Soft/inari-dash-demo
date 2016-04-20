package com.inari.dash.game.tasks;

import com.inari.dash.game.GameSelectionController;
import com.inari.dash.game.GameSystem;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.control.Controller;
import com.inari.firefly.control.task.Task;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.graphics.sprite.ESprite;
import com.inari.firefly.graphics.text.EText;
import com.inari.firefly.graphics.text.FontAsset;

public final class LoadGameSelection extends Task {

    protected LoadGameSelection( int id ) {
        super( id );
    }

    @Override
    public final void runTask() {
        EntitySystem entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        AssetSystem assetSystem = context.getSystem( AssetSystem.SYSTEM_KEY );

        FontAsset fontAsset = assetSystem.getAssetAs( GameSystem.GAME_FONT_TEXTURE_NAME, FontAsset.class );
        int fontId = fontAsset.index();
        int rockfordTitleId = fontAsset.getSpriteId( '&' );
        entitySystem.getEntityBuilder()
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.XPOSITION, 320 )
            .set( ETransform.YPOSITION, 50 )
            .set( ETransform.SCALE_X, 4 )
            .set( ETransform.SCALE_Y, 4 )
            .set( ESprite.SPRITE_ID, rockfordTitleId )
        .activateAndNext()
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.XPOSITION, 30 )
            .set( ETransform.YPOSITION, 120 )
            .set( EText.FONT_ASSET_ID, fontId )
            .set( EText.TEXT, "%%%%%%%%%%%%%%%%%%%%%%%" )
        .activateAndNext()
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.XPOSITION, 30 )
            .set( ETransform.YPOSITION, 500 )
            .set( EText.FONT_ASSET_ID, fontId )
            .set( EText.TEXT, "%%%%%%%%%%%%%%%%%%%%%%%" )
        .activateAndNext()
            .set( EEntity.ENTITY_NAME, GameSystem.ENTITY_NAME_GAME_SELECTION_TITLE )
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.XPOSITION, 50 )
            .set( ETransform.YPOSITION, 200 )
            .set( EText.FONT_ASSET_ID, fontId )
            .set( EText.TEXT, "GAME:" )
        .activateAndNext()
            .set( EEntity.ENTITY_NAME, GameSystem.ENTITY_NAME_GAME_SELECTION )
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.XPOSITION, 220 )
            .set( ETransform.YPOSITION, 200 )
            .set( EText.FONT_ASSET_ID, fontId )
            .set( EText.TEXT, "XXX" )
        .activateAndNext()
            .set( EEntity.ENTITY_NAME, GameSystem.ENTITY_NAME_CAVE_SELECTION_TITLE )
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.XPOSITION, 50 )
            .set( ETransform.YPOSITION, 300 )
            .set( EText.FONT_ASSET_ID, fontId )
            .set( EText.TEXT, "CAVE:" )
        .activateAndNext()
            .set( EEntity.ENTITY_NAME, GameSystem.ENTITY_NAME_CAVE_SELECTION )
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.XPOSITION, 220 )
            .set( ETransform.YPOSITION, 300 )
            .set( EText.FONT_ASSET_ID, fontId )
            .set( EText.TEXT, "1" )
        .activateAndNext()
            .set( EEntity.ENTITY_NAME, GameSystem.ENTITY_NAME_EXIT_TITLE )
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.XPOSITION, 50 )
            .set( ETransform.YPOSITION, 400 )
            .set( EText.FONT_ASSET_ID, fontId )
            .set( EText.TEXT, "EXIT" )
        .activate();
        
        context.getComponentBuilder( Controller.TYPE_KEY )
            .set( Controller.NAME, GameSystem.GAME_SELECTION_CONTROLLER_NAME )
            .set( Controller.UPDATE_RESOLUTION, 5f )
        .build( GameSelectionController.class  );
    }

}
