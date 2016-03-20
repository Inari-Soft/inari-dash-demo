package com.inari.dash.game.tasks;

import com.inari.dash.game.GameExitCondition;
import com.inari.dash.game.GameSystem;
import com.inari.dash.game.StartGameCondition;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.state.StateChange;
import com.inari.firefly.state.StateSystem;
import com.inari.firefly.state.Workflow;
import com.inari.firefly.task.Task;
import com.inari.firefly.task.TaskSystem;
import com.inari.firefly.task.WorkflowEventTrigger;

public class InitGameWorkflow extends Task {
    
    public static final String TASK_NAME = "InitGameWorkflow";
    
    public enum TaskName {
        LOAD_GAME( 
            LoadGame.class, 
            true,
            new WorkflowEventTrigger( GameSystem.GAME_WORKFLOW_NAME, WorkflowEventTrigger.Type.ENTER_STATE, StateName.GAME_SELECTION.name() )
        ),
        LOAD_GAME_SELECTION( LoadGameSelection.class ),
        LOAD_PLAY( 
            LoadPlay.class,
            false,
            new WorkflowEventTrigger( GameSystem.GAME_WORKFLOW_NAME, WorkflowEventTrigger.Type.STATE_CHANGE, StateChangeName.PLAY_CAVE.name() )
        ),
        LOAD_CAVE( LoadCave.class ),
        NEXT_CAVE( NextCave.class ),
        REPLAY_CAVE( ReplayCave.class ),
        DISPOSE_CAVE( DisposeCave.class ),
        DISPOSE_PLAY( 
            DisposePlay.class,
            false,
            new WorkflowEventTrigger( GameSystem.GAME_WORKFLOW_NAME, WorkflowEventTrigger.Type.STATE_CHANGE, StateChangeName.EXIT_PLAY.name() )
        ),
        DISPOSE_GAME_SELECTION( DisposeGameSelection.class ),
        DISPOSE_GAME( 
            DisposeGame.class, 
            true,
            new WorkflowEventTrigger( GameSystem.GAME_WORKFLOW_NAME, WorkflowEventTrigger.Type.STATE_CHANGE, StateChangeName.EXIT_GAME.name() )
        );
        
        public final boolean removeAfterRun;
        public final Class<? extends Task> type;
        public final WorkflowEventTrigger trigger;
        
        private TaskName( Class<? extends Task> type ) {
            this.type = type;
            this.removeAfterRun = false;
            trigger = null;
        }
        
        private TaskName( Class<? extends Task> type, boolean removeAfterRun, WorkflowEventTrigger trigger ) {
            this.type = type;
            this.removeAfterRun = removeAfterRun;
            this.trigger = trigger;
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
    public void runTask() {
        StateSystem stateSystem = context.getSystem( StateSystem.SYSTEM_KEY );
        TaskSystem taskSystem = context.getSystem( TaskSystem.SYSTEM_KEY );
        
        for ( TaskName taskName : TaskName.values() ) {
            ComponentBuilder taskBuilder = taskSystem.getTaskBuilder()
                .set( Task.NAME, taskName.name() )
                .set( Task.REMOVE_AFTER_RUN, taskName.removeAfterRun );
            
            if ( taskName.trigger != null ) {
                taskBuilder.add( Task.TRIGGERS, taskName.trigger );
            }
            taskBuilder.build( taskName.type );
        }
        
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
