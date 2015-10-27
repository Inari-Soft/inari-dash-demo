package com.inari.dash.game.cave.unit.misc;

import java.util.Collection;
import java.util.Map;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.AspectSetBuilder;
import com.inari.dash.game.cave.CaveService;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitHandle;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.animation.sprite.SpriteAnimationBuilder;
import com.inari.firefly.animation.sprite.SpriteAnimationBuilder.SpriteAnimationHandler;
import com.inari.firefly.asset.AssetNameKey;
import com.inari.firefly.asset.AssetTypeKey;
import com.inari.firefly.control.EController;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;

public final class ExitHandle extends UnitHandle {
    
    public enum State {
        CLOSED,
        OPEN
    }
    
    public static final String EXIT_NAME = "exit";
    public static final AssetNameKey ROCKFORD_SPRITE_ASSET_KEY = new AssetNameKey( CaveService.GAME_UNIT_TEXTURE_KEY.group, EXIT_NAME );
    
    private SpriteAnimationHandler spriteAnimationHandler;
    private int exitEntityId;
    private int firstSpriteId;

    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );
        
        spriteAnimationHandler = new SpriteAnimationBuilder( context )
            .setGroup( CaveService.GAME_UNIT_TEXTURE_KEY.group )
            .setLooping( true )
            .setNamePrefix( EXIT_NAME )
            .setTextureAssetKey( CaveService.GAME_UNIT_TEXTURE_KEY )
            .setStatedAnimationType( ExitAnimationController.class )
            .setState( State.CLOSED.ordinal() )
            .addSpritesToAnimation( 0, new Rectangle( 32, 6 * 32, 32, 32 ), 1, true )
            .setState( State.OPEN.ordinal() )
            .addSpritesToAnimation( 0, new Rectangle( 32, 6 * 32, 32, 32 ), 2, true )
        .build();
        
        Collection<AssetTypeKey> allSpriteAssetKeys = spriteAnimationHandler.getAllSpriteAssetKeys();
        caveAssetsToReload.addAll( allSpriteAssetKeys );
        firstSpriteId = allSpriteAssetKeys.iterator().next().id;
        
        initialized = true;
    }

    @Override
    public final void loadCaveData( FFContext context ) {
        super.loadCaveData( context );
        
        float updateRate = caveService.getUpdateRate();
        spriteAnimationHandler.setFrameTime( State.CLOSED.ordinal(), Integer.MAX_VALUE );
        spriteAnimationHandler.setFrameTime( State.OPEN.ordinal(), 400 - (int) updateRate * 4 );
    }

    @Override
    public void disposeCaveData( FFContext context ) {
        super.disposeCaveData( context );
        
        entitySystem.delete( exitEntityId );
    }

    @Override
    public final UnitType type() {
        return UnitType.EXIT;
    }

    @Override
    public final void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( "X", type() );
        bdcffMap.put( "H", type() );
    }

    @Override
    public final int createOne( int xGridPos, int yGridPos ) {
        exitEntityId = entitySystem.getEntityBuilderWithAutoActivation()
            .set( EController.CONTROLLER_IDS, new int[] { spriteAnimationHandler.getControllerId() } )
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveService.CAVE_VIEW_NAME ) )
            .set( ESprite.SPRITE_ID, firstSpriteId )
            .set( ETile.GRID_X_POSITION, xGridPos )
            .set( ETile.GRID_Y_POSITION, yGridPos )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.ASPECTS, AspectSetBuilder.create() )
        .build().getId();
        return exitEntityId;
    }
    
    @Override
    public final int getEntityId() {
        return exitEntityId;
    }

    @Override
    public final void dispose( FFContext context ) {
        spriteAnimationHandler.dispose( context );
    }
}
