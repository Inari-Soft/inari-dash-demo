package com.inari.dash.game.cave.unit;

import com.inari.dash.game.cave.CaveSystem;
import com.inari.firefly.FFInitException;
import com.inari.firefly.audio.AudioSystem;
import com.inari.firefly.entity.EntityController;

public abstract class UnitController extends EntityController {
    
    protected CaveSystem caveService;
    protected AudioSystem soundSystem;
    

    protected UnitController( int id ) {
        super( id );
        
    }

    @Override
    public void init() throws FFInitException {
        super.init();
        
        caveService = context.getSystem( CaveSystem.SYSTEM_KEY );
        soundSystem = context.getSystem( AudioSystem.SYSTEM_KEY ); 
    }

}
