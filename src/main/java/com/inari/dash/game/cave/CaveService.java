package com.inari.dash.game.cave;

import java.util.HashMap;
import java.util.Map;

import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.graphics.RGBColor;
import com.inari.commons.lang.TypedKey;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.dash.Configuration;
import com.inari.dash.game.CaveData;
import com.inari.dash.game.GameData;
import com.inari.dash.game.GameService;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.dash.game.cave.unit.action.ActionType;
import com.inari.firefly.Disposable;
import com.inari.firefly.action.Action;
import com.inari.firefly.action.ActionSystem;
import com.inari.firefly.asset.AssetNameKey;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.control.Controller;
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.filter.IColorFilter;
import com.inari.firefly.libgdx.GDXConfiguration;
import com.inari.firefly.renderer.TextureAsset;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.renderer.tile.TileGrid;
import com.inari.firefly.renderer.tile.TileGrid.TileRenderMode;
import com.inari.firefly.renderer.tile.TileGridSystem;
import com.inari.firefly.sound.Sound;
import com.inari.firefly.sound.SoundAsset;
import com.inari.firefly.sound.SoundSystem;
import com.inari.firefly.sound.event.SoundEvent;
import com.inari.firefly.sound.event.SoundEvent.Type;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFContextInitiable;
import com.inari.firefly.system.FFInitException;
import com.inari.firefly.system.LowerSystemFacade;
import com.inari.firefly.system.UpdateEvent;
import com.inari.firefly.system.view.View;
import com.inari.firefly.system.view.ViewSystem;

public class CaveService implements FFContextInitiable, Disposable {
    
    public enum CaveState {
        ENTERING,
        PLAY,
        EXIT_OPEN,
        WON,
        TIMEOUT,
        LOOSE
    }
    
    public static final TypedKey<CaveService> CONTEXT_KEY = TypedKey.create( "CaveService", CaveService.class );
    public static final String CAVE_SOUND_GROUP_NAME = "caveSound";
    
    public static enum CaveSoundKey {
        COVER( "original/sound/cover.wav", true ),
        EXPLOSION( "original/sound/explosion.wav", false ),
        TIMEOUT1( "original/sound/timeout1.wav", false ),
        TIMEOUT2( "original/sound/timeout2.wav", false ),
        TIMEOUT3( "original/sound/timeout3.wav", false ),
        TIMEOUT4( "original/sound/timeout4.wav", false ),
        TIMEOUT5( "original/sound/timeout5.wav", false ),
        TIMEOUT6( "original/sound/timeout6.wav", false ),
        TIMEOUT7( "original/sound/timeout7.wav", false ),
        TIMEOUT8( "original/sound/timeout8.wav", false ),
        TIMEOUT9( "original/sound/timeout9.wav", false ),
        CRACK( "original/sound/crack.wav", false ),
        FINISHED( "original/sound/finished.wav", false )
        ;
        
        public final int id;
        public final String fileName;
        public final AssetNameKey assetKey;
        public final boolean looping;
        
        private CaveSoundKey( String fileName, boolean looping ) {
            id = Indexer.getIndexedObjectSize( SoundAsset.class ) + ordinal();
            this.fileName = fileName;
            assetKey = new AssetNameKey( CAVE_SOUND_GROUP_NAME, name() );
            this.looping = looping;
        }
    }
    
    public static final AssetNameKey GAME_UNIT_TEXTURE_KEY = new AssetNameKey( "gameUnitTexturKey", "gameUnitTexturKey" );
    
    public static final String HEADER_VIEW_NAME = "HeaderView";
    public static final String HEADER_VIEW_CONTROLLER_NAME = "HeaderViewController";
    public static final int HEADER_VIEW_HEIGHT = 32;
    public static final String CAVE_VIEW_NAME = "GameView";
    public static final String CAVE_VIEW_CONTROLLER_NAME = "CaveViewController";
    public static final String CAVE_VIEW_CAMERA_NAME = "CaveViewCamera";
    public static final String CAVE_TILE_GRID_NAME = "CaveTileGrid";
    public static final TypedKey<IColorFilter> COLOR_FILTER_KEY = TypedKey.create( "COLOR_FILTER_KEY", IColorFilter.class );
    
    private EntitySystem entitySystem;
    private IEventDispatcher eventDispatcher;
    
    private HeaderController headerController;
    private CaveController caveController;
    private CaveCamera caveCamera;
    
    private static final Map<String, UnitType> BDCFF_TYPES_MAP = new HashMap<String, UnitType>();
    
    private GameData gameData;
    private CaveData caveData;
    private TileGrid tileGrid;
    private AmoebaData amoebaData;
    
    private boolean initialized = false;
    private boolean loaded = false;
    private CaveState caveState = CaveState.ENTERING;
    
    /** Here we create all the assets that are needed for playing a cave */
    @Override
    public final void init( FFContext context ) throws FFInitException {
        context.putComponent( CONTEXT_KEY, this );
        GameService gameService = context.getComponent( GameService.CONTEXT_KEY );
        Configuration config = gameService.getConfiguration();
        LowerSystemFacade lowerSystemFacade = context.getComponent( FFContext.LOWER_SYSTEM_FACADE );
        ViewSystem viewSystem = context.getComponent( ViewSystem.CONTEXT_KEY );
        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        ControllerSystem controllerSystem = context.getComponent( ControllerSystem.CONTEXT_KEY );
        eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
        ActionSystem actionSystem = context.getComponent( ActionSystem.CONTEXT_KEY );
        SoundSystem soundSystem = context.getComponent( SoundSystem.CONTEXT_KEY );
        entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );
        
        int screenWidth = lowerSystemFacade.getScreenWidth();
        int screenHeight = lowerSystemFacade.getScreenHeight();
        
        // create the two views with controller
        caveController = new CaveController();
        eventDispatcher.register( UpdateEvent.class, caveController );
        headerController = controllerSystem.getControllerBuilder( HeaderController.class )
            .set( Controller.NAME, HEADER_VIEW_CONTROLLER_NAME )
        .build();
        caveCamera = controllerSystem.getControllerBuilder( CaveCamera.class )
            .set( Controller.NAME, CAVE_VIEW_CAMERA_NAME )
        .build();
        
        viewSystem.getViewBuilderWithAutoActivation()
            .set( View.NAME, HEADER_VIEW_NAME )
            .set( View.LAYERING_ENABLED, false )
            .set( View.BOUNDS, new Rectangle( 0, 0, screenWidth, HEADER_VIEW_HEIGHT ) )
            .set( View.WORLD_POSITION, new Position( 0, 0 ) )
            .set( View.CONTROLLER_ID, controllerSystem.getControllerId( HEADER_VIEW_CONTROLLER_NAME ) )
            .set( View.CLEAR_COLOR, new RGBColor( 0, 0, 0, 1 ) )
        .buildAndNext()
            .set( View.NAME, CAVE_VIEW_NAME )
            .set( View.LAYERING_ENABLED, false )
            .set( View.BOUNDS, new Rectangle( 20, HEADER_VIEW_HEIGHT, screenWidth - 40, screenHeight - HEADER_VIEW_HEIGHT - 20 ) )
            .set( View.WORLD_POSITION, new Position( 0, 0 ) )
            .set( View.CONTROLLER_ID, controllerSystem.getControllerId( CAVE_VIEW_CAMERA_NAME ) )
            .set( View.CLEAR_COLOR, new RGBColor( 0, 0, 0, 1 ) )
        .build();
        
        // create global cave assets and sounds
        assetSystem.getAssetBuilder( TextureAsset.class )
            .set( TextureAsset.NAME, GAME_UNIT_TEXTURE_KEY.name )
            .set( TextureAsset.ASSET_GROUP, GAME_UNIT_TEXTURE_KEY.group )
            .set( TextureAsset.RESOURCE_NAME, config.unitTextureResource )
            .set( TextureAsset.TEXTURE_WIDTH, config.unitTextureWidth )
            .set( TextureAsset.TEXTURE_HEIGHT, config.unitTextureHeight )
         .build();
        
        for ( CaveSoundKey caveSoundKey : CaveSoundKey.values() ) {
            assetSystem.getAssetBuilderWithAutoLoad( SoundAsset.class )
                .set( SoundAsset.NAME, caveSoundKey.assetKey.name )
                .set( SoundAsset.ASSET_GROUP, caveSoundKey.assetKey.group )
                .set( SoundAsset.RESOURCE_NAME, caveSoundKey.fileName )
                .set( SoundAsset.STREAMING, false )
            .build( caveSoundKey.id );
            soundSystem.getSoundBuilder()
                .set( Sound.NAME, caveSoundKey.assetKey.name )
                .set( Sound.ASSET_ID, caveSoundKey.id )
                .set( Sound.LOOPING, false )
                .set( Sound.CHANNEL, 4 )
            .build( caveSoundKey.id );
        }
        
        // create unit actions
        for ( ActionType actionType : ActionType.values() ) {
            if ( actionType.getActionTypeClass() != null ) {
                actionSystem.getActionBuilder( actionType.getActionTypeClass() )
                    .set( Action.NAME, actionType.name() )
                .build( actionType.index() );
            }
        }
        
        // create and initialize all units
        for ( UnitType unitType : UnitType.values() ) {
            if ( unitType.handler != null ) {
                unitType.handler.init( context );
                unitType.handler.initBDCFFTypesMap( BDCFF_TYPES_MAP );
            }
        }
        
        initialized = true;
    } 
    
    public final void loadCave( FFContext context, GameData gameData ) {
        if ( !initialized ) {
            throw new FFInitException( "CaveService not initilized. Needs initialization first." );
        }
        
        TileGridSystem tileGridSystem = context.getComponent( TileGridSystem.CONTEXT_KEY );
        ViewSystem viewSystem = context.getComponent( ViewSystem.CONTEXT_KEY );
        GameService gameService = context.getComponent( GameService.CONTEXT_KEY );
        Configuration config = gameService.getConfiguration();
        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        
        this.gameData = gameData;
        caveData = gameData.getCurrentCave();
        amoebaData = new AmoebaData( caveData );

        // load unit texture asset with cave colors
        TextureAsset unitTextureAsset = assetSystem.getAsset( GAME_UNIT_TEXTURE_KEY, TextureAsset.class );
        IColorFilter colorFilter = caveData.getColorFilter();
        context.putComponent( COLOR_FILTER_KEY, colorFilter );
        unitTextureAsset.setDynamicAttribute( GDXConfiguration.DynamicAttributes.TEXTURE_COLOR_FILTER_NAME, COLOR_FILTER_KEY.id() );
        assetSystem.loadAsset( GAME_UNIT_TEXTURE_KEY );

        // create tileGrid and cave entities
        int caveWidth = caveData.getCaveWidth();
        int caveHeight = caveData.getCaveHeight();
        tileGrid = tileGridSystem.getTileGridBuilder()
            .set( TileGrid.NAME, CAVE_TILE_GRID_NAME )
            .set( TileGrid.VIEW_ID, viewSystem.getViewId( CAVE_VIEW_NAME ) )
            .set( TileGrid.WIDTH, caveWidth )
            .set( TileGrid.HEIGHT, caveHeight )
            .set( TileGrid.CELL_WIDTH, config.unitWidth )
            .set( TileGrid.CELL_HEIGHT, config.unitHeight )
            .set( TileGrid.RENDER_MODE, TileRenderMode.FAST_RENDERING )
        .build();
        
        // load all units
        for ( UnitType unitType : UnitType.values() ) {
            if ( unitType.handler != null ) {
                unitType.handler.loadCaveData( context );
            }
        }
        
        String caveDataString = caveData.getCaveDataString();
        int index = 0;
        for ( int y = 0; y < caveHeight; y++ ) {
            for ( int x = 0; x < caveWidth; x++ ) {
                String type = String.valueOf( caveDataString.charAt( index ) );
                UnitType unitType = BDCFF_TYPES_MAP.get( type );
                if ( unitType != null ) {
                    unitType.handler.createOne( type, x, y );
                } else {
                    UnitType.SOLID_WALL.handler.createOne( x, y );
                }
                index++;
            }
        }

        loaded = true;
    }
    
    public final void collectDiamond() {
        gameData.collectDiamond();
        if ( caveState == CaveState.PLAY && caveData.getDiamondsToCollect() == caveData.getDiamondsCollected() ) {
            caveState = CaveState.EXIT_OPEN;
            eventDispatcher.notify( new SoundEvent( CaveSoundKey.CRACK.id, Type.PLAY_SOUND ) );
        }
    }
    
    public final void playerIn() {
        caveState = CaveState.PLAY;
    }
    
    public final void won() {
        caveState = CaveState.WON;
    }
    
    public final CaveState getCaveState() {
        return caveState;
    }
    
    public final AmoebaData getAmoebaData() {
        return amoebaData;
    }

    public float getUpdateRate() {
        return caveData.getUpdateRate();
    }
    
    public final void setEntityId( int entityId, int x, int y ) {
        tileGrid.set( entityId, x, y );
    }
    
    public final int getEntityId( int x, int y, Direction dir ) {
        switch ( dir ) {
            case NORTH: return tileGrid.get( x, y - 1 );
            case NORTH_WEST: return tileGrid.get( x - 1, y - 1 );
            case WEST: return tileGrid.get( x - 1, y );
            case SOUTH_WEST: return tileGrid.get( x - 1, y + 1 );
            case SOUTH: return tileGrid.get( x, y + 1 );
            case SOUTH_EAST: return tileGrid.get( x + 1, y + 1 );
            case EAST: return tileGrid.get( x + 1, y );
            case NORTH_EAST: return tileGrid.get( x + 1, y - 1 );
            default: return tileGrid.get( x, y );
        }
    }
    
    public final int getEntityId( int x, int y ) {
        return tileGrid.get( x, y );
    }
    
    public final void deleteUnit( int x, int y, Direction tileOnDirection ) {
        deleteUnit( getEntityId( x, y, tileOnDirection ), x, y );
    }
    
    public final void deleteUnit( int entityId, int x, int y ) {
        ETile tile = entitySystem.getComponent( entityId, ETile.class );
        if ( tile.isMultiPosition() ) {
            tile.getGridPositions().remove( new Position( x, y ) );
        } else {
            entitySystem.delete( entityId );
        }
    }
    
    public final UnitType getUnitType( int x, int y ) {
        int entityId = getEntityId( x, y );
        return entitySystem.getComponent( entityId, EUnit.class ).getUnitType();
    }
    
    public final UnitType getUnitType( int x, int y, Direction tileOnDirection ) {
        int entityId = getEntityId( x, y, tileOnDirection );
        return entitySystem.getComponent( entityId, EUnit.class ).getUnitType();
    }
    
    public final boolean isOfType( int entityId, UnitType type ) {
        EUnit unit = entitySystem.getComponent( entityId, EUnit.class );
        return unit.getUnitType() == type;
    }
    
    public boolean isOfType( int entityId, Direction tileOnDirection, UnitType type ) {
        ETile tile = entitySystem.getComponent( entityId, ETile.class );
        return isOfType( getEntityId( tile.getGridXPos(), tile.getGridYPos(), tileOnDirection ), type );
    }
    
    public final boolean isOfType( int x, int y, UnitType type ) {
        return isOfType( getEntityId( x, y, Direction.NONE ), type );
    }
    
    public final boolean isOfType( int x, int y, Direction tileOnDirection, UnitType type ) {
        return isOfType( getEntityId( x, y, tileOnDirection ), type );
    }
    
    public boolean hasTypeInSurrounding( int entityId, UnitType type ) {
        ETile tile = entitySystem.getComponent( entityId, ETile.class );
        int x = tile.getGridXPos();
        int y = tile.getGridYPos();
        return 
            isOfType( x, y, Direction.NORTH, type ) ||
            isOfType( x, y, Direction.EAST, type ) ||
            isOfType( x, y, Direction.SOUTH, type ) ||
            isOfType( x, y, Direction.EAST, type );
    }

    public final boolean hasAspect( int entityId, Aspect aspect ) {
        EUnit unit = entitySystem.getComponent( entityId, EUnit.class );
        return unit.has( aspect );
    }

    public final boolean hasAspect( int x, int y, Aspect aspect ) {
        return hasAspect( x, y, Direction.NONE, aspect );
    }
    
    public final boolean hasAspect( int x, int y, Direction tileOnDirection, Aspect aspect ) {
        return hasAspect( getEntityId( x, y, tileOnDirection ), aspect );
    }
    
    public final boolean isMoving( int entityId, Direction moveDirection ) {
        EUnit unit = entitySystem.getComponent( entityId, EUnit.class );
        return unit.getMovement() == moveDirection;
    }
    
    public final boolean isMoving( int x, int y, Direction moveDirection ) {
        return isMoving( x, y, Direction.NONE, moveDirection );
    }
    
    public final boolean isMoving( int x, int y, Direction tileOnDirection, Direction moveDirection ) {
        EUnit unit = entitySystem.getComponent( getEntityId( x, y, tileOnDirection ), EUnit.class );
        return unit.getMovement() == moveDirection;
    }
    
    public final void createOne( int x, int y, UnitType unitType ) {
        unitType.handler.createOne( x, y );
    }

    public final void disposeCave( FFContext context ) {
        if ( !loaded ) {
            return;
        }
        // dispose all units
        for ( UnitType unitType : UnitType.values() ) {
            if ( unitType.handler != null ) {
                unitType.handler.disposeCaveData( context );
            }
        }
        
        loaded = false;
    }

    @Override
    public final void dispose( FFContext context ) {
        // load all units
        for ( UnitType unitType : UnitType.values() ) {
            if ( unitType.handler != null ) {
                unitType.handler.dispose( context );
            }
        }
        initialized = false;
    }
    
    public final static class AmoebaData {
        public final float amoebaSlowGrowthProb;
        public final float amoebaFastGrowthProb;
        public final float amoebaThreshold;
        public final int amoebaTime;
        public final int growthLimit;
        
        private AmoebaData( CaveData caveData ) {
            amoebaSlowGrowthProb = caveData.getAmoebaSlowGrowthProb();
            amoebaFastGrowthProb = caveData.getAmoebaFastGrowthProb();
            amoebaThreshold = caveData.getAmoebaThreshold();
            amoebaTime = caveData.getAmoebaTime();

            int caveSize = caveData.getCaveWidth() * caveData.getCaveHeight();
            growthLimit = Math.round( caveSize * amoebaThreshold );
        }
    }

}
