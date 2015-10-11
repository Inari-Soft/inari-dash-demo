package com.inari.dash.game.selection;

import java.util.Collection;

import com.inari.commons.lang.TypedKey;
import com.inari.dash.game.GameInfo;
import com.inari.dash.game.GameService;
import com.inari.dash.game.io.GameInfos;
import com.inari.firefly.Disposable;
import com.inari.firefly.Loadable;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.control.Controller;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFContextInitiable;
import com.inari.firefly.system.FFInitException;
import com.inari.firefly.system.view.ViewSystem;
import com.inari.firefly.text.EText;
import com.inari.firefly.text.TextSystem;

public final class GameSelectionService implements FFContextInitiable, Loadable, Disposable {
    
    public static final TypedKey<GameSelectionService> CONTEXT_KEY = TypedKey.create( "GameSelectionService", GameSelectionService.class );
    
    private static final String GAME_SELECTION_CONTROLLER_NAME = "GAME_SELECTION_CONTROLLER";

    public static enum Mode {
        GAME_SELECTION,
        CAVE_SELECTION,
        EXIT
    }

    private AssetSystem assetSystem;
    private EntitySystem entitySystem;
    private TextSystem textSystem;
    private ControllerSystem controllerSystem;
    private ViewSystem viewSystem;
    private GameInfos gameInfosLoader;
    
    private Mode mode = Mode.GAME_SELECTION;
    private int selectedGameIndex = 0;
    private int selectedCave = 0;
    private boolean selected = false;
    
    private boolean loaded = false;
    
    private EText gameSelectionTitle;
    private EText gameSelection;
    private EText caveSelectionTitle;
    private EText caveSelection;
    private EText exit;
    
    GameSelectionService() {}
    
    @Override
    public void init( FFContext context ) throws FFInitException {
        assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );
        textSystem = context.getComponent( TextSystem.CONTEXT_KEY );
        controllerSystem = context.getComponent( ControllerSystem.CONTEXT_KEY );
        viewSystem = context.getComponent( ViewSystem.CONTEXT_KEY );
        
        gameInfosLoader = new GameInfos();
        gameInfosLoader.load( context );
    }
    
    @Override
    public final Disposable load( FFContext context ) {
        if ( loaded ) {
            return this;
        }

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
            .set( EText.FONT_ID, textSystem.getFontId( GameService.GAME_FONT_TEXTURE_KEY.name ) )
            .set( EText.TEXT, "%%%%%%%%%%%%%%%%%%%%%%%" )
        .buildAndNext()
            .set( ETransform.VIEW_ID, 0 )
            .set( ETransform.XPOSITION, 30 )
            .set( ETransform.YPOSITION, 500 )
            .set( EText.FONT_ID, textSystem.getFontId( GameService.GAME_FONT_TEXTURE_KEY.name ) )
            .set( EText.TEXT, "%%%%%%%%%%%%%%%%%%%%%%%" )
        .build();
        
        gameSelectionTitle = entitySystem.getComponent( 
            entitySystem.getEntityBuilderWithAutoActivation()
                .set( ETransform.VIEW_ID, 0 )
                .set( ETransform.XPOSITION, 50 )
                .set( ETransform.YPOSITION, 200 )
                .set( EText.FONT_ID, textSystem.getFontId( GameService.GAME_FONT_TEXTURE_KEY.name ) )
                .set( EText.TEXT, "GAME:" )
            .build().getId(),
            EText.class
        );
        gameSelection = entitySystem.getComponent( 
            entitySystem.getEntityBuilderWithAutoActivation()
                .set( ETransform.VIEW_ID, 0 )
                .set( ETransform.XPOSITION, 220 )
                .set( ETransform.YPOSITION, 200 )
                .set( EText.FONT_ID, textSystem.getFontId( GameService.GAME_FONT_TEXTURE_KEY.name ) )
                .set( EText.TEXT, "XXX" )
            .build().getId(),
            EText.class
        );
        caveSelectionTitle = entitySystem.getComponent( 
            entitySystem.getEntityBuilderWithAutoActivation()
                .set( ETransform.VIEW_ID, 0 )
                .set( ETransform.XPOSITION, 50 )
                .set( ETransform.YPOSITION, 300 )
                .set( EText.FONT_ID, textSystem.getFontId( GameService.GAME_FONT_TEXTURE_KEY.name ) )
                .set( EText.TEXT, "CAVE:" )
            .build().getId(),
            EText.class
        );
        caveSelection = entitySystem.getComponent( 
            entitySystem.getEntityBuilderWithAutoActivation()
                .set( ETransform.VIEW_ID, 0 )
                .set( ETransform.XPOSITION, 220 )
                .set( ETransform.YPOSITION, 300 )
                .set( EText.FONT_ID, textSystem.getFontId( GameService.GAME_FONT_TEXTURE_KEY.name ) )
                .set( EText.TEXT, "1" )
            .build().getId(),
            EText.class
        );
        exit = entitySystem.getComponent( 
            entitySystem.getEntityBuilderWithAutoActivation()
                .set( ETransform.VIEW_ID, 0 )
                .set( ETransform.XPOSITION, 50 )
                .set( ETransform.YPOSITION, 400 )
                .set( EText.FONT_ID, textSystem.getFontId( GameService.GAME_FONT_TEXTURE_KEY.name ) )
                .set( EText.TEXT, "EXIT" )
            .build().getId(),
            EText.class
        );
        
        GameSelectionController controller = controllerSystem.getControllerBuilder( GameSelectionController.class )
            .set( Controller.NAME, GAME_SELECTION_CONTROLLER_NAME )
        .build();
        viewSystem.getView( 0 ).setControllerId( controller.getId() );
        
        update();
        
        loaded = true;
        return this;
    }
    
    public final Collection<GameInfo> getGameInfos() {
        return gameInfosLoader.getGameInfos();
    }
    
    public final GameInfo getSelectedGame() {
        return gameInfosLoader.getGameInfos().get( selectedGameIndex );
    }

    public final int getSelectedCave() {
        return selectedCave;
    }
    
    public final boolean isSelected() {
        return selected;
    }

    public final Mode getMode() {
        return mode;
    }
    
    final void update() {
        gameSelectionTitle.setTintColor( ( mode == Mode.GAME_SELECTION )? GameService.YELLOW_FONT_COLOR : GameService.WHITE_FONT_COLOR );
        caveSelectionTitle.setTintColor( ( mode == Mode.CAVE_SELECTION )? GameService.YELLOW_FONT_COLOR : GameService.WHITE_FONT_COLOR );
        exit.setTintColor( ( mode == Mode.EXIT )? GameService.YELLOW_FONT_COLOR : GameService.WHITE_FONT_COLOR );
        
        gameSelection.setText( getSelectedGame().getName() );
        caveSelection.setText( String.valueOf( getSelectedCave() ) );
    }

    final void select() {
        selected = true;
    }
    
    final void nextSelectionMode() {
        switch ( mode ) {
            case GAME_SELECTION : {
                mode = Mode.CAVE_SELECTION;
                break;
            }
            case CAVE_SELECTION : {
                mode = Mode.EXIT;
                break;
            }
            case EXIT : {
                mode = Mode.GAME_SELECTION;
                break;
            }
        }
    }
    
    final void peviousSelectionMode() {
        switch ( mode ) {
            case GAME_SELECTION : {
                mode = Mode.EXIT;
                break;
            }
            case CAVE_SELECTION : {
                mode = Mode.GAME_SELECTION;
                break;
            }
            case EXIT : {
                mode = Mode.CAVE_SELECTION;
                break;
            }
        }
    }
    
    final void nextGameSelection() {
        selectedGameIndex++;
        int size = getGameInfos().size();
        if ( selectedGameIndex >= size ) {
            selectedGameIndex = size - 1;
        }
        selectedCave = 0;
    }
    
    final void previousGameSelection() {
        selectedGameIndex--;
        if ( selectedGameIndex < 0 ) {
            selectedGameIndex = 0;
        }
        selectedCave = 0;
    }
    
    final void nextCaveSelection() {
        selectedCave++;
        if ( selectedCave >= getSelectedGame().getCaves() ) {
            selectedCave = getSelectedGame().getCaves() - 1;
        }
    }
    
    final void previousCaveSelection() {
        selectedCave--;
        if ( selectedCave < 0 ) {
            selectedCave = 0;
        }
    }

    @Override
    public final void dispose( FFContext context ) {
        if ( !loaded ) {
            return;
        }
        
        entitySystem.deleteAll();
        controllerSystem.deleteController( GAME_SELECTION_CONTROLLER_NAME );

        gameSelectionTitle = null;
        gameSelection = null;
        caveSelectionTitle = null;
        caveSelection = null;
        exit = null;
        textSystem.clear();
        
        mode = Mode.GAME_SELECTION;
        selectedGameIndex = 0;
        selectedCave = 0;
        selected = false;
    }

}
