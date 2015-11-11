package com.inari.dash.game.cave;

import java.util.BitSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.inari.commons.event.IEventDispatcher;
import com.inari.commons.geom.Position;
import com.inari.commons.geom.Rectangle;
import com.inari.commons.lang.aspect.AspectSetBuilder;
import com.inari.dash.game.GameData;
import com.inari.dash.game.GameService;
import com.inari.dash.game.GameService.StateChangeName;
import com.inari.dash.game.cave.CaveService.CaveSoundKey;
import com.inari.dash.game.cave.CaveService.CaveState;
import com.inari.dash.game.cave.unit.EUnit;
import com.inari.dash.game.cave.unit.UnitAspect;
import com.inari.dash.game.cave.unit.UnitType;
import com.inari.dash.game.cave.unit.action.UnitActionType;
import com.inari.firefly.Disposable;
import com.inari.firefly.action.event.ActionEvent;
import com.inari.firefly.asset.AssetSystem;
import com.inari.firefly.asset.AssetTypeKey;
import com.inari.firefly.control.Controller;
import com.inari.firefly.entity.ETransform;
import com.inari.firefly.entity.EntitySystem;
import com.inari.firefly.renderer.sprite.ESprite;
import com.inari.firefly.renderer.sprite.SpriteAsset;
import com.inari.firefly.sound.event.SoundEvent;
import com.inari.firefly.sound.event.SoundEvent.Type;
import com.inari.firefly.state.event.WorkflowEvent;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.system.FFTimer;
import com.inari.firefly.system.FFTimer.UpdateScheduler;
import com.inari.firefly.system.FireFly;
import com.inari.firefly.system.LowerSystemFacade;
import com.inari.firefly.system.RenderEvent;
import com.inari.firefly.system.RenderEventListener;
import com.inari.firefly.system.UpdateEvent;
import com.inari.firefly.system.UpdateEventListener;
import com.inari.firefly.system.view.View;
import com.inari.firefly.system.view.ViewSystem;
import com.inari.firefly.task.event.TaskEvent;
import com.inari.firefly.text.EText;
import com.inari.firefly.text.TextSystem;

public final class CaveController extends Controller {
    
    private final FFContext context;
    private final CaveService caveService;
    private final IEventDispatcher eventDispatcher;
    private final EntitySystem entitySystem;
    
    private CaveInitScene caveInitScene = null;
    private UpdateScheduler secondTimer;
    private int initSeconds = 0;
    
    private final int playerTextPos = 0;
    private final int menTextPos = 12;
    private final int caveTextPos = 21;
    
    private final int diamondTextPos = 0;
    private final int collectedTextPos = 7;
    private final int timeTextPos = 12;
    private final int scoreTextPos = 18;
    
    private final int exitEntityId;
    private final int playerEntityId;
    
    
    protected CaveController( int id, FFContext context ) {
        super( id );
        this.context = context;
        caveService = context.getComponent( CaveService.CONTEXT_KEY ); 
        eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
        entitySystem = context.getComponent( EntitySystem.CONTEXT_KEY );
        secondTimer = context.getComponent( FFContext.TIMER ).createUpdateScheduler( 1 );
        exitEntityId = UnitType.EXIT.getHandle().getEntityId();
        playerEntityId = UnitType.ROCKFORD.getHandle().getEntityId();
    }

    @Override
    public final void update( FFTimer timer ) {
        GameData gameData = caveService.getGameData();
        CaveData caveData = caveService.getCaveData();
        
        if ( caveService.caveState == CaveState.INIT ) {
            if ( caveInitScene == null ) {
                caveInitScene = new CaveInitScene( context );
                initHeader( gameData );
            }
            if ( secondTimer.needsUpdate() ) {
                initSeconds ++;
            }
            if ( initSeconds > 5 ) {
                caveInitScene.dispose( context );
                caveInitScene = null;
                caveService.caveState = CaveState.ENTERING;
                playHeader();
            }
            return;
        }
        
        if ( caveService.caveState == CaveState.ENTERING ) {
            EUnit playerUnit = entitySystem.getComponent( playerEntityId, EUnit.class );
            initSeconds = 0;
            if ( playerUnit.has( UnitAspect.ALIVE ) ) {
                caveService.caveState = CaveState.PLAY;
            }
            return;
        }
        
        if ( caveService.caveState == CaveState.PLAY ) {
            initSeconds = 0;
            if ( secondTimer.needsUpdate() ) {
                caveData.tick();
                int caveTime = caveData.getTime();
                if ( caveTime < 10 && caveTime > 0 ) {
                    String soundName = "TIMEOUT" + caveTime;
                    eventDispatcher.notify( new SoundEvent( CaveSoundKey.valueOf( soundName ).id, Type.PLAY_SOUND ) );
                }
                
                if ( caveTime == 0 ) {
                    caveService.caveState = CaveState.LOOSE;
                    EUnit playerUnit = entitySystem.getComponent( playerEntityId, EUnit.class );
                    playerUnit.resetAspect( UnitAspect.ALIVE );
                    initSeconds = 0;
                    return;
                }
            }
            
            EUnit exitUnit = entitySystem.getComponent( exitEntityId, EUnit.class );
            if ( !exitUnit.has( UnitAspect.ACTIVE ) ) {
                boolean enough = caveData.getDiamondsToCollect() == caveData.getDiamondsCollected();
                if ( enough ) {
                    exitUnit.setAspects( AspectSetBuilder.create( UnitAspect.ACTIVE, UnitAspect.WALKABLE ) );
                    eventDispatcher.notify( new ActionEvent( UnitActionType.FLASH.index(), exitEntityId ) );
                    eventDispatcher.notify( new SoundEvent( CaveSoundKey.CRACK.id, Type.PLAY_SOUND ) );
                }
            }
            
            if ( caveData.isModified() || gameData.isModified() ) {
                updatePlayHeader( gameData, caveData );
            }
            
            return;
        }
        
        if ( caveService.caveState == CaveState.WON ) {
            if ( initSeconds <= 0 ) {
                EUnit playerUnit = entitySystem.getComponent( playerEntityId, EUnit.class );
                playerUnit.resetAspect( UnitAspect.ALIVE );
                eventDispatcher.notify( new SoundEvent( CaveService.CaveSoundKey.FINISHED.id, Type.PLAY_SOUND ) );
                initSeconds++;
            }
            int time = caveData.getTime();
            if ( time > 0 ) {
                caveData.tick();
                updatePlayHeader( gameData, caveData );
            } else {
                eventDispatcher.notify( new SoundEvent( CaveService.CaveSoundKey.FINISHED.id, Type.STOP_PLAYING ) );
                if ( gameData.hasNextCave() ) {
                    eventDispatcher.notify( new TaskEvent( TaskEvent.Type.RUN_TASK, GameService.TaskName.NEXT_CAVE.name() ) );
                } else {
                    exitPlay();
                }
                return;
            }
        }
        
        if ( caveService.caveState == CaveState.LOOSE ) {
            if ( initSeconds > 2 ) {
                int lives = gameData.getLives() - 1;
                gameData.setLives( lives );
                if ( lives >= 1 ) {
                    eventDispatcher.notify( new TaskEvent( TaskEvent.Type.RUN_TASK, GameService.TaskName.REPLAY_CAVE.name() ) );
                } else {
                    gameOverHeader();
                }
                initSeconds = 0;
                return;
            }
            if ( secondTimer.needsUpdate() ) {
                initSeconds ++;
            }
            return;
        }
        
        if ( caveService.caveState == CaveState.GAME_OVER ) {
            if ( Gdx.input.isKeyPressed( Input.Keys.ENTER ) || Gdx.input.isKeyPressed( Input.Keys.SPACE ) ) {
                exitPlay();
            }
            return;
        }
    }

    private void exitPlay() {
        eventDispatcher.notify( new WorkflowEvent( 
            GameService.GAME_WORKFLOW_NAME, 
            StateChangeName.EXIT_PLAY.name(), 
            WorkflowEvent.Type.STATE_CHANGE ) 
        );
    }

    
    @Override
    public final void dispose( FFContext context ) {
        // TODO Auto-generated method stub
        
    }
    
    private void initHeader( GameData gameData ) {
        clearHeader();
        char[] charArray = "PLAYER 1".toCharArray();
        char[] headerText = caveService.getHeaderText();
        System.arraycopy( charArray, 0, headerText, playerTextPos, charArray.length );
        charArray = ( gameData.getLives() + " MEN" ).toCharArray();
        System.arraycopy( charArray, 0, headerText, menTextPos, charArray.length );
        charArray = ( "1:A" ).toCharArray();
        System.arraycopy( charArray, 0, headerText, caveTextPos, charArray.length );
    }
    
    private void playHeader() {
        clearHeader();
        updatePlayHeader( caveService.getGameData(), caveService.getCaveData() );
    }
    
    private void gameOverHeader() {
        clearHeader();
        caveService.caveState = CaveState.GAME_OVER;
        TextSystem textSystem = context.getComponent( TextSystem.CONTEXT_KEY );
        ViewSystem viewSystem = context.getComponent( ViewSystem.CONTEXT_KEY );
        entitySystem.getEntityBuilderWithAutoActivation()
            .set( ETransform.VIEW_ID, viewSystem.getViewId( CaveService.HEADER_VIEW_NAME ) )
            .set( ETransform.XPOSITION, 100 )
            .set( ETransform.YPOSITION, 8 )
            .set( EText.FONT_ID, textSystem.getFontId( GameService.GAME_FONT_TEXTURE_KEY.name ) )
            .set( EText.TEXT_STRING, "%%% GAME OVER %%%" )
            .set( EText.TINT_COLOR, GameService.YELLOW_FONT_COLOR )
        .build();
    }
    
    private void updatePlayHeader( GameData gameData, CaveData caveData ) {
        int diamondsToCollect = caveData.getDiamondsToCollect();
        boolean enough = caveData.getDiamondsCollected() >= diamondsToCollect;
        String diamondsToCollectString = 
            ( enough )?
                "%%" : ( ( diamondsToCollect < 10 )? "0" + diamondsToCollect : String.valueOf( diamondsToCollect ) );
        int pointsForDiamond = ( enough )? caveData.getExtraDiamondPoints() : caveData.getNeededDiamondPoints();
        String neededDiamondPointsString = ( pointsForDiamond < 10 )? "0" + pointsForDiamond : String.valueOf( pointsForDiamond );
        char[] charArray = ( diamondsToCollectString + "%" + neededDiamondPointsString ).toCharArray();
        char[] headerText = caveService.getHeaderText();
        System.arraycopy( charArray, 0, headerText, diamondTextPos, charArray.length );
        
        int collected = caveData.getDiamondsCollected();
        String collectedString = ( collected < 10 )? "0" + collected : String.valueOf( collected );
        charArray = collectedString.toCharArray();
        System.arraycopy( charArray, 0, headerText, collectedTextPos, charArray.length );
        
        int time = caveData.getTime();
        String timeString = ( time < 10 )? "00" + time : ( time < 100 )? "0" + time : String.valueOf( time );
        charArray = timeString.toCharArray();
        System.arraycopy( charArray, 0, headerText, timeTextPos, charArray.length );
        
        charArray = "000000".toCharArray();
        char[] score = String.valueOf( gameData.getScore() ).toCharArray();
        System.arraycopy( score, 0, charArray, charArray.length - score.length, score.length );
        System.arraycopy( charArray, 0, headerText, scoreTextPos, charArray.length );
    }
    
    private void clearHeader() {
        char[] headerText = caveService.getHeaderText();
        for ( int i = 0; i < headerText.length; i++ ) {
            headerText[ i ] = ' ';
        }
    }

    private final class CaveInitScene implements RenderEventListener, UpdateEventListener, Disposable {
        
        private int width;
        private int height;
        private int size;
        private final int caveViewId;
        
        private final BitSet introTiles;
        private final ESprite tmpSprite = new ESprite();
        
        private final int[] spriteData = new int[ 4 ];
        
        private final LowerSystemFacade lowerSystem;
        private final IEventDispatcher eventDispatcher;
        private final UpdateScheduler animationTimer;
        private final View caveView;

        CaveInitScene( FFContext context ) {
            lowerSystem = context.getComponent( FFContext.LOWER_SYSTEM_FACADE );
            AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
            ViewSystem viewSystem = context.getComponent( ViewSystem.CONTEXT_KEY );
            eventDispatcher = context.getComponent( FFContext.EVENT_DISPATCHER );
            CaveData caveData = caveService.getCaveData();
            
            eventDispatcher.register( UpdateEvent.class, this );
            eventDispatcher.register( RenderEvent.class, this );

            caveView  = viewSystem.getView( CaveService.CAVE_VIEW_NAME );
            caveViewId = caveView.getId();
            Rectangle viewBounds = caveView.getBounds();
            
            width = caveData.getCaveWidth() * 2;
            height = caveData.getCaveHeight() * 2;
            if ( viewBounds.width / 16 < width ) {
                width = viewBounds.width / 16;
            }
            if ( viewBounds.height / 16 < height ) {
                height = viewBounds.height / 16;
            }
            
            size = width * height;
            introTiles = new BitSet( size );
            for ( int i = 0; i < size; i++ ) {
                introTiles.set( i );
            }
            
            for ( int i = 0; i < 3; i++ ) {
                int offset = 4 * i;
                spriteData[ i ] = assetSystem.getAssetBuilderWithAutoLoad( SpriteAsset.class )
                    .set( SpriteAsset.NAME, "introTileSprite" + i )
                    .set( SpriteAsset.ASSET_GROUP, CaveService.GAME_UNIT_TEXTURE_KEY.group )
                    .set( SpriteAsset.TEXTURE_ID, assetSystem.getAssetId( CaveService.GAME_UNIT_TEXTURE_KEY ) )
                    .set( SpriteAsset.TEXTURE_REGION, new Rectangle( 32 + offset, ( 6 * 32 ) + offset, 16, 16 ) )
                .build().getId();
            }
            spriteData[ 3 ] = 0;
            tmpSprite.setSpriteId( spriteData[ spriteData[ 3 ] ] );
            
            animationTimer = context.getComponent( FFContext.TIMER ).createUpdateScheduler( 10 );
            
            eventDispatcher.notify( new SoundEvent( CaveService.CaveSoundKey.COVER.id, Type.PLAY_SOUND ) );
        }   

        @Override
        public final void update( UpdateEvent event ) {
            int removed = 0;
            while ( removed < 10 && !introTiles.isEmpty() ) {
                int nextIndex = FireFly.RANDOM.nextInt( size );
                if ( introTiles.get( nextIndex ) ) {
                    introTiles.flip( nextIndex );
                    removed++;
                }
            }
            if ( animationTimer.needsUpdate() ) {
                spriteData[ 3 ]++;
                if ( spriteData[ 3 ] >= 3 ) {
                    spriteData[ 3 ] = 0;
                }
                tmpSprite.setSpriteId( spriteData[ spriteData[ 3 ] ] );
            }
        }

        @Override
        public final void render( RenderEvent event ) {
            if ( event.getViewId() != caveViewId ) {
                return;
            }
            Position worldPosition = caveView.getWorldPosition();
            for ( int y = 0; y < height; y++ ) {
                for ( int x = 0; x < width; x++ ) {
                    if ( introTiles.get( x * y ) ) {
                        lowerSystem.renderSprite( tmpSprite, x * 16 + worldPosition.x , y * 16 + worldPosition.y );
                    }
                }
            }
        }

        @Override
        public final void dispose( FFContext context ) {
            eventDispatcher.notify( new SoundEvent( CaveService.CaveSoundKey.COVER.id, Type.STOP_PLAYING ) );
            eventDispatcher.unregister( UpdateEvent.class, this );
            eventDispatcher.unregister( RenderEvent.class, this );
            
            AssetSystem assetSystem = context.getComponent( AssetSystem.CONTEXT_KEY );
            for ( int i = 0; i < 3; i++ ) {
                assetSystem.deleteAsset( new AssetTypeKey( spriteData[ i ], SpriteAsset.class ) );
            }
        }
    }

}