
package com.inari.dash;

import java.util.Collection;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.inari.dash.game.GameExitCondition;
import com.inari.dash.game.GameService;
import com.inari.dash.game.GameService.StateChangeName;
import com.inari.dash.game.GameService.StateName;
import com.inari.dash.game.GameService.TaskName;
import com.inari.dash.game.StartGameCondition;
import com.inari.dash.game.io.GameInfos;
import com.inari.firefly.component.attr.AttributeKey;
import com.inari.firefly.libgdx.GDXConfiguration;
import com.inari.firefly.libgdx.GDXFFApplicationAdapter;
import com.inari.firefly.state.State;
import com.inari.firefly.state.StateChange;
import com.inari.firefly.state.StateSystem;
import com.inari.firefly.state.Workflow;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;
import com.inari.firefly.task.TaskSystem;

public class InariDash extends GDXFFApplicationAdapter {

    @Override
    public String getTitle() {
        return "Inari Dash";
    }
    
    public static void main (String[] arg) {
        try {
            LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
            config.resizable = false;
            config.width = 800;
            config.height = 600;
            new LwjglApplication( new InariDash(), config );
        } catch ( Throwable t ) {
            t.printStackTrace();
        }
    }
    

    @Override
    protected Collection<AttributeKey<?>> getDynamicAttributes() {
        Collection<AttributeKey<?>> dynamicAttributes = super.getDynamicAttributes();
        dynamicAttributes.add( GDXConfiguration.DynamicAttributes.TEXTURE_COLOR_FILTER_NAME );
        return dynamicAttributes;
    }

    @Override
    protected final void init( FFContext context ) {
        Configuration configuration = new Configuration();
        GameInfos gameInfos = new GameInfos();
        gameInfos.load( context );
        GameService gameService = new GameService( context, configuration, gameInfos );
        context.putComponent( GameService.CONTEXT_KEY, gameService );
        
        initWorkflow( context );
    }

    @Override
    protected void resize( int width, int height, FFContext context ) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void pause( FFContext context ) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void resume( FFContext context ) {
        // TODO Auto-generated method stub
    }
    
    private void initWorkflow( FFContext context ) {
        StateSystem stateSystem = context.getComponent( StateSystem.CONTEXT_KEY );
        TaskSystem taskSystem = context.getComponent( TaskSystem.CONTEXT_KEY );
        
        for ( TaskName taskName : TaskName.values() ) {
            taskSystem.getTaskBuilder( taskName.type )
                .set( Task.NAME, taskName.name() )
                .set( Task.REMOVE_AFTER_RUN, taskName.removeAfterRun )
            .build();
        }
        
        int workflowId = stateSystem.getWorkflowBuilder()
            .set( Workflow.NAME, GameService.GAME_WORKFLOW_NAME )
            .set( Workflow.START_STATE_NAME, StateName.GAME_SELECTION.name() )
            .set( Workflow.INIT_TASK_ID, taskSystem.getTaskId( TaskName.LOAD_GAME.name() ) )
        .build().getId();
        
        stateSystem.getStateBuilder()
            .set( State.NAME, StateName.GAME_SELECTION.name() )
            .set( State.WORKFLOW_ID, workflowId )
        .buildAndNext()
            .set( State.NAME, StateName.CAVE_PLAY.name() )
            .set( State.WORKFLOW_ID, workflowId )
        .build();
        
        stateSystem.getStateChangeBuilder()
            .set( StateChange.NAME, StateChangeName.EXIT_GAME.name() )
            .set( StateChange.WORKFLOW_ID, workflowId )
            .set( StateChange.CONDITION_TYPE_NAME, GameExitCondition.class.getName() )
            .set( StateChange.FORM_STATE_ID, stateSystem.getStateId( StateName.GAME_SELECTION.name() ) )
            .set( StateChange.TASK_ID, taskSystem.getTaskId( TaskName.DISPOSE_GAME.name() ) )
        .buildAndNext()
            .set( StateChange.NAME, StateChangeName.PLAY_CAVE.name() )
            .set( StateChange.WORKFLOW_ID, workflowId )
            .set( StateChange.CONDITION_TYPE_NAME, StartGameCondition.class.getName() )
            .set( StateChange.FORM_STATE_ID, stateSystem.getStateId( StateName.GAME_SELECTION.name() ) )
            .set( StateChange.TO_STATE_ID, stateSystem.getStateId( StateName.CAVE_PLAY.name() ) )
            .set( StateChange.TASK_ID, taskSystem.getTaskId( TaskName.LOAD_PLAY.name() ) )
        .buildAndNext()
            .set( StateChange.NAME, StateChangeName.EXIT_PLAY.name() )
            .set( StateChange.WORKFLOW_ID, workflowId )
            .set( StateChange.FORM_STATE_ID, stateSystem.getStateId( StateName.CAVE_PLAY.name() ) )
            .set( StateChange.TO_STATE_ID, stateSystem.getStateId( StateName.GAME_SELECTION.name() ) )
            .set( StateChange.TASK_ID, taskSystem.getTaskId( TaskName.DISPOSE_PLAY.name() ) )
        .build();
        
        stateSystem.activateWorkflow( workflowId );
    }

}
