package com.inari.dash.game.cave.unit.misc;

import java.util.Map;

import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.AspectsBuilder;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.dash.game.cave.CaveSystem.SoundChannel;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.Unit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.asset.Asset;
import com.inari.firefly.audio.AudioSystemEvent;
import com.inari.firefly.audio.AudioSystemEvent.Type;
import com.inari.firefly.audio.Sound;
import com.inari.firefly.audio.SoundAsset;
import com.inari.firefly.composite.sprite.AnimatedSpriteData;
import com.inari.firefly.composite.sprite.AnimatedTile;
import com.inari.firefly.control.Controller;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.entity.EntitySystem.Entity;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.utils.Disposable;

public final class Amoeba extends Unit {

    private int amoebaEntityId;
    private int soundId;
    
    protected Amoeba( int id ) {
        super( id );
    }
    
    @Override
    public final UnitType type() {
        return UnitType.AMOEBA;
    }

    @Override
    public final void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( "a", type() );
    }

    @Override
    public final int getSoundId() {
        return soundId;
    }


    @Override
    public final Disposable load( FFContext context ) {
        
        int soundAssetId = context.getComponentBuilder( Asset.TYPE_KEY )
            .set( SoundAsset.NAME, UnitType.AMOEBA.name() + "_sound" )
            .set( SoundAsset.RESOURCE_NAME, "original/sound/amoeba.wav" )
            .set( SoundAsset.STREAMING, false )
        .activate( SoundAsset.class );
        
        soundId = context.getComponentBuilder( Sound.TYPE_KEY )
            .set( Sound.NAME, UnitType.AMOEBA.name() + "_sound" )
            .set( Sound.SOUND_ASSET_ID, soundAssetId )
            .set( Sound.CHANNEL, SoundChannel.AMOEBA.ordinal() )
            .set( Sound.LOOPING, true )
        .build();
        
        float updateRate = getUpdateRate();
        AnimatedSpriteData[] animationData = AnimatedSpriteData.create( 
            80 - (int) updateRate * 4, 
            new Rectangle( 0, 8 * 32, 32, 32 ), 
            8, Direction.EAST 
        );
        int animationAssetId = context.getComponentBuilder( Asset.TYPE_KEY )
            .set( AnimatedTile.NAME, UnitType.AMOEBA.name() )
            .set( AnimatedTile.LOOPING, true )
            .set( AnimatedTile.UPDATE_RESOLUTION, updateRate )
            .set( AnimatedTile.TEXTURE_ASSET_NAME, CaveSystem.GAME_UNIT_TEXTURE_NAME )
            .add( AnimatedTile.ANIMATED_SPRITE_DATA, animationData )
        .activate( AnimatedTile.class );
        int animatioControllerId = context
            .getSystemComponent( Asset.TYPE_KEY, animationAssetId, AnimatedTile.class )
            .getAnimationControllerId();
        
        int controllerId = context.getComponentBuilder( Controller.TYPE_KEY )
            .set( EntityController.NAME, UnitType.AMOEBA.name() )
            .set( EntityController.UPDATE_RESOLUTION, updateRate )
        .build( AmoebaController.class );
        
        amoebaEntityId = context.getComponentBuilder( Entity.ENTITY_TYPE_KEY )
            .add( EEntity.CONTROLLER_IDS, controllerId )
            .add( EEntity.CONTROLLER_IDS, animatioControllerId )
            .set( ETransform.VIEW_NAME, CaveSystem.CAVE_VIEW_NAME )
            .set( ETile.MULTI_POSITION, true )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.ASPECTS, AspectsBuilder.create( 
                UnitAspect.DESTRUCTIBLE
            ) )
        .activate();
        
        return this;
    }

    @Override
    public final void dispose( FFContext context ) {

        context.deleteSystemComponent( Controller.TYPE_KEY, UnitType.AMOEBA.name() );
        context.deleteEntity( amoebaEntityId );
        context.deleteSystemComponent( Asset.TYPE_KEY, UnitType.AMOEBA.name() );
        
        context.notify( new AudioSystemEvent( soundId, Type.STOP_PLAYING ) );
        context.deleteSystemComponent( Sound.TYPE_KEY, soundId );
        context.deleteSystemComponent( Asset.TYPE_KEY, UnitType.AMOEBA.name() + "_sound" );
    }

    

    @Override
    public final int createOne( int xGridPos, int yGridPos, String type ) {
        ETile tile = context.getEntityComponent( amoebaEntityId, ETile.TYPE_KEY );
        if ( tile.getGridPositions().isEmpty() ) {
            EUnit unit = context.getEntityComponent( amoebaEntityId, EUnit.TYPE_KEY );
            unit.setAspect( UnitAspect.ACTIVE );
        }
        tile.getGridPositions().add( new Position( xGridPos, yGridPos ) );
        context.getSystem( CaveSystem.SYSTEM_KEY )
            .setEntityId( amoebaEntityId, xGridPos, yGridPos );
        
        return amoebaEntityId;
    }
    

}
