package com.inari.dash.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.inari.commons.lang.TypedKey;
import com.inari.dash.game.GameInfo.CodeType;
import com.inari.dash.game.cave.CaveData;
import com.inari.firefly.component.DataComponent;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;

public class GameData extends DataComponent {
    
    public static final TypedKey<GameData> CONTEXT_KEY = TypedKey.create( "GameData", GameData.class );

    private final GameInfo gameInfo;

    private int lives = 3;
    private int score;
    
    private int bonusLiveScore = 500;
    private int nextLiveOnScore = 500;
    
    private boolean modified = false;
    
    private List<CaveData> caves = new ArrayList<CaveData>();
    private int currentCurrentCaveIndex = 0;
    private CaveData currentCave;
    
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
    
    public void setCave( int index ) {
        currentCave = caves.get( index );
        currentCurrentCaveIndex = index;
    }

    public void nextCave() {
        if ( hasNextCave() ) {
            currentCurrentCaveIndex++;
            currentCave = caves.get( currentCurrentCaveIndex );
        }
    }
    
    public boolean hasNextCave() {
        return currentCurrentCaveIndex < caves.size() - 1;
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

    @Override
    public Class<GameData> componentType() {
        return GameData.class;
    }

    @Override
    public Set<AttributeKey<?>> attributeKeys() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void fromAttributes( AttributeMap attributes ) {
        // TODO Auto-generated method stub
    }

    @Override
    public void toAttributes( AttributeMap attributes ) {
        // TODO Auto-generated method stub
    }

    @Override
    public final TypedKey<? extends DataComponent> componentKey() {
        return CONTEXT_KEY;
    }

}
