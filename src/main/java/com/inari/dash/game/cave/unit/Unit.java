package com.inari.dash.game.cave.unit;

import java.util.Map;

import com.inari.commons.lang.aspect.AspectGroup;
import com.inari.commons.lang.list.DynArray;
import com.inari.dash.game.cave.CaveSystem;
import com.inari.firefly.component.ComponentId;
import com.inari.firefly.component.attr.Attribute;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.component.attr.ComponentAttributeMap;
import com.inari.firefly.entity.EntitySystem.Entity;
import com.inari.firefly.prototype.Prototype;

public abstract class Unit extends Prototype {

    public static final AspectGroup UNIT_ASPECT_GROUP = new AspectGroup( "UNIT_ASPECT_GROUP" );
    
    public static final AttributeKey<Integer> GRID_X = new AttributeKey<Integer>( "gridX", Integer.class, Unit.class );
    public static final AttributeKey<Integer> GRID_Y = new AttributeKey<Integer>( "gridY", Integer.class, Unit.class );
    public static final AttributeKey<String> TYPE = new AttributeKey<String>( "type", String.class, Unit.class );
    
    private static final AttributeMap ATTRIBUTES = new ComponentAttributeMap( null );
    private static final Attribute GRID_X_ATTR = new Attribute( GRID_X, -1 );
    private static final Attribute GRID_Y_ATTR = new Attribute( GRID_Y, -1 );
    private static final Attribute TYPE_ATTR = new Attribute( TYPE, null );

    protected static final DynArray<ComponentId> RESULT = new DynArray<ComponentId>();

    protected Unit( int id ) {
        super( id );
    }

    public abstract UnitType type();
    
    public int getSoundId() {
        throw new UnsupportedOperationException( "Not supported for type: " + type() );
    }
    
    public int getEntityId() {
        throw new UnsupportedOperationException( "Not supported for type: " + type() );
    }
    
    public abstract void initBDCFFTypesMap( Map<String, UnitType> bdcffMap );
    
    @Override
    public final DynArray<ComponentId> createOne( AttributeMap attributes ) {
        int xGridPos = attributes.getValue( Unit.GRID_X, -1 );
        int yGridPos = attributes.getValue( Unit.GRID_Y, -1 );
        String type = attributes.getValue( Unit.TYPE, null );
        
        int entityId = createOne( xGridPos, yGridPos, type );
        
        RESULT.set( 0, new ComponentId( Entity.ENTITY_TYPE_KEY, entityId ) );
        return RESULT;
    }
    
    protected final float getUpdateRate() {
        return context.getSystem( CaveSystem.SYSTEM_KEY ).getUpdateRate();
    }
    
    public final int createOne( int xGridPos, int yGridPos ) {
        return createOne( xGridPos, yGridPos, null );
    }
    
    public abstract int createOne( int xGridPos, int yGridPos, String type );
    
    public static final AttributeMap getAttributes( int gridX, int gridY ) {
        return getAttributes( gridX, gridY, null );
    }
    
    public static final AttributeMap getAttributes( int gridX, int gridY, String type ) {
        GRID_X_ATTR.setValue( gridX );
        GRID_Y_ATTR.setValue( gridY );
        TYPE_ATTR.setValue( type );
        return ATTRIBUTES;
    }

}
