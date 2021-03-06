package com.inari.dash.game.cave.unit.stone;

import java.util.Map;

import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Rectangle;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.CaveSystem.SoundChannel;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.Unit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.audio.Sound;
import com.inari.firefly.audio.SoundAsset;
import com.inari.firefly.composite.sprite.AnimatedSpriteData;
import com.inari.firefly.composite.sprite.AnimatedTile;
import com.inari.firefly.control.Controller;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.entity.prefab.EntityPrefab;
import com.inari.firefly.entity.prefab.EntityPrefabSystem;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.utils.Disposable;

public final class Diamond extends Unit {

    private int prefabId;
    private int[] soundIds;
    private int soundIndex = 0;
    
    protected Diamond( int id ) {
        super( id );
    }
    
    @Override
    public final UnitType type() {
        return UnitType.DIAMOND;
    }

    @Override
    public final void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( "d", type() );
    }
    
    @Override
    public final Disposable load( FFContext context ) {
        
        soundIds = new int[ 8 ];
        for ( int i = 0; i < 8; i++ ) {
            String name = UnitType.DIAMOND.name() + ( i + 1 );
            context.getComponentBuilder( Asset.TYPE_KEY )
                .set( SoundAsset.NAME, name )
                .set( SoundAsset.RESOURCE_NAME, "original/sound/" + name + ".wav" )
                .set( SoundAsset.STREAMING, false )
            .activate( SoundAsset.class );

            soundIds[ i ] = context.getComponentBuilder( Sound.TYPE_KEY )
                .set( Sound.NAME, name )
                .set( Sound.SOUND_ASSET_NAME, name )
                .set( Sound.LOOPING, false )
                .set( Sound.CHANNEL, SoundChannel.DIAMOND.ordinal() )
            .build();
        }
        
        float updateRate = getUpdateRate();
        AnimatedSpriteData[] animationData = AnimatedSpriteData.create( 
            80 - (int) updateRate * 4, 
            new Rectangle( 0, 10 * 32, 32, 32 ), 
            8, Direction.EAST 
        );
        int animationAssetId = context.getComponentBuilder( Asset.TYPE_KEY )
            .set( AnimatedTile.NAME, UnitType.DIAMOND.name() )
            .set( AnimatedTile.LOOPING, true )
            .set( AnimatedTile.UPDATE_RESOLUTION, updateRate )
            .set( AnimatedTile.TEXTURE_ASSET_NAME, CaveSystem.GAME_UNIT_TEXTURE_NAME )
            .add( AnimatedTile.ANIMATED_SPRITE_DATA, animationData )
        .activate( AnimatedTile.class );
        int animatioControllerId = context
            .getSystemComponent( Asset.TYPE_KEY, animationAssetId, AnimatedTile.class )
            .getAnimationControllerId();
        
        int controllerId = context.getComponentBuilder( Controller.TYPE_KEY )
            .set( EntityController.NAME, UnitType.DIAMOND.name() )
            .set( EntityController.UPDATE_RESOLUTION, updateRate )
        .build( DiamondController.class );
        
        prefabId = context.getComponentBuilder( EntityPrefab.TYPE_KEY )
            .add( EEntity.CONTROLLER_IDS, controllerId )
            .add( EEntity.CONTROLLER_IDS, animatioControllerId )
            .set( EntityPrefab.NAME, UnitType.DIAMOND.name() )
            .set( EntityPrefab.INITIAL_CREATE_NUMBER, 200 )
            .set( ETransform.VIEW_NAME, CaveSystem.CAVE_VIEW_NAME )
            .set( ETile.MULTI_POSITION, false )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.CHANGE_TO, UnitType.ROCK )
            .add( EEntity.ASPECTS, UnitAspect.DESTRUCTIBLE )
            .add( EEntity.ASPECTS, UnitAspect.STONE )
            .add( EEntity.ASPECTS, UnitAspect.ASLOPE )
            .add( EEntity.ASPECTS, UnitAspect.WALKABLE )
        .build();
        
        return this;
    }

    @Override
    public final void dispose( FFContext context ) {
        context.deleteSystemComponent( Controller.TYPE_KEY, type().name() );
        context.deleteSystemComponent( EntityPrefab.TYPE_KEY, type().name() );
        context.deleteSystemComponent( Asset.TYPE_KEY, type().name() );
        
        for ( int i = 0; i < 8; i++ ) {
            String name = UnitType.DIAMOND.name() + ( i + 1 );
            context.deleteSystemComponent( Sound.TYPE_KEY, name );
            context.deleteSystemComponent( Asset.TYPE_KEY, name );
        }
    }

    @Override
    public final int createOne( int xGridPos, int yGridPos, String type ) {
        int entityId = context.getSystem( EntityPrefabSystem.SYSTEM_KEY ).buildOne( prefabId );
        ETile tile = context.getEntityComponent( entityId, ETile.TYPE_KEY );
        tile.setGridXPos( xGridPos );
        tile.setGridYPos( yGridPos );
        context.activateEntity( entityId );
        return entityId;
    }

    @Override
    public final int getSoundId() {
        soundIndex++;
        if ( soundIndex >= soundIds.length ) {
            soundIndex = 0;
        }
        return soundIds[ soundIndex ];
    }

}
