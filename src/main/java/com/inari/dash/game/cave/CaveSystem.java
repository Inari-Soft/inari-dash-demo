package com.inari.dash.game.cave;

import java.util.HashMap;
import java.util.Map;

import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Position;
import com.inari.commons.lang.TypedKey;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.dash.game.GameData;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.asset.AssetNameKey;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.filter.IColorFilter;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.renderer.tile.TileGrid;
import com.inari.firefly.renderer.tile.TileGridSystem;
import com.inari.firefly.sound.SoundAsset;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFInitException;
import com.inari.firefly.system.FFSystem;

public class CaveSystem implements FFSystem {
    
    public static final TypedKey<CaveSystem> CONTEXT_KEY = TypedKey.create( "CaveService", CaveSystem.class );
    public static final Map<String, UnitType> BDCFF_TYPES_MAP = new HashMap<String, UnitType>();
    
    public enum CaveState {
        INIT,
        ENTERING,
        PLAY,
        WON,
        LOOSE,
        GAME_OVER
    }
    
    public static final String CAVE_SOUND_GROUP_NAME = "caveSound";
    public static final AssetNameKey GAME_UNIT_TEXTURE_KEY = new AssetNameKey( "gameUnitTexturKey", "gameUnitTexturKey" );
    public static final String HEADER_VIEW_NAME = "HeaderView";
    public static final int HEADER_VIEW_HEIGHT = 32;
    public static final String CAVE_VIEW_NAME = "GameView";
    public static final String CAVE_CONTROLLER_NAME = "CaveController";
    public static final String CAVE_CAMERA_CONTROLLER_NAME = "CaveViewCamera";
    public static final String CAVE_TILE_GRID_NAME = "CaveTileGrid";
    public static final TypedKey<IColorFilter> COLOR_FILTER_KEY = TypedKey.create( "COLOR_FILTER_KEY", IColorFilter.class );
    
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
    
    private EntitySystem entitySystem;
    private TileGridSystem tileGridSystem;
    
    CaveState caveState;

    private GameData gameData;
    private CaveData caveData;
    private AmoebaData amoebaData;
    private TileGrid tileGrid = null;
    private char[] headerText = "%%%%%%%%%%%%%%%%%%%%%%%%".toCharArray();
    
    @Override
    public final void init( FFContext context ) throws FFInitException {
        gameData = context.getComponent( GameData.CONTEXT_KEY );
        entitySystem = context.getSystem( EntitySystem.CONTEXT_KEY );
        tileGridSystem = context.getSystem( TileGridSystem.CONTEXT_KEY );
    }

    @Override
    public final void dispose( FFContext context ) {
        // TODO Auto-generated method stub
        
    }
    
    public final void reset() {
        caveState = CaveState.INIT;
        caveData = gameData.getCurrentCave();
        amoebaData = new AmoebaData( caveData );
        tileGrid = null;
    }
    
    public final CaveData getCaveData() {
        return caveData;
    }
    
    public final GameData getGameData() {
        return gameData;
    }
    
    public final char[] getHeaderText() {
        return headerText;
    }

    public final int getDiamondsToCollect() {
        return caveData.getDiamondsToCollect();
    }

    public final int getDiamondsCollected() {
        return caveData.getDiamondsCollected();
    }
    
    public final void collectDiamond() {
        gameData.collectDiamond();
    }
    
    public final CaveState getCaveState() {
        return caveState;
    }
    
    public final AmoebaData getAmoebaData() {
        return amoebaData;
    }
    
    public final int getMagicWallTime() {
        return caveData.getMagicWallActivTime();
    }

    public final float getUpdateRate() {
        return caveData.getUpdateRate() + 2;
    }
    
    public final void setEntityId( int entityId, int x, int y ) {
        loadTileGrid();
        tileGrid.set( entityId, x, y );
    }
    
    public final int getEntityId( int x, int y, Direction dir ) {
        loadTileGrid();
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
        loadTileGrid();
        return tileGrid.get( x, y );
    }

    public final void deleteUnit( int x, int y, Direction tileOnDirection ) {
        deleteUnit( getEntityId( x, y, tileOnDirection ), x, y );
    }
    
    public final void deleteUnit( int x, int y ) {
        deleteUnit( getEntityId( x, y ), x, y );
    }
    
    public final void deleteUnit( int entityId, int x, int y ) {
        ETile tile = entitySystem.getComponent( entityId, ETile.class );
        EUnit unit = entitySystem.getComponent( entityId, EUnit.class );
        if ( unit.getUnitType() == UnitType.ROCKFORD ) {
            caveState = CaveState.LOOSE;
        }
        if ( unit.getUnitType() == UnitType.EXIT ) {
            caveState = CaveState.WON;
        }
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
        int entityId = getEntityId( x, y, tileOnDirection );
        if ( entityId < 0 ) {
            System.out.println("*");
        }
        return isOfType( entityId, type );
    }
    
    public final boolean hasInSurrounding( int entityId, UnitType type ) {
        ETile tile = entitySystem.getComponent( entityId, ETile.class );
        int x = tile.getGridXPos();
        int y = tile.getGridYPos();
        return 
            isOfType( x, y, Direction.NORTH, type ) ||
            isOfType( x, y, Direction.EAST, type ) ||
            isOfType( x, y, Direction.SOUTH, type ) ||
            isOfType( x, y, Direction.WEST, type );
    }
    
    public boolean hasInSurrounding( int entityId, UnitType type, UnitAspect aspect ) {
        ETile tile = entitySystem.getComponent( entityId, ETile.class );
        int x = tile.getGridXPos();
        int y = tile.getGridYPos();
        return 
            ( isOfType( x, y, Direction.NORTH, type ) && hasAspect( x, y, Direction.NORTH, aspect ) ) ||
            ( isOfType( x, y, Direction.EAST, type ) && hasAspect( x, y, Direction.EAST, aspect ) ) ||
            ( isOfType( x, y, Direction.SOUTH, type ) && hasAspect( x, y, Direction.SOUTH, aspect ) ) ||
            ( isOfType( x, y, Direction.WEST, type ) && hasAspect( x, y, Direction.WEST, aspect ) );
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

    private void loadTileGrid() {
        if ( tileGrid != null ) {
            return;
        }
        
        tileGrid = tileGridSystem.getTileGrid( CaveSystem.CAVE_TILE_GRID_NAME );
    }

}
