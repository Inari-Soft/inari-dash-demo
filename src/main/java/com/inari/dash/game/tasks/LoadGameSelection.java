package com.inari.dash.game.tasks;

import com.inari.dash.game.GameSelectionController;
import com.inari.dash.game.GameSystem;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.control.Controller;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.text.EText;
import com.inari.firefly.renderer.text.TextSystem;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;

public final class LoadGameSelection extends Task {

    protected LoadGameSelection( int id ) {
        super( id );
    }

    @Override
    public final void run( FFContext context ) {
        EntitySystem entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        AssetSystem assetSystem = context.getSystem( AssetSystem.SYSTEM_KEY );
        TextSystem textSystem = context.getSystem( TextSystem.SYSTEM_KEY );
        
        int fontId = textSystem.getFontId( GameSystem.GAME_FONT_TEXTURE_KEY.name );
        int rockfordTitleId = assetSystem.getAssetId( GameSystem.GAME_FONT_TEXTURE_KEY.group, GameSystem.GAME_FONT_TEXTURE_KEY.name + "_1_2" );
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
            .set( EText.FONT_ID, fontId )
            .set( EText.TEXT_STRING, "%%%%%%%%%%%%%%%%%%%%%%%" )
        .activateAndNext()
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.XPOSITION, 30 )
            .set( ETransform.YPOSITION, 500 )
            .set( EText.FONT_ID, fontId )
            .set( EText.TEXT_STRING, "%%%%%%%%%%%%%%%%%%%%%%%" )
        .activateAndNext()
            .set( EEntity.ENTITY_NAME, GameSystem.ENTITY_NAME_GAME_SELECTION_TITLE )
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.XPOSITION, 50 )
            .set( ETransform.YPOSITION, 200 )
            .set( EText.FONT_ID, fontId )
            .set( EText.TEXT_STRING, "GAME:" )
        .activateAndNext()
            .set( EEntity.ENTITY_NAME, GameSystem.ENTITY_NAME_GAME_SELECTION )
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.XPOSITION, 220 )
            .set( ETransform.YPOSITION, 200 )
            .set( EText.FONT_ID, fontId )
            .set( EText.TEXT_STRING, "XXX" )
        .activateAndNext()
            .set( EEntity.ENTITY_NAME, GameSystem.ENTITY_NAME_CAVE_SELECTION_TITLE )
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.XPOSITION, 50 )
            .set( ETransform.YPOSITION, 300 )
            .set( EText.FONT_ID, fontId )
            .set( EText.TEXT_STRING, "CAVE:" )
        .activateAndNext()
            .set( EEntity.ENTITY_NAME, GameSystem.ENTITY_NAME_CAVE_SELECTION )
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.XPOSITION, 220 )
            .set( ETransform.YPOSITION, 300 )
            .set( EText.FONT_ID, fontId )
            .set( EText.TEXT_STRING, "1" )
        .activateAndNext()
            .set( EEntity.ENTITY_NAME, GameSystem.ENTITY_NAME_EXIT_TITLE )
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.XPOSITION, 50 )
            .set( ETransform.YPOSITION, 400 )
            .set( EText.FONT_ID, fontId )
            .set( EText.TEXT_STRING, "EXIT" )
        .activate();
        
        context.getComponentBuilder( Controller.TYPE_KEY )
            .set( Controller.NAME, GameSystem.GAME_SELECTION_CONTROLLER_NAME )
            .set( Controller.UPDATE_RESOLUTION, 5f )
        .build( GameSelectionController.class  );
    }

}
