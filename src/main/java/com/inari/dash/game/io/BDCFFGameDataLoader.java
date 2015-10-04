package com.inari.dash.game.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.badlogic.gdx.files.FileHandle;
import com.inari.commons.StringUtils;
import com.inari.dash.game.GameData;
import com.inari.firefly.system.FFInitException;

public class BDCFFGameDataLoader {
    
    public GameData load( FileHandle fileHandler ) {
        BufferedReader reader = new BufferedReader( new InputStreamReader( fileHandler.read() ) );
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
                    processLine( nextLine, builder, reader );
                }
                nextLine = reader.readLine();
            }
            reader.close();
        } catch ( Exception e ) {
            throw new FFInitException( "Error while trying to load BDCFF file: " + fileHandler, e );
        }
        if ( !validFile ) {
            throw new IllegalArgumentException( "Invalid BDCFF file: " + fileHandler );
        }
        
        return builder.createGame();
    }
    
    private void processLine( String nextLine, GameDataBuilder builder, BufferedReader reader ) {
        if ( nextLine.startsWith( "[game]" ) ) {
            builder.startGame();
            return;
        }
        if ( nextLine.startsWith( "[cave]" ) ) {
            builder.startCave();
            return;
        }
        if ( nextLine.startsWith( "[map]" ) ) {
            builder.setCaveMap( processCaveMap( reader, builder ) );
            return;
        }
        if ( nextLine.startsWith( "[/cave]" ) ) {
            builder.endCave();
            return;
        }
        if ( nextLine.startsWith( "[/game]" ) ) {
            builder.endGame();
            return;
        }
        
        String[] nameValue = StringUtils.splitToArray( nextLine, "=" );
        if ( nameValue == null ) {
            return;
        }
        if ( nameValue.length == 1 ) {
            builder.setProperty( nameValue[ 0 ], "" );
            return;
        }
        builder.setProperty( nameValue[ 0 ], nameValue[ 1 ] );
    }
    
    private String processCaveMap( BufferedReader reader, GameDataBuilder builder ) {
        try {
            int caveWidth = -1;
            int caveHeight = 0;
            String caveMap = "";
            String nextLine = reader.readLine();
            if ( caveWidth < 0 ) {
                caveWidth = nextLine.length();
            }
            while ( !nextLine.startsWith( "[/map]" ) ) {
                caveMap += nextLine;
                nextLine = reader.readLine();
                caveHeight++;
            }
            
            builder.setProperty( BDCFFProperty.Size.name(), caveWidth + " " + caveHeight );
            
            return caveMap;
        } catch ( Exception e ) {
            e.printStackTrace();
            return null;
        }
    }


}
