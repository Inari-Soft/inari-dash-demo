package com.inari.dash.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inari.commons.lang.TypedKey;
import com.inari.dash.game.GameInfo.CodeType;
import com.inari.dash.game.unit.IUnitType;
import com.inari.dash.game.unit.UnitType;

public class GameData {
    
    public static final TypedKey<GameData> CONTEXT_KEY = TypedKey.create( "GameData", GameData.class );

    private final GameInfo gameInfo;

    private int lives = 3;
    private int score;
    
    private int bonusLiveScore = 500;
    private int nextLiveOnScore = 500;
    
    private IUnitType playerTileType = UnitType.ROCKFORD;
    private IUnitType emptyTileType = UnitType.SPACE; 
    
    private boolean modified = false;
    
    private List<CaveData> caves = new ArrayList<CaveData>();
    private CaveData currentCave;
    private Iterator<CaveData> caveDataIterator = null;
    
    public GameData( GameInfo gameInfo ) {
        this.gameInfo = gameInfo;
    }
    
    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public GameData addCave( CaveData cave ) {
        caves.add( cave );
        return this;
    }
    
    public CaveData getCurrentCave() {
        return currentCave;
    }

    public GameData setNextCave() {
        if ( caveDataIterator == null ) {
            caveDataIterator = caves.iterator();
        }
        if ( caveDataIterator.hasNext() ) {
            currentCave = caveDataIterator.next();
        } 
        return this;
    }
    
    public boolean hasNextCave() {
        return caveDataIterator.hasNext();
    }

    public GameData collectDiamond() {
        currentCave.collectDiamond();
        if ( currentCave.getDiamondsCollected() > currentCave.getDiamondsToCollect() ) {
            score += currentCave.getExtraDiamondPoints();
        } else {
            score += currentCave.getNeededDiamondPoints();
        }
        if ( score > nextLiveOnScore ) {
            lives++;
            nextLiveOnScore += bonusLiveScore;
        }
        
        modified = true;
        return this;
    }

    public int getScore() {
        return score;
    }

    public GameData setScore( int score ) {
        this.score = score;
        return this;
    }
    
    public GameData addScore( int score ) {
        this.score += score;
        modified = true;
        return this;
    }
    
    public GameData setLives( int lives ) {
        this.lives = lives;
        return this;
    }

    public int getLives() {
        return lives;
    }

    public int getBonusLiveScore() {
        return bonusLiveScore;
    }

    public GameData setBonusLiveScore( int bonusLiveScore ) {
        this.bonusLiveScore = bonusLiveScore;
        return this;
    }

    public boolean isModified() {
        return modified;
    }
    
    public CodeType getType() {
        return gameInfo.getType();
    }

    public IUnitType getPlayerTileType() {
        return playerTileType;
    }

    public GameData setPlayerTileType( IUnitType playerTileType ) {
        this.playerTileType = playerTileType;
        return this;
    }

    public IUnitType getEmptyTileType() {
        return emptyTileType;
    }

    public GameData setEmptyTileType( IUnitType emptyTileType ) {
        this.emptyTileType = emptyTileType;
        return this;
    }

}
