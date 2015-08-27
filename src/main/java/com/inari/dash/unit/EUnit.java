package com.inari.dash.unit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.geom.Direction;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;

public class EUnit extends EntityComponent {
    
    public static final int COMPONENT_TYPE = Indexer.getIndexForType( EUnit.class, EntityComponent.class );
    
    public static final AttributeKey<UnitType> UNIT_TYPE = new AttributeKey<UnitType>( "unitType", UnitType.class, EUnit.class );
    public static final AttributeKey<Direction> MOVEMENT = new AttributeKey<Direction>( "movement", Direction.class, EUnit.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        UNIT_TYPE,
        MOVEMENT
    };
    
    private UnitType unitType;
    private Direction movement;
    
    public EUnit() {
        unitType = null;
        movement = Direction.NONE;
    }
    
    @Override
    public final Class<EUnit> getComponentType() {
        return EUnit.class;
    }
    
    public final UnitType getUnitType() {
        return unitType;
    }
    
    public final void setUnitType( UnitType unitType ) {
        this.unitType = unitType;
    }
    
    public final Direction getMovement() {
        return movement;
    }
    
    public final void setMovement( Direction movement ) {
        this.movement = movement;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        unitType = attributes.getValue( UNIT_TYPE, unitType );
        movement = attributes.getValue( MOVEMENT, movement );
    }

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( UNIT_TYPE, unitType );
        attributes.put( MOVEMENT, movement );
    }


}
