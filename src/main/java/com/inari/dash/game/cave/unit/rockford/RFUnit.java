package com.inari.dash.game.cave.unit.rockford;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;

public final class RFUnit extends EntityComponent {
    
    public static final EntityComponentTypeKey<RFUnit> TYPE_KEY = EntityComponentTypeKey.create( RFUnit.class );
    
    public enum RFState {
        ENTERING,
        APPEARING,             // Rockford appears in a short explosion where the blinking door was before
        IDLE,                       // Rockford idle state, no move, no animation
        IDLE_BLINKING,     // Rockford eyes are blinking
        IDLE_FRETFUL,       // Rockford is fretful waiting for user interaction
        LEFT,
        RIGHT
        ;
    }

    
    public static final AttributeKey<RFState> STATE = new AttributeKey<RFState>( "state", RFState.class, RFUnit.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        STATE
    };
    
    private RFState state;
    
    
    public RFUnit() {
        super( TYPE_KEY );
        resetAttributes();
    }

    @Override
    public final void resetAttributes() {
        state = RFState.ENTERING;
    }
    
    @Override
    public final Class<RFUnit> componentType() {
        return RFUnit.class;
    }

    public final RFState getState() {
        return state;
    }

    public final void setState( RFState state ) {
        this.state = state;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        state = attributes.getValue( STATE, state );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( STATE, state );
    }

}
