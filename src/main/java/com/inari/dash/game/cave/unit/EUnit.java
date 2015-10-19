package com.inari.dash.game.cave.unit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inari.commons.geom.Direction;
import com.inari.commons.lang.aspect.Aspect;
import com.inari.commons.lang.aspect.AspectBitSet;
import com.inari.commons.lang.indexed.Indexer;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.component.attr.AttributeMap;
import com.inari.firefly.entity.EntityComponent;

public final class EUnit extends EntityComponent {
    
    public static final int COMPONENT_TYPE = Indexer.getIndexForType( EUnit.class, EntityComponent.class );
    
    public static final AttributeKey<UnitType> UNIT_TYPE = new AttributeKey<UnitType>( "unitType", UnitType.class, EUnit.class );
    public static final AttributeKey<Direction> MOVEMENT = new AttributeKey<Direction>( "movement", Direction.class, EUnit.class );
    public static final AttributeKey<AspectBitSet> ASPECTS = new AttributeKey<AspectBitSet>( "aspects", AspectBitSet.class, EUnit.class );
    public static final AttributeKey<Integer> ANIMATION_COUNT = new AttributeKey<Integer>( "animationCount", Integer.class, EUnit.class );
    public static final AttributeKey<Boolean> HIT = new AttributeKey<Boolean>( "hit", Boolean.class, EUnit.class );
    public static final AttributeKey<UnitType> EXPLOSION_TYPE = new AttributeKey<UnitType>( "explosionType", UnitType.class, EUnit.class );
    public static final AttributeKey<UnitType> EXPLODE_TO = new AttributeKey<UnitType>( "explodeTo", UnitType.class, EUnit.class );
    public static final AttributeKey<?>[] ATTRIBUTE_KEYS = new AttributeKey[] { 
        UNIT_TYPE,
        MOVEMENT,
        ASPECTS,
        ANIMATION_COUNT,
        HIT,
        EXPLOSION_TYPE,
        EXPLODE_TO
    };
    
    private UnitType unitType;
    private Direction movement;
    private AspectBitSet aspects;
    private int animationCount;
    private boolean hit;
    private UnitType explosionType;
    private UnitType explodeTo;
    
    public EUnit() {
        unitType = null;
        movement = Direction.NONE;
        animationCount = 0;
        hit = false;
        explosionType = UnitType.EXPLOSION;
        explodeTo = UnitType.SPACE;
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

    public final AspectBitSet getAspects() {
        return aspects;
    }
    
    public final void setAspect( Aspect aspect ) {
        aspects.set( aspect );
    }

    public final void setAspects( AspectBitSet aspects ) {
        this.aspects = aspects;
    }
    
    public final boolean has( Aspect aspect ) {
        if ( aspects == null ) {
            return false;
        }
        
        return aspects.contains( aspect.aspectId() );
    }

    public final int getAnimationCount() {
        return animationCount;
    }

    public final void setAnimationCount( int animationCount ) {
        this.animationCount = animationCount;
    }
    
    public final void incrementAnimationCount() {
        animationCount++;
    }
    
    public final void resetAnimationCount() {
        animationCount = 0;
    }

    public final boolean isHit() {
        return hit;
    }

    public final void setHit( boolean hit ) {
        this.hit = hit;
    }
    
    public final UnitType getExplosionType() {
        return explosionType;
    }

    public final void setExplosionType( UnitType explosionType ) {
        this.explosionType = explosionType;
    }

    public final UnitType getExplodeTo() {
        return explodeTo;
    }

    public final void setExplodeTo( UnitType explodeTo ) {
        this.explodeTo = explodeTo;
    }

    @Override
    public final Set<AttributeKey<?>> attributeKeys() {
        return new HashSet<AttributeKey<?>>( Arrays.asList( ATTRIBUTE_KEYS ) );
    }

    @Override
    public final void fromAttributes( AttributeMap attributes ) {
        unitType = attributes.getValue( UNIT_TYPE, unitType );
        movement = attributes.getValue( MOVEMENT, movement );
        aspects = attributes.getValue( ASPECTS, aspects );
        animationCount = attributes.getValue( ANIMATION_COUNT, animationCount );
        hit = attributes.getValue( HIT, hit );
        explosionType = attributes.getValue( EXPLOSION_TYPE, explosionType );
        explodeTo = attributes.getValue( EXPLODE_TO, explodeTo );
    } 

    @Override
    public final void toAttributes( AttributeMap attributes ) {
        attributes.put( UNIT_TYPE, unitType );
        attributes.put( MOVEMENT, movement );
        attributes.put( ASPECTS, aspects );
        attributes.put( ANIMATION_COUNT, animationCount );
        attributes.put( HIT, hit );
        attributes.put( EXPLOSION_TYPE, explosionType );
        attributes.put( EXPLODE_TO, explodeTo );
    }

}
