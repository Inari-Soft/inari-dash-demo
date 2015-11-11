package com.inari.dash.game.cave.unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.inari.dash.game.cave.CaveService;
import com.inari.firefly.Disposable;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.asset.AssetTypeKey;
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.entity.EntityPrefabSystem;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.sound.SoundSystem;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFContextInitiable;
import com.inari.firefly.system.FFInitException;
import com.inari.firefly.system.view.ViewSystem;

public abstract class UnitHandle implements FFContextInitiable, Disposable {

    protected final List<AssetTypeKey> caveAssetsToReload = new ArrayList<AssetTypeKey>();
    protected boolean initialized = false;
    
    protected EntitySystem entitySystem;
    protected EntityPrefabSystem prefabSystem;
    protected CaveService caveService;
    protected AssetSystem assetSystem;
    protected ViewSystem viewSystem;
    protected SoundSystem soundSystem;
    protected ControllerSystem controllerSystem;
    
    @Override
    public void init( FFContext context ) throws FFInitException {
        entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );
        assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        caveService = context.getComponent( CaveService.CONTEXT_KEY );
        viewSystem = context.getComponent( ViewSystem.CONTEXT_KEY );
        prefabSystem = context.getComponent( EntityPrefabSystem.CONTEXT_KEY );
        soundSystem = context.getComponent( SoundSystem.CONTEXT_KEY );
        controllerSystem = context.getComponent( ControllerSystem.CONTEXT_KEY );
        
        initialized = true;
    }
    
    public abstract UnitType type();
    
    public abstract void initBDCFFTypesMap( Map<String, UnitType> bdcffMap );
    
    public void loadCaveData( FFContext context ) {
        if ( !initialized ) {
            throw new FFInitException( "UnitHandle: " + getClass().getName() + " not initialized" );
        }
        
        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        
        for ( AssetTypeKey assetKey : caveAssetsToReload ) {
            assetSystem.loadAsset( assetKey );
        }
    }
    
    public void disposeCaveData( FFContext context ) {
        if ( !initialized ) {
            throw new FFInitException( "UnitHandle: " + getClass().getName() + " not initialized" );
        }
        
        AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
        
        for ( AssetTypeKey assetKey : caveAssetsToReload ) {
            assetSystem.disposeAsset( assetKey );
        }
    }
    
    public int getSoundId() {
        return -1;
    }
    
    public int createOne( String type, int xGridPos, int yGridPos ) {
        return createOne( xGridPos, yGridPos );
    }
    
    public int getEntityId() {
        throw new UnsupportedOperationException( "Not supported for type: " + this.getClass() );
    }
    
    public abstract int createOne( int xGridPos, int yGridPos );

}