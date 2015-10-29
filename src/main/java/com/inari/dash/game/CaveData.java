package com.inari.dash.game;


import com.inari.commons.graphics.RGBColor;
import com.inari.firefly.filter.IColorFilter;


public class CaveData {
    
    public static enum CaveColors {
        Black( RGBColor.create( 0, 0, 0 ).getRGB8888() ), 
        White( RGBColor.create( 255, 255, 255 ).getRGB8888() ), 
        Red( RGBColor.create( 155, 77, 66 ).getRGB8888() ), 
        Cyan( RGBColor.create( 140, 214, 223 ).getRGB8888() ), 
        Purple( RGBColor.create( 157, 84, 196 ).getRGB8888() ), 
        Green( RGBColor.create( 120, 191, 78 ).getRGB8888() ), 
        Blue( RGBColor.create( 40, 40, 183 ).getRGB8888() ), 
        Yellow( RGBColor.create( 233, 245, 130 ).getRGB8888() ), 
        Orange( RGBColor.create( 153, 102, 51 ).getRGB8888() ), 
        Brown( RGBColor.create( 107, 85, 0 ).getRGB8888() ), 
        LightRed( RGBColor.create( 210, 136, 126 ).getRGB8888() ), 
        Gray1( RGBColor.create( 124, 124, 124 ).getRGB8888() ), 
        Gray2( RGBColor.create( 146, 146, 146 ).getRGB8888() ), 
        LightGreen( RGBColor.create( 193, 255, 154 ).getRGB8888() ), 
        LightBlue( RGBColor.create( 159, 221, 255 ).getRGB8888() ), 
        Gray3( RGBColor.create( 193, 193, 193 ).getRGB8888() ),
        
        BaseForegroundColor1( RGBColor.create( 165, 42, 0 ).getRGB8888() ),
        BaseForegroundColor2( RGBColor.create( 63, 63, 63 ).getRGB8888() ),
        BaseForegroundColor3( RGBColor.create( 255, 255, 255 ).getRGB8888() ),
        BaseAmoebaColor( RGBColor.create( 0, 255, 0 ).getRGB8888() );
        
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
        this.caveDataString = this.caveDataString.replaceAll( "\n", "" );
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
