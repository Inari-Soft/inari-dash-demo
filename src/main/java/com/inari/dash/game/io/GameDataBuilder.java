package com.inari.dash.game.io;

import java.util.HashMap;
import java.util.Map;

import com.inari.commons.StringUtils;
import com.inari.dash.game.GameData;
import com.inari.dash.game.GameInfo;
import com.inari.dash.game.GameInfo.CodeType;
import com.inari.dash.game.GameInfo.Fontset;
import com.inari.dash.game.GameInfo.Gametype;
import com.inari.dash.game.GameInfo.Graphicset;
import com.inari.dash.game.cave.CaveData;
import com.inari.dash.game.cave.CaveData.CaveColors;
import com.inari.firefly.converter.IntColorConverter;

public class GameDataBuilder {
    
    private GameData gameData;
    private GameInfo currentGameInfo;
    private CaveData currentCaveData; 
    
    
    public void startGame() {
        currentGameInfo = new GameInfo().setType( CodeType.BDCFF );
        gameData = new GameData( currentGameInfo );
    }
    
    public void endGame() {
        
    }
    
    GameInfo getCurrentGameInfo() {
        return currentGameInfo;
    }
    
    public GameData createGame() {
        return gameData;
    }
    
    public GameDataBuilder setType( CodeType type ) {
        currentGameInfo.setType( type );
        return this;
    }
    
    public GameDataBuilder startCave() {
        currentCaveData = new CaveData();
        gameData.addCave( currentCaveData );
        return this;
    }
    
    public GameDataBuilder endCave() {
        currentCaveData = null;
        return this;
    }
    
    public GameDataBuilder setCaveMap( String caveMapString ) {
        currentCaveData.setCaveDataString( caveMapString );
        return this;
    }
    
    public GameDataBuilder setProperty( String name, String value ) {
        try {
            value = value.trim();
            BDCFFProperty propertyConstant = BDCFFProperty.valueOf( name );
            switch ( propertyConstant ) {
                case Author: {
                    currentGameInfo.setAuthor( value );
                    break;
                }
                case GraphicSet: {
                    currentGameInfo.setGraphicset( Graphicset.valueOf( value ) );
                    break;
                }
                case Fontset: {
                    currentGameInfo.setFontset( Fontset.valueOf( value ) );
                    break;
                }
                case Date: {
                    currentGameInfo.setDate( value );
                    break;
                }
                case Name: {
                    if ( currentCaveData == null ) {
                        currentGameInfo.setName( value.toUpperCase() );
                    } else {
                        currentCaveData.setCaveName( value.toUpperCase() );
                    }
                    break;
                }
                case Description: {
                    currentGameInfo.setDescription( value );
                    break;
                }
                case GameType: {
                    currentGameInfo.setGameType( Gametype.valueOf( value ) );
                    break;
                }
                case WWW: {
                    currentGameInfo.setWww( value );
                    break;
                }
                case Caves: {
                    currentGameInfo.setCaves( Integer.parseInt( value ) );
                    break;
                }
                case Lives: {
                    gameData.setLives( Integer.parseInt( value ) );
                    break;
                }
                case BonusLife: {
                    gameData.setBonusLiveScore( Integer.parseInt( value ) );
                    break;
                }
                case AmoebaGrowthProb: {
                    setAmoebaGrowthProb( value );
                    break;
                }
                case AmoebaThreshold: {
                    currentCaveData.setAmoebaThreshold( Float.parseFloat( value ) );
                    break;
                }
                case AmoebaTime: {
                    currentCaveData.setAmoebaTime( Integer.parseInt( value ) );
                    break;
                }
                case CaveDelay:
                    currentCaveData.setUpdateRate( 12 - Integer.parseInt( value ) );
                    break;
                case FrameTime: {
                    currentCaveData.setUpdateRate( Integer.parseInt( value ) );
                    break;
                }
                case Size:
                case CaveSize: {
                    setCaveSize( value );
                    break;
                }
                case CaveTime: {
                    currentCaveData.setTime( Integer.parseInt( value ) );
                    break;
                }
                case Colors : {
                    setColors( value );
                    break;
                }
                case DiamondsRequired: {
                    currentCaveData.setDiamondsToCollect( Integer.parseInt( getFirstValue( value ) ) );
                    break;
                }
                case DiamondValue: {
                    setDiamondValues( value );
                    break;
                }
                case Intermission: {
                    currentCaveData.setIntermission( Boolean.parseBoolean( value ) );
                    break;
                }
                case MagicWallTime: {
                    currentCaveData.setMagicWallActivTime( Integer.parseInt( value ) );
                    break;
                }
                case TimeValue: {
                    currentCaveData.setExtraSecondScore( Integer.parseInt( getFirstValue( value ) ) );
                    break;
                }
            }
        } catch ( Exception e ) {
            System.out.println( "--> Unknown BDCFFProperty: " + name );
            // ignore
        }
        return this;
    }
    
    
    
    private void setDiamondValues( String value ) {
        String[] splitToArray = StringUtils.splitToArray( value, " " );
        if ( splitToArray.length > 1 ) {
            currentCaveData.setExtraDiamondPoints( Integer.parseInt( splitToArray[ 1 ] ) );
            currentCaveData.setNeededDiamondPoints( Integer.parseInt( splitToArray[ 0 ] ) );
        } else {
            currentCaveData.setExtraDiamondPoints( Integer.parseInt( splitToArray[ 0 ] ) );
            currentCaveData.setNeededDiamondPoints( Integer.parseInt( splitToArray[ 0 ] ) );
        }
    }

    private String getFirstValue( String value ) {
        String[] splitToArray = StringUtils.splitToArray( value, " " );
        return splitToArray[ 0 ];
    }

    private void setColors( String value ) {
        String[] splitToArray = StringUtils.splitToArray( value, " " );
        
        String foreground1 = ( splitToArray.length >= 5 )? splitToArray[ 2 ]: splitToArray[ 0 ];
        String foreground2 = ( splitToArray.length >= 5 )? splitToArray[ 3 ]: splitToArray[ 1 ];
        String foreground3 = ( splitToArray.length >= 5 )? splitToArray[ 4 ]: splitToArray[ 2 ];
        String amoebaColor = ( splitToArray.length > 5 )? splitToArray[ 5 ]: null;
        
        Map<Integer,Integer> colorMap = new HashMap<Integer,Integer>();
        colorMap.put( CaveColors.BaseForegroundColor1.color, CaveColors.valueOf( foreground1 ).color );
        colorMap.put( CaveColors.BaseForegroundColor2.color, CaveColors.valueOf( foreground2 ).color );
        colorMap.put( CaveColors.BaseForegroundColor3.color, CaveColors.valueOf( foreground3 ).color );
        if ( amoebaColor != null ) {
            colorMap.put( CaveColors.BaseAmoebaColor.color, CaveColors.valueOf( amoebaColor ).color );
        }
        
        IntColorConverter colorFunction = new IntColorConverter( colorMap );
        currentCaveData.setColorFunction( colorFunction );
    }

    private void setCaveSize( String value ) {
        String[] splitToArray = StringUtils.splitToArray( value, " " );
        currentCaveData.setCaveWidth( Integer.parseInt( splitToArray[ 0 ] ) );
        currentCaveData.setCaveHeight( Integer.parseInt( splitToArray[ 1 ] ) );
    }

    private void setAmoebaGrowthProb( String value ) {
        String[] splitToArray = StringUtils.splitToArray( value, " " );
        currentCaveData.setAmoebaSlowGrowthProb( Integer.parseInt( splitToArray[ 0 ] ) );
        currentCaveData.setAmoebaFastGrowthProb( Integer.parseInt( splitToArray[ 1 ] ) );
    }


}
