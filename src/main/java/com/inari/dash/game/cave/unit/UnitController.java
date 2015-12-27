package com.inari.dash.game.cave.unit;

import com.inari.dash.game.cave.CaveSystem;
import com.inari.firefly.audio.AudioSystem;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.entity.EntityController;
import com.inari.firefly.system.FFContext;

public abstract class UnitController extends EntityController {
    
    protected CaveSystem caveService;
    protected AudioSystem soundSystem;
    

    protected UnitController( int id, FFContext context ) {
        super( id, context );
        caveService = context.getSystem( CaveSystem.SYSTEM_KEY );
        soundSystem = context.getSystem( AudioSystem.SYSTEM_KEY ); 
    }

    @Override
    public AttributeKey<?>[] getControlledAttribute() {
        return null;
    }

}
