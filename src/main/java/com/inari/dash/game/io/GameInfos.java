package com.inari.dash.game.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.inari.commons.StringUtils;
import com.inari.dash.game.GameInfo;
import com.inari.firefly.Disposable;
import com.inari.firefly.FFInitException;
import com.inari.firefly.Loadable;
import com.inari.firefly.system.FFContext;

public class GameInfos implements Loadable, Disposable {
    
    public static final String GAMES_DIR = "games/";
    public static final String GAME_INDEX_FILE_NAME = GAMES_DIR + "gamesIndex";
    
    private List<GameInfo> gameInfos;

    public final List<GameInfo> getGameInfos() {
        return gameInfos;
    }

    @Override
    public Disposable load( FFContext context ) {
        Collection<String> gameFileNames = getGameFileNames();
        gameInfos = new ArrayList<GameInfo>();
        for ( String gameFileName : gameFileNames ) {
            FileHandle resource = Gdx.files.internal( GAMES_DIR + gameFileName );
            GameInfo loadOne = loadOne( resource );
            if ( loadOne != null ) {
                gameInfos.add( loadOne );
            }
        }
        
        return this;
    }
    
    private Collection<String> getGameFileNames() {
        FileHandle resource = Gdx.files.internal( GAME_INDEX_FILE_NAME );
        if ( !resource.exists() ) {
            throw new FFInitException( "Failed to get the game index file within resource: " + resource );
        }
        
        BufferedReader reader = resource.reader( 8192 );
        Collection<String> result = new ArrayList<String>();
        try {
            String nextLine = reader.readLine();
            while ( nextLine != null ) {
                result.add( nextLine.trim() );
                nextLine = reader.readLine();
            }
            reader.close();
            return result;
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        
        return result;
    } 
    
    private GameInfo loadOne( FileHandle resource ) {
        if ( !resource.exists() ) {
            System.out.println( "Unable to load game file resource: " + resource );
            return null;
        }
        
        BufferedReader reader = new BufferedReader( new InputStreamReader( resource.read() ) );
        GameDataBuilder builder = new GameDataBuilder();
        boolean validFile = false;
        try {
            String nextLine = reader.readLine();
            while ( ( nextLine != null ) && !nextLine.startsWith( "[/BDCFF]" ) ) {
                nextLine = nextLine.trim();
                if ( nextLine.startsWith( "[BDCFF]" ) ) {
                    validFile = true;
                } else if ( StringUtils.isBlank( nextLine ) || nextLine.startsWith( ";" ) ) {
                    // ignore comment
                } else if ( validFile ) {
                    if ( ! processLine( nextLine, builder, reader ) ) {
                        break;
                    }
                }
                nextLine = reader.readLine();
            }
            reader.close();
        } catch ( Exception e ) {
            throw new FFInitException( "Error while trying to load BDCFF file: " + resource, e );
        }
        if ( !validFile ) {
            // just ignore
            return null;
        }
        
        GameInfo gameInfo = builder.getCurrentGameInfo();
        gameInfo.setGameConfigResource( resource );
        return gameInfo;
    }
    
    private boolean processLine( String nextLine, GameDataBuilder builder, BufferedReader reader ) {
        if ( nextLine.startsWith( "[game]" ) ) {
            builder.startGame();
            return true;
        }
        if ( nextLine.startsWith( "[cave]" ) ) {
            builder.startCave();
            return false;
        }
        
        String[] nameValue = StringUtils.splitToArray( nextLine, "=" );
        if ( nameValue == null ) {
            return true;
        }
        if ( nameValue.length == 1 ) {
            builder.setProperty( nameValue[ 0 ], "" );
            return true;
        }
        builder.setProperty( nameValue[ 0 ], nameValue[ 1 ] );
        return true;
    }

    @Override
    public void dispose( FFContext context ) {
        if ( gameInfos != null ) {
            gameInfos.clear();
            gameInfos = null;
        }
    }

}
