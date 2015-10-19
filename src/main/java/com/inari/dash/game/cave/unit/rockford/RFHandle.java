package com.inari.dash.game.cave.unit.rockford;

import java.util.Collection;
import java.util.Map;

import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.AspectSetBuilder;
import com.inari.dash.game.cave.CaveService;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitHandle;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.dash.game.cave.unit.rockford.RFUnit.RFState;
import com.inari.firefly.animation.sprite.SpriteAnimationBuilder;
import com.inari.firefly.animation.sprite.SpriteAnimationBuilder.SpriteAnimationHandler;
import com.inari.firefly.asset.AssetNameKey;
import com.inari.firefly.asset.AssetTypeKey;
import com.inari.firefly.control.EController;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.sound.Sound;
import com.inari.firefly.sound.SoundAsset;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;

public final class RFHandle extends UnitHandle {
    
    public static final String ROCKFORD_NAME = "rockford";
    public static final AssetNameKey ROCKFORD_SPRITE_ASSET_KEY = new AssetNameKey( CaveService.GAME_UNIT_TEXTURE_KEY.group, ROCKFORD_NAME );
    public static final AssetNameKey ROCKFORD_SPACE_SOUND_ASSEET_KEY = new AssetNameKey( CaveService.CAVE_SOUND_GROUP_NAME, ROCKFORD_NAME + "_space" );
    public static final AssetNameKey ROCKFORD_SAND_SOUND_ASSEET_KEY = new AssetNameKey( CaveService.CAVE_SOUND_GROUP_NAME, ROCKFORD_NAME + "_sand" );
    public static final AssetNameKey ROCKFORD_COLLECT_SOUND_ASSEET_KEY = new AssetNameKey( CaveService.CAVE_SOUND_GROUP_NAME, ROCKFORD_NAME + "_collect" );
    
    private int controllerId;
    private SpriteAnimationHandler spriteAnimationHandler;
    private int firstSpriteId;
    
    @Override
    public final void init( FFContext context ) throws FFInitException {
        super.init( context );

        RFController controller = (RFController) controllerSystem.getComponentBuilder( RFController.class )
            .set( EntityController.NAME, ROCKFORD_NAME )
        .build();
        controllerId = controller.getId();
        
        createSounds( controller );
        
        spriteAnimationHandler = new SpriteAnimationBuilder( context )
            .setGroup( CaveService.GAME_UNIT_TEXTURE_KEY.group )
            .setLooping( true )
            .setNamePrefix( ROCKFORD_NAME )
            .setTextureAssetKey( CaveService.GAME_UNIT_TEXTURE_KEY )
            .setStatedAnimationType( RFSpriteAnimation.class )
            .setState( RFState.ENTERING.ordinal() )
            .addSpritesToAnimation( 0, new Rectangle( 32, 6 * 32, 32, 32 ), 2, true )
            .setState( RFState.APPEARING.ordinal() )
            .addSpritesToAnimation( 0, new Rectangle( 32, 0, 32, 32 ), 3, true )
            .setState( RFState.IDLE.ordinal() )
            .addSpritesToAnimation( 0, new Rectangle( 0, 0, 32, 32 ), 1, true )
            .setState( RFState.IDLE_BLINKING.ordinal() )
            .addSpritesToAnimation( 0, new Rectangle( 0, 32, 32, 32 ), 8, true )
            .setState( RFState.IDLE_FRETFUL.ordinal() )
            .addSpritesToAnimation( 0, new Rectangle( 0, 2 * 32, 32, 32 ), 8, true )
            .setState( RFState.LEFT.ordinal() )
            .addSpritesToAnimation( 0, new Rectangle( 0, 4 * 32, 32, 32 ), 8, true )
            .setState( RFState.RIGHT.ordinal() )
            .addSpritesToAnimation( 0, new Rectangle( 0, 5 * 32, 32, 32 ), 8, true )
        .build();
        
        Collection<AssetTypeKey> allSpriteAssetKeys = spriteAnimationHandler.getAllSpriteAssetKeys();
        caveAssetsToReload.addAll( allSpriteAssetKeys );
        firstSpriteId = allSpriteAssetKeys.iterator().next().id;
        
        initialized = true;
    }

    private void createSounds( RFController rfController ) {
        assetSystem.getAssetBuilderWithAutoLoad( SoundAsset.class )
            .set( SoundAsset.NAME, ROCKFORD_SPACE_SOUND_ASSEET_KEY.name )
            .set( SoundAsset.ASSET_GROUP, ROCKFORD_SPACE_SOUND_ASSEET_KEY.group )
            .set( SoundAsset.RESOURCE_NAME, "original/sound/walkSpace.wav" )
            .set( SoundAsset.STREAMING, false )
        .buildAndNext()
            .set( SoundAsset.NAME, ROCKFORD_SAND_SOUND_ASSEET_KEY.name )
            .set( SoundAsset.ASSET_GROUP, ROCKFORD_SAND_SOUND_ASSEET_KEY.group )
            .set( SoundAsset.RESOURCE_NAME, "original/sound/walkSand.wav" )
            .set( SoundAsset.STREAMING, false )
        .buildAndNext()
            .set( SoundAsset.NAME, ROCKFORD_COLLECT_SOUND_ASSEET_KEY.name )
            .set( SoundAsset.ASSET_GROUP, ROCKFORD_COLLECT_SOUND_ASSEET_KEY.group )
            .set( SoundAsset.RESOURCE_NAME, "original/sound/collectDiamond.wav" )
            .set( SoundAsset.STREAMING, false )
        .build();
        
        rfController.spaceSoundId = soundSystem.getSoundBuilder()
            .set( Sound.NAME, ROCKFORD_SPACE_SOUND_ASSEET_KEY.name )
            .set( Sound.ASSET_ID, assetSystem.getAssetId( ROCKFORD_SPACE_SOUND_ASSEET_KEY ) )
            .set( Sound.LOOPING, false )
        .build().getId();
        rfController.sandSoundId = soundSystem.getSoundBuilder()
            .set( Sound.NAME, ROCKFORD_SAND_SOUND_ASSEET_KEY.name )
            .set( Sound.ASSET_ID, assetSystem.getAssetId( ROCKFORD_SAND_SOUND_ASSEET_KEY ) )
            .set( Sound.LOOPING, false )
        .build().getId();
        rfController.collectSoundId = soundSystem.getSoundBuilder()
            .set( Sound.NAME, ROCKFORD_COLLECT_SOUND_ASSEET_KEY.name )
            .set( Sound.ASSET_ID, assetSystem.getAssetId( ROCKFORD_COLLECT_SOUND_ASSEET_KEY ) )
            .set( Sound.LOOPING, false )
        .build().getId();
        rfController.inSoundId = CaveService.CaveSoundKey.CRACK.id;
    }

    @Override
    public final void loadCaveData( FFContext context ) {
        super.loadCaveData( context );
        
        float updateRate = caveService.getUpdateRate();
        RFController rfController = (RFController) controllerSystem.getController( controllerId );
        rfController.setUpdateResolution( updateRate );
        spriteAnimationHandler.setFrameTime( RFState.ENTERING.ordinal(), 400 - (int) updateRate * 4 );
        spriteAnimationHandler.setFrameTime( RFState.APPEARING.ordinal(), 300 - (int) updateRate * 4 );
        spriteAnimationHandler.setFrameTime( RFState.IDLE.ordinal(), Integer.MAX_VALUE );
        spriteAnimationHandler.setFrameTime( RFState.IDLE_BLINKING.ordinal(), 100 - (int) updateRate * 4 );
        spriteAnimationHandler.setFrameTime( RFState.IDLE_FRETFUL.ordinal(), 100 - (int) updateRate * 4 );
        spriteAnimationHandler.setFrameTime( RFState.LEFT.ordinal(), 60 - (int) updateRate * 4 );
        spriteAnimationHandler.setFrameTime( RFState.RIGHT.ordinal(), 60 - (int) updateRate * 4 );
    }

    @Override
    public final UnitType type() {
        return UnitType.ROCKFORD;
    }

    @Override
    public final void initBDCFFTypesMap( Map<String, UnitType> bdcffMap ) {
        bdcffMap.put( "P", type() );
        bdcffMap.put( "P1", type() );
    }

    @Override
    public final int createOne( int xGridPos, int yGridPos ) {
        return entitySystem.getEntityBuilderWithAutoActivation()
            .set( EController.CONTROLLER_IDS, new int[] { controllerId, spriteAnimationHandler.getControllerId() } )
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveService.CAVE_VIEW_NAME ) )
            .set( ESprite.SPRITE_ID, firstSpriteId )
            .set( ETile.GRID_X_POSITION, xGridPos )
            .set( ETile.GRID_Y_POSITION, yGridPos )
            .set( EUnit.UNIT_TYPE, type() )
            .set( EUnit.ASPECTS, AspectSetBuilder.create( UnitAspect.MASSIVE ) )
            .set( RFUnit.STATE, RFState.ENTERING )
        .build().getId();
    }
    
    @Override
    public final void dispose( FFContext context ) {
        spriteAnimationHandler.dispose( context );
        RFController rfController = (RFController) controllerSystem.getController( controllerId );
        if ( rfController != null ) {
            soundSystem.deleteSound( rfController.spaceSoundId );
            soundSystem.deleteSound( rfController.sandSoundId );
            soundSystem.deleteSound( rfController.collectSoundId );
        }
        controllerSystem.deleteController( controllerId );
    }

}
