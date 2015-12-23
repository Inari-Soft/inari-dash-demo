package com.inari.dash.game.cave;

import java.util.HashMap;
import java.util.Map;

import com.inari.commons.geom.Direction;
import com.inari.commons.geom.Position;
import com.inari.commons.lang.TypedKey;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.indexed.IndexedTypeKey;
import com.inari.dash.game.GameData;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.firefly.FFInitException;
import com.inari.firefly.filter.IColorFilter;
import com.inari.firefly.renderer.tile.ETile;
import com.inari.firefly.renderer.tile.TileGrid;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFSystem;

public class CaveSystem implements FFSystem {
    
    public static final FFSystemTypeKey<CaveSystem> SYSTEM_KEY = FFSystemTypeKey.create( CaveSystem.class );
    public static final Map<String, UnitType> BDCFF_TYPES_MAP = new HashMap<String, UnitType>();
    public static final String CAVE_INIT_SCENE_NAME = "CaveInitScene";
    public static final String INTRO_TILE_SPRITE_NAME = "introTileSprite";
    
    public enum SoundChannel {
        CAVE,
        DIAMOND,
        ROCK,
        MAGIC_WALL,
        AMOEBA,
        COLLECT
    }
    
    public enum CaveState {
        INIT,
        ENTERING,
        PLAY,
        WON,
        LOOSE,
        GAME_OVER
    }
    
    public static final String GAME_UNIT_TEXTURE_NAME = "gameUnitTexturKey";
    public static final String HEADER_VIEW_NAME = "HeaderView";
    public static final int HEADER_VIEW_HEIGHT = 32;
    public static final String CAVE_VIEW_NAME = "GameView";
    public static final String CAVE_CONTROLLER_NAME = "CaveController";
    public static final String CAVE_CAMERA_CONTROLLER_NAME = "CaveViewCamera";
    public static final String CAVE_TILE_GRID_NAME = "CaveTileGrid";
    public static final TypedKey<IColorFilter> COLOR_FILTER_KEY = TypedKey.create( "COLOR_FILTER_KEY", IColorFilter.class );
    
    public static enum CaveSoundKey {
        COVER( "original/sound/cover.wav", true ),
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
        
        public final String fileName;
        public final boolean looping;
        
        private CaveSoundKey( String fileName, boolean looping ) {
            this.fileName = fileName;
            this.looping = looping;
        }
    }
    
    private FFContext context;
    
    CaveState caveState;

    private GameData gameData;
    private CaveData caveData;
    private AmoebaData amoebaData;
    private TileGrid tileGrid = null;
    private char[] headerText = "%%%%%%%%%%%%%%%%%%%%%%%%".toCharArray();
    
    @Override
    public final IndexedTypeKey indexedTypeKey() {
        return SYSTEM_KEY;
    }

    @Override
    public final FFSystemTypeKey<CaveSystem> systemTypeKey() {
        return SYSTEM_KEY;
    }
    
    @Override
    public final void init( FFContext context ) throws FFInitException {
        gameData = context.getContextComponent( GameData.CONTEXT_KEY );
        this.context = context;
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
        return caveData.getUpdateRate() + 3;
    }
    
    public final boolean updateCamera() {
        return caveState == CaveState.PLAY || caveState == CaveState.ENTERING || caveState == CaveState.INIT;
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
        ETile tile = context.getEntityComponent( entityId, ETile.TYPE_KEY );
        EUnit unit = context.getEntityComponent( entityId, EUnit.TYPE_KEY );
        if ( unit.getUnitType() == UnitType.ROCKFORD ) {
            caveState = CaveState.LOOSE;
        }
        if ( unit.getUnitType() == UnitType.EXIT ) {
            caveState = CaveState.WON;
        }
        if ( tile.isMultiPosition() ) {
            tile.getGridPositions().remove( new Position( x, y ) );
        } else {
            context.deleteEntity( entityId );
        }
    }
    
    public final UnitType getUnitType( int x, int y ) {
        int entityId = getEntityId( x, y );
        return context.getEntityComponent( entityId, EUnit.TYPE_KEY ).getUnitType();
    }
    
    public final UnitType getUnitType( int x, int y, Direction tileOnDirection ) {
        int entityId = getEntityId( x, y, tileOnDirection );
        return context.getEntityComponent( entityId, EUnit.TYPE_KEY ).getUnitType();
    }
    
    public final boolean isOfType( int entityId, UnitType type ) {
        EUnit unit = context.getEntityComponent( entityId, EUnit.TYPE_KEY );
        return unit.getUnitType() == type;
    }
    
    public boolean isOfType( int entityId, Direction tileOnDirection, UnitType type ) {
        ETile tile = context.getEntityComponent( entityId, ETile.TYPE_KEY );
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
        ETile tile = context.getEntityComponent( entityId, ETile.TYPE_KEY );
        int x = tile.getGridXPos();
        int y = tile.getGridYPos();
        return 
            isOfType( x, y, Direction.NORTH, type ) ||
            isOfType( x, y, Direction.EAST, type ) ||
            isOfType( x, y, Direction.SOUTH, type ) ||
            isOfType( x, y, Direction.WEST, type );
    }
    
    public boolean hasInSurrounding( int entityId, UnitType type, UnitAspect aspect ) {
        ETile tile = context.getEntityComponent( entityId, ETile.TYPE_KEY );
        int x = tile.getGridXPos();
        int y = tile.getGridYPos();
        return 
            ( isOfType( x, y, Direction.NORTH, type ) && hasAspect( x, y, Direction.NORTH, aspect ) ) ||
            ( isOfType( x, y, Direction.EAST, type ) && hasAspect( x, y, Direction.EAST, aspect ) ) ||
            ( isOfType( x, y, Direction.SOUTH, type ) && hasAspect( x, y, Direction.SOUTH, aspect ) ) ||
            ( isOfType( x, y, Direction.WEST, type ) && hasAspect( x, y, Direction.WEST, aspect ) );
    }

    public final boolean hasAspect( int entityId, Aspect aspect ) {
        EUnit unit = context.getEntityComponent( entityId, EUnit.TYPE_KEY );
        return unit.has( aspect );
    }

    public final boolean hasAspect( int x, int y, Aspect aspect ) {
        return hasAspect( x, y, Direction.NONE, aspect );
    }
    
    public final boolean hasAspect( int x, int y, Direction tileOnDirection, Aspect aspect ) {
        return hasAspect( getEntityId( x, y, tileOnDirection ), aspect );
    }
    
    public final boolean isMoving( int entityId, Direction moveDirection ) {
        EUnit unit = context.getEntityComponent( entityId, EUnit.TYPE_KEY );
        return unit.getMovement() == moveDirection;
    }
    
    public final boolean isMoving( int x, int y, Direction moveDirection ) {
        return isMoving( x, y, Direction.NONE, moveDirection );
    }
    
    public final boolean isMoving( int x, int y, Direction tileOnDirection, Direction moveDirection ) {
        EUnit unit = context.getEntityComponent( getEntityId( x, y, tileOnDirection ), EUnit.TYPE_KEY );
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
        
        tileGrid = context.getSystemComponent( TileGrid.TYPE_KEY, CaveSystem.CAVE_TILE_GRID_NAME );
    }

}
