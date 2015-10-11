package com.inari.dash.game.cave.unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.inari.firefly.Disposable;
import com.inari.firefly.asset.AssetNameKey;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFContextInitiable;
import com.inari.firefly.system.FFInitException;

public abstract class UnitHandle implements FFContextInitiable, Disposable {
    
    protected final List<AssetNameKey> caveAssetsToReload = new ArrayList<AssetNameKey>();
    protected boolean initialized = false;
    
    public abstract UnitType type();
    
    public abstract void initBDCFFTypesMap( Map<String, UnitType> bdcffMap );
    
    public void loadAssets( FFContext context ) {
        if ( !initialized ) {
            throw new FFInitException( "UnitHandle: " + getClass().getName() + " not initialized" );
        }
        
        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        
        for ( AssetNameKey assetKey : caveAssetsToReload ) {
            assetSystem.loadAsset( assetKey );
        }
    }
    
    void disposeAssets( FFContext context ) {
        if ( !initialized ) {
            throw new FFInitException( "UnitHandle: " + getClass().getName() + " not initialized" );
        }
        
        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        
        for ( AssetNameKey assetKey : caveAssetsToReload ) {
            assetSystem.disposeAsset( assetKey );
        }
    }
    
    public abstract void createOne( int xGridPos, int yGridPos );

}
