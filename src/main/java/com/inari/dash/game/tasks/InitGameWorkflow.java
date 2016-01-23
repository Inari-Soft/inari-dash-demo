package com.inari.dash.game.tasks;

import com.inari.dash.game.GameExitCondition;
import com.inari.dash.game.GameSystem;
import com.inari.dash.game.StartGameCondition;
import com.inari.firefly.state.StateChange;
import com.inari.firefly.state.StateSystem;
import com.inari.firefly.state.Workflow;
import com.inari.firefly.system.FFContext;
import com.inari.firefly.task.Task;
import com.inari.firefly.task.TaskSystem;
import com.inari.firefly.task.WorkflowEventTrigger;

public class InitGameWorkflow extends Task {
    
    public static final String TASK_NAME = "InitGameWorkflow";
    
    public enum TaskName {
        LOAD_GAME( LoadGame.class, true ),
        LOAD_GAME_SELECTION( LoadGameSelection.class ),
        LOAD_PLAY( LoadPlay.class ),
        LOAD_CAVE( LoadCave.class ),
        NEXT_CAVE( NextCave.class ),
        REPLAY_CAVE( ReplayCave.class ),
        DISPOSE_CAVE( DisposeCave.class ),
        DISPOSE_PLAY( DisposePlay.class ),
        DISPOSE_GAME_SELECTION( DisposeGameSelection.class ),
        DISPOSE_GAME( DisposeGame.class, true );
        
        public boolean removeAfterRun = false;
        public final Class<? extends Task> type;
        
        private TaskName( Class<? extends Task> type ) {
            this.type = type;
        }
        
        private TaskName( Class<? extends Task> type, boolean removeAfterRun ) {
            this.type = type;
            this.removeAfterRun = removeAfterRun;
        }
    }
    
    public enum StateName {
        GAME_SELECTION,
        CAVE_PLAY
    }
    
    public enum StateChangeName {
        EXIT_GAME( StateName.GAME_SELECTION, null ),
        PLAY_CAVE( StateName.GAME_SELECTION, StateName.CAVE_PLAY ),
        EXIT_PLAY( StateName.CAVE_PLAY, StateName.GAME_SELECTION )
        ;
        public final StateName from;
        public final StateName to;
        private StateChangeName( StateName from, StateName to ) {
            this.from = from;
            this.to = to;
        }
    }

   

    protected InitGameWorkflow( int id ) {
        super( id );
    }

    @Override
    public void run( FFContext context ) {
        StateSystem stateSystem = context.getSystem( StateSystem.SYSTEM_KEY );
        TaskSystem taskSystem = context.getSystem( TaskSystem.SYSTEM_KEY );
        
        for ( TaskName taskName : TaskName.values() ) {
            taskSystem.getTaskBuilder()
                .set( Task.NAME, taskName.name() )
                .set( Task.REMOVE_AFTER_RUN, taskName.removeAfterRun )
            .build( taskName.type );
        }
        
        taskSystem.getTaskTriggerBuilder()
            .set( WorkflowEventTrigger.TASK_ID, taskSystem.getTaskId( TaskName.LOAD_GAME.name() ) )
            .set( WorkflowEventTrigger.WORKFLOW_NAME, GameSystem.GAME_WORKFLOW_NAME )
            .set( WorkflowEventTrigger.TRIGGER_TYPE, WorkflowEventTrigger.Type.ENTER_STATE )
            .set( WorkflowEventTrigger.TRIGGER_NAME, StateName.GAME_SELECTION.name() )
        .buildAndNext( WorkflowEventTrigger.class )
            .set( WorkflowEventTrigger.TASK_ID, taskSystem.getTaskId( TaskName.DISPOSE_GAME.name() ) )
            .set( WorkflowEventTrigger.WORKFLOW_NAME, GameSystem.GAME_WORKFLOW_NAME )
            .set( WorkflowEventTrigger.TRIGGER_TYPE, WorkflowEventTrigger.Type.STATE_CHANGE )
            .set( WorkflowEventTrigger.TRIGGER_NAME, StateChangeName.EXIT_GAME.name() )
        .buildAndNext( WorkflowEventTrigger.class )
            .set( WorkflowEventTrigger.TASK_ID, taskSystem.getTaskId( TaskName.LOAD_PLAY.name() ) )
            .set( WorkflowEventTrigger.WORKFLOW_NAME, GameSystem.GAME_WORKFLOW_NAME )
            .set( WorkflowEventTrigger.TRIGGER_TYPE, WorkflowEventTrigger.Type.STATE_CHANGE )
            .set( WorkflowEventTrigger.TRIGGER_NAME, StateChangeName.PLAY_CAVE.name() )
        .buildAndNext( WorkflowEventTrigger.class )
            .set( WorkflowEventTrigger.TASK_ID, taskSystem.getTaskId( TaskName.DISPOSE_PLAY.name() ) )
            .set( WorkflowEventTrigger.WORKFLOW_NAME, GameSystem.GAME_WORKFLOW_NAME )
            .set( WorkflowEventTrigger.TRIGGER_TYPE, WorkflowEventTrigger.Type.STATE_CHANGE )
            .set( WorkflowEventTrigger.TRIGGER_NAME, StateChangeName.EXIT_PLAY.name() )
        .build( WorkflowEventTrigger.class );
        
        stateSystem.getWorkflowBuilder()
            .set( Workflow.NAME, GameSystem.GAME_WORKFLOW_NAME )
            .set( Workflow.START_STATE_NAME, StateName.GAME_SELECTION.name() )
            .add( Workflow.STATES, StateName.GAME_SELECTION.name() )
            .add( Workflow.STATES, StateName.CAVE_PLAY.name() )
            .add( 
                Workflow.STATE_CHANGES, 
                new StateChange( 
                    StateChangeName.EXIT_GAME.name(), 
                    StateName.GAME_SELECTION.name(), 
                    null,
                    new GameExitCondition() 
                ) 
            )
            .add( 
                Workflow.STATE_CHANGES, 
                new StateChange( 
                    StateChangeName.PLAY_CAVE.name(), 
                    StateName.GAME_SELECTION.name(), 
                    StateName.CAVE_PLAY.name(), 
                    new StartGameCondition() 
                ) 
            )
            .add( 
                Workflow.STATE_CHANGES, 
                new StateChange( 
                    StateChangeName.EXIT_PLAY.name(), 
                    StateName.GAME_SELECTION.name(), 
                    StateName.GAME_SELECTION.name()
                ) 
            )
        .activate();
    }

}
