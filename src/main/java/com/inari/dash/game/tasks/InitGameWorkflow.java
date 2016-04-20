package com.inari.dash.game.tasks;

import com.inari.dash.game.GameExitCondition;
import com.inari.dash.game.GameSystem;
import com.inari.dash.game.StartGameCondition;
import com.inari.firefly.component.build.ComponentBuilder;
import com.inari.firefly.control.state.StateChange;
import com.inari.firefly.control.state.StateSystem;
import com.inari.firefly.control.state.Workflow;
import com.inari.firefly.control.task.Task;
import com.inari.firefly.control.task.TaskSystem;
import com.inari.firefly.control.task.WorkflowTaskTrigger;
import com.inari.firefly.system.Condition;

public class InitGameWorkflow extends Task {
    
    public static final String TASK_NAME = "InitGameWorkflow";
    
    public enum TaskName {
        LOAD_GAME( 
            LoadGame.class, 
            true,
            new WorkflowTaskTrigger( 
                GameSystem.GAME_WORKFLOW_NAME, 
                WorkflowTaskTrigger.Type.ENTER_STATE, 
                StateName.GAME_SELECTION.name() 
            )
        ),
        LOAD_GAME_SELECTION( LoadGameSelection.class ),
        LOAD_PLAY( 
            LoadPlay.class,
            false,
            new WorkflowTaskTrigger( 
                GameSystem.GAME_WORKFLOW_NAME, 
                WorkflowTaskTrigger.Type.STATE_CHANGE, 
                StateChangeName.PLAY_CAVE.name() 
            )
        ),
        LOAD_CAVE( LoadCave.class ),
        NEXT_CAVE( NextCave.class ),
        REPLAY_CAVE( ReplayCave.class ),
        DISPOSE_CAVE( DisposeCave.class ),
        DISPOSE_PLAY( 
            DisposePlay.class,
            false,
            new WorkflowTaskTrigger( 
                GameSystem.GAME_WORKFLOW_NAME, 
                WorkflowTaskTrigger.Type.STATE_CHANGE, 
                StateChangeName.EXIT_PLAY.name() 
            )
        ),
        DISPOSE_GAME_SELECTION( DisposeGameSelection.class ),
        DISPOSE_GAME( 
            DisposeGame.class, 
            true,
            new WorkflowTaskTrigger( 
                GameSystem.GAME_WORKFLOW_NAME, 
                WorkflowTaskTrigger.Type.STATE_CHANGE, 
                StateChangeName.EXIT_GAME.name()
            )
        );
        
        public final boolean removeAfterRun;
        public final Class<? extends Task> type;
        public final WorkflowTaskTrigger trigger;
        
        private TaskName( Class<? extends Task> type ) {
            this.type = type;
            this.removeAfterRun = false;
            trigger = null;
        }
        
        private TaskName( Class<? extends Task> type, boolean removeAfterRun, WorkflowTaskTrigger trigger ) {
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
        EXIT_GAME( StateName.GAME_SELECTION, null, new GameExitCondition() ),
        PLAY_CAVE( StateName.GAME_SELECTION, StateName.CAVE_PLAY, new StartGameCondition() ),
        EXIT_PLAY( StateName.CAVE_PLAY, StateName.GAME_SELECTION, null )
        ;
        public final StateName from;
        public final StateName to;
        public final Condition condition;
        private StateChangeName( StateName from, StateName to, Condition condition ) {
            this.from = from;
            this.to = to;
            this.condition = condition;
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
                taskBuilder.set( Task.TRIGGER, taskName.trigger );
            }
            taskBuilder.build( taskName.type );
        }
        
        ComponentBuilder workflowBuilder = stateSystem.getWorkflowBuilder()
            .set( Workflow.NAME, GameSystem.GAME_WORKFLOW_NAME )
            .set( Workflow.START_STATE_NAME, StateName.GAME_SELECTION.name() )
            .add( Workflow.STATES, StateName.GAME_SELECTION.name() )
            .add( Workflow.STATES, StateName.CAVE_PLAY.name() );
        
        for ( StateChangeName stateChangeName : StateChangeName.values() ) {
            workflowBuilder.add( 
                Workflow.STATE_CHANGES, 
                new StateChange( 
                    stateChangeName.name(), 
                    stateChangeName.from.name(),
                    ( stateChangeName.to != null )? stateChangeName.to.name() : null,
                    stateChangeName.condition
                ) 
            );
        }

        workflowBuilder.activate();
    }

}
