package com.inari.dash.game.cave.unit;

import java.util.Map;

import com.inari.dash.game.cave.CaveSystem;
import com.inari.firefly.Disposable;
import com.inari.firefly.FFInitException;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.audio.AudioSystem;
import com.inari.firefly.control.ControllerSystem;
import com.inari.firefly.entity.EntityPrefabSystem;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.state.StateSystem;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFContextInitiable;
import com.inari.firefly.system.view.ViewSystem;

public abstract class UnitHandle implements FFContextInitiable, Disposable {

    protected boolean initialized = false;
    
    protected EntitySystem entitySystem;
    protected EntityPrefabSystem prefabSystem;
    protected CaveSystem caveService;
    protected AssetSystem assetSystem;
    protected ViewSystem viewSystem;
    protected AudioSystem soundSystem;
    protected ControllerSystem controllerSystem;
    protected StateSystem stateSystem;
    
    @Override
    public void init( FFContext context ) throws FFInitException {
        entitySystem = context.getSystem( EntitySystem.SYSTEM_KEY );
        assetSystem = context.getSystem( AssetSystem.SYSTEM_KEY );
        caveService = context.getSystem( CaveSystem.SYSTEM_KEY );
        viewSystem = context.getSystem( ViewSystem.SYSTEM_KEY );
        prefabSystem = context.getSystem( EntityPrefabSystem.SYSTEM_KEY );
        soundSystem = context.getSystem( AudioSystem.SYSTEM_KEY );
        controllerSystem = context.getSystem( ControllerSystem.SYSTEM_KEY );
        stateSystem = context.getSystem( StateSystem.SYSTEM_KEY );
        
        initialized = true;
    }
    
    public abstract UnitType type();
    
    public abstract void initBDCFFTypesMap( Map<String, UnitType> bdcffMap );
    
    public void loadCaveData( FFContext context ) {
        if ( !initialized ) {
            throw new FFInitException( "UnitHandle: " + getClass().getName() + " not initialized" );
        }
    }
    
    public void disposeCaveData( FFContext context ) {
        if ( !initialized ) {
            throw new FFInitException( "UnitHandle: " + getClass().getName() + " not initialized" );
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
