package com.inari.dash.game;

import java.awt.Color;

import com.inari.firefly.filter.IColorFilter;


public class CaveData {
    
    public static enum CaveColors {
        Black( Color.BLACK.getRGB() ), 
        White( Color.WHITE.getRGB() ), 
        Red( new Color( 155, 77, 66 ).getRGB() ), 
        Cyan( new Color( 140, 214, 223 ).getRGB() ), 
        Purple( new Color( 157, 84, 196 ).getRGB() ), 
        Green( new Color( 120, 191, 78 ).getRGB() ), 
        Blue( new Color( 40, 40, 183 ).getRGB() ), 
        Yellow( new Color( 233, 245, 130 ).getRGB() ), 
        Orange( new Color( 153, 102, 51 ).getRGB() ), 
        Brown( new Color( 107, 85, 0 ).getRGB() ), 
        LightRed( new Color( 210, 136, 126 ).getRGB() ), 
        Gray1( new Color( 124, 124, 124 ).getRGB() ), 
        Gray2( new Color( 146, 146, 146 ).getRGB() ), 
        LightGreen( new Color( 193, 255, 154 ).getRGB() ), 
        LightBlue( new Color( 159, 221, 255 ).getRGB() ), 
        Gray3( new Color( 193, 193, 193 ).getRGB() ),
        
        BaseForegroundColor1( new Color( 165, 42, 0 ).getRGB() ),
        BaseForegroundColor2( new Color( 63, 63, 63 ).getRGB() ),
        BaseForegroundColor3( new Color( 255, 255, 255 ).getRGB() ),
        BaseAmoebaColor( new Color( 0, 255, 0 ).getRGB() );
        
        public final int color;
        
        private CaveColors( int color ) {
            this.color = color;
        }
    }
    
    public static final int DEFAULT_CAVE_WIDTH = 40;
    public static final int DEFAULT_CAVE_HEIGHT = 22;
    public static final int DEFAULT_INTERMISSION_WIDTH = 20;
    public static final int DEFAULT_INTERMISSION_HEIGHT = 12;

    private boolean intermission = false;
    private int updateRate = 10;
    private IColorFilter colorFilter = null;
    
    private int caveWidth = -1;
    private int caveHeight = -1;
    private String caveDataString;
    
    private String caveName = "NO NAME";
    private int timeBackup = 50;
    private int time = 50;

    private int diamondsToCollect = 0;
    private int diamondsCollected;
    private int neededDiamondPoints = 10;
    private int extraDiamondPoints = 20; 
    private int extraSecondScore = 2;

    private float amoebaSlowGrowthProb = 0.03f;
    private float amoebaFastGrowthProb = 0.25f;
    private float amoebaThreshold = 0.227f;
    private int amoebaTime = 999;
    
    private int magicWallActivTime = 50;

    private boolean modified = true;
    

    public boolean isModified() {
        return modified;
    }

    public void setModified( boolean modified ) {
        this.modified = modified;
    }

    public final int getUpdateRate() {
        return updateRate;
    }

    public final void setUpdateRate( int updateRate ) {
        this.updateRate = updateRate;
    }

    public int getCaveWidth() {
        if ( caveWidth < 0 ) {            
            if ( intermission ) {
                return DEFAULT_INTERMISSION_WIDTH;
            } else {
                return DEFAULT_CAVE_WIDTH;
            }
        }
        return caveWidth;
    }

    public CaveData setCaveWidth( int caveWidth ) {
        this.caveWidth = caveWidth;
        return this;
    }

    public int getCaveHeight() {
        if ( caveHeight < 0 ) {
            if ( intermission ) {
                return DEFAULT_INTERMISSION_HEIGHT;
            } else {
                return DEFAULT_CAVE_HEIGHT;
            }
        }
        return caveHeight;
    }

    public CaveData setCaveHeight( int caveHeight ) {
        this.caveHeight = caveHeight;
        return this;
    }

    public int getTime() {
        return time;
    }

    public CaveData setTime( int time ) {
        this.time = time;
        timeBackup = time;
        return this;
    }
    
    public void tick() {
        if ( time == 0 ) {
            return;
        }
        time--;
        modified = true;
    }
    
    public boolean timeout() {
        return time == 0;
    }

    public int getDiamondsToCollect() {
        return diamondsToCollect;
    }

    public CaveData setDiamondsToCollect( int diamondsToCollect ) {
        this.diamondsToCollect = diamondsToCollect;
        return this;
    }

    public String getCaveDataString() {
        return caveDataString;
    }

    public CaveData setCaveDataString( String caveDataString ) {
        this.caveDataString = caveDataString;
        return this;
    }

    public int getDiamondsCollected() {
        return diamondsCollected;
    }
    
    

    public float getAmoebaSlowGrowthProb() {
        return amoebaSlowGrowthProb;
    }

    public CaveData setAmoebaSlowGrowthProb( float amoebaSlowGrowthProb ) {
        this.amoebaSlowGrowthProb = amoebaSlowGrowthProb;
        return this;
    }

    public float getAmoebaFastGrowthProb() {
        return amoebaFastGrowthProb;
    }

    public CaveData setAmoebaFastGrowthProb( float amoebaFastGrowthProb ) {
        this.amoebaFastGrowthProb = amoebaFastGrowthProb;
        return this;
    }

    public float getAmoebaThreshold() {
        return amoebaThreshold;
    }

    public CaveData setAmoebaThreshold( float amoebaThreshold ) {
        this.amoebaThreshold = amoebaThreshold;
        return this;
    }

    public int getAmoebaTime() {
        return amoebaTime;
    }

    public CaveData setAmoebaTime( int amoebaTime ) {
        this.amoebaTime = amoebaTime;
        return this;
    }


    public int getMagicWallActivTime() {
        return magicWallActivTime;
    }

    public CaveData setMagicWallActivTime( int magicWallActivTime ) {
        this.magicWallActivTime = magicWallActivTime;
        return this;
    }

    public int getNeededDiamondPoints() {
        return neededDiamondPoints;
    }

    public CaveData setNeededDiamondPoints( int neededDiamondPoints ) {
        this.neededDiamondPoints = neededDiamondPoints;
        return this;
    }

    public int getExtraDiamondPoints() {
        return extraDiamondPoints;
    }

    public CaveData setExtraDiamondPoints( int extraDiamondPoints ) {
        this.extraDiamondPoints = extraDiamondPoints;
        return this;
    }

    public String getCaveName() {
        return caveName;
    }

    public CaveData setCaveName( String caveName ) {
        this.caveName = caveName;
        return this;
    }

    public int getExtraSecondScore() {
        return extraSecondScore;
    }

    public CaveData setExtraSecondScore( int extraSecondScore ) {
        this.extraSecondScore = extraSecondScore;
        return this;
    }

    public final IColorFilter getColorFilter() {
        return colorFilter;
    }

    public final void setColorFilter( IColorFilter colorFilter ) {
        this.colorFilter = colorFilter;
    }

    public boolean isIntermission() {
        return intermission;
    }

    public CaveData setIntermission( boolean intermission ) {
        this.intermission = intermission;
        return this;
    }

    public void collectDiamond() {
        diamondsCollected++;
        modified = true;
    }

    public void reset() {
        time = timeBackup;
        diamondsCollected = 0;
        modified = true;
    }
}
