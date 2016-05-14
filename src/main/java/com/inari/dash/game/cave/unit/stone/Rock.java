package com.inari.dash.game.cave.unit.stone;

import java.util.Map;

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
import com.inari.firefly.control.Controller;
import com.inari.firefly.entity.EEntity;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.entity.prefab.EntityPrefab;
import com.inari.firefly.entity.prefab.EntityPrefabSystem;
import com.inari.firefly.graphics.sprite.SpriteAsset;
import com.inari.firefly.graphics.tile.ETile;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.utils.Disposable;

public final class Rock extends Unit {

    private int prefabId;
    private int soundId;
    
    protected Rock( int id ) {
        super( id );
    }
    
    @Override
    public final UnitType type() {
        return UnitType.ROCK;
    }
    
    @Override
    public final int getSoundId() {
        return soundId;
    }

    @Override
    public final void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( "r", type() );
    }

    @Override
    public final Disposable load( FFContext context ) {
        String soundName = UnitType.ROCK.name() + "_sound";
        context.getComponentBuilder( Asset.TYPE_KEY )
            .set( SoundAsset.NAME, soundName )
            .set( SoundAsset.RESOURCE_NAME, "original/sound/stone.wav" )
            .set( SoundAsset.STREAMING, false )
        .activate( SoundAsset.class );
        
        soundId = context.getComponentBuilder( Sound.TYPE_KEY )
            .set( Sound.NAME, soundName )
            .set( Sound.SOUND_ASSET_NAME, soundName )
            .set( Sound.LOOPING, false )
            .set( Sound.CHANNEL, SoundChannel.ROCK.ordinal() )
        .build();
        
        context.getComponentBuilder( Asset.TYPE_KEY )
            .set( SpriteAsset.NAME, UnitType.ROCK.name() )
            .set( SpriteAsset.TEXTURE_ASSET_NAME, CaveSystem.GAME_UNIT_TEXTURE_NAME )
            .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 0, 7 * 32, 32, 32 ) )
        .activate( SpriteAsset.class );
        
        
        int controllerId = context.getComponentBuilder( Controller.TYPE_KEY )
            .set( EntityController.NAME, UnitType.ROCK.name() )
            .set( EntityController.UPDATE_RESOLUTION, getUpdateRate() )
        .build( RockController.class );
        
        prefabId = context.getComponentBuilder( EntityPrefab.TYPE_KEY )
            .add( EEntity.CONTROLLER_IDS, controllerId )
            .set( EntityPrefab.NAME, UnitType.ROCK.name() )
            .set( EntityPrefab.INITIAL_CREATE_NUMBER, 200 )
            .set( ETransform.VIEW_NAME, CaveSystem.CAVE_VIEW_NAME )
            .set( ETile.SPRITE_ASSET_NAME, UnitType.ROCK.name() )
            .set( ETile.MULTI_POSITION, false )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.CHANGE_TO, UnitType.DIAMOND )
            .add( EEntity.ASPECTS, UnitAspect.DESTRUCTIBLE )
            .add( EEntity.ASPECTS, UnitAspect.STONE )
            .add( EEntity.ASPECTS, UnitAspect.ASLOPE )
        .build();
        
        return this;
    }

    @Override
    public final void dispose( FFContext context ) {
        context.deleteSystemComponent( Controller.TYPE_KEY, type().name() );
        context.deleteSystemComponent( EntityPrefab.TYPE_KEY, type().name() );
        context.deleteSystemComponent( Asset.TYPE_KEY, type().name() );
        
        String soundName = UnitType.ROCK.name() + "_sound";
        context.deleteSystemComponent( Sound.TYPE_KEY, soundName );
        context.deleteSystemComponent( Asset.TYPE_KEY, soundName );
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

}
