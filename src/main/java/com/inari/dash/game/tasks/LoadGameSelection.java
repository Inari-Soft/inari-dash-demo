package com.inari.dash.game.tasks;

import com.inari.dash.game.GameSelectionController;
import com.inari.dash.game.GameService;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.control.Controller;
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.Entity;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;
import com.inari.firefly.text.EText;
import com.inari.firefly.text.TextSystem;

public final class LoadGameSelection extends Task {

    protected LoadGameSelection( int id ) {
        super( id );
    }

    @Override
    public final void run( FFContext context ) {
        EntitySystem entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );
        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        TextSystem textSystem = context.getComponent( TextSystem.CONTEXT_KEY );
        ControllerSystem controllerSystem = context.getComponent( ControllerSystem.CONTEXT_KEY );
        
        int fontId = textSystem.getFontId( GameService.GAME_FONT_TEXTURE_KEY.name );
        int rockfordTitleId = assetSystem.getAssetId( GameService.GAME_FONT_TEXTURE_KEY.group, GameService.GAME_FONT_TEXTURE_KEY.name + "_1_2" );
        entitySystem.getEntityBuilderWithAutoActivation()
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.XPOSITION, 320 )
            .set( ETransform.YPOSITION, 50 )
            .set( ETransform.SCALE_X, 4 )
            .set( ETransform.SCALE_Y, 4 )
            .set( ESprite.SPRITE_ID, rockfordTitleId )
        .buildAndNext()
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.XPOSITION, 30 )
            .set( ETransform.YPOSITION, 120 )
            .set( EText.FONT_ID, fontId )
            .set( EText.TEXT_STRING, "%%%%%%%%%%%%%%%%%%%%%%%" )
        .buildAndNext()
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.XPOSITION, 30 )
            .set( ETransform.YPOSITION, 500 )
            .set( EText.FONT_ID, fontId )
            .set( EText.TEXT_STRING, "%%%%%%%%%%%%%%%%%%%%%%%" )
        .buildAndNext()
            .set( Entity.NAME, GameService.ENTITY_NAME_GAME_SELECTION_TITLE )
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.XPOSITION, 50 )
            .set( ETransform.YPOSITION, 200 )
            .set( EText.FONT_ID, fontId )
            .set( EText.TEXT_STRING, "GAME:" )
        .buildAndNext()
            .set( Entity.NAME, GameService.ENTITY_NAME_GAME_SELECTION )
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.XPOSITION, 220 )
            .set( ETransform.YPOSITION, 200 )
            .set( EText.FONT_ID, fontId )
            .set( EText.TEXT_STRING, "XXX" )
        .buildAndNext()
            .set( Entity.NAME, GameService.ENTITY_NAME_CAVE_SELECTION_TITLE )
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.XPOSITION, 50 )
            .set( ETransform.YPOSITION, 300 )
            .set( EText.FONT_ID, fontId )
            .set( EText.TEXT_STRING, "CAVE:" )
        .buildAndNext()
            .set( Entity.NAME, GameService.ENTITY_NAME_CAVE_SELECTION )
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.XPOSITION, 220 )
            .set( ETransform.YPOSITION, 300 )
            .set( EText.FONT_ID, fontId )
            .set( EText.TEXT_STRING, "1" )
        .buildAndNext()
            .set( Entity.NAME, GameService.ENTITY_NAME_EXIT_TITLE )
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.XPOSITION, 50 )
            .set( ETransform.YPOSITION, 400 )
            .set( EText.FONT_ID, fontId )
            .set( EText.TEXT_STRING, "EXIT" )
        .build();
        
        controllerSystem.getControllerBuilder( GameSelectionController.class )
            .set( Controller.NAME, GameService.GAME_SELECTION_CONTROLLER_NAME )
        .build();
        
    }

}
