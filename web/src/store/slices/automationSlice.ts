import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { Workflow, WorkflowExecution } from '@types/index';

interface AutomationState {
  workflows: Workflow[];
  executions: WorkflowExecution[];
  isLoading: boolean;
  error: string | null;
}

const initialState: AutomationState = {
  workflows: [],
  executions: [],
  isLoading: false,
  error: null,
};

const automationSlice = createSlice({
  name: 'automation',
  initialState,
  reducers: {
    setWorkflows: (state, action: PayloadAction<Workflow[]>) => {
      state.workflows = action.payload;
    },
    addWorkflow: (state, action: PayloadAction<Workflow>) => {
      state.workflows.push(action.payload);
    },
    updateWorkflow: (state, action: PayloadAction<Workflow>) => {
      const index = state.workflows.findIndex((w) => w.id === action.payload.id);
      if (index !== -1) {
        state.workflows[index] = action.payload;
      }
    },
    removeWorkflow: (state, action: PayloadAction<string>) => {
      state.workflows = state.workflows.filter((w) => w.id !== action.payload);
    },
    toggleWorkflow: (state, action: PayloadAction<string>) => {
      const workflow = state.workflows.find((w) => w.id === action.payload);
      if (workflow) {
        workflow.enabled = !workflow.enabled;
      }
    },
    setExecutions: (state, action: PayloadAction<WorkflowExecution[]>) => {
      state.executions = action.payload;
    },
    addExecution: (state, action: PayloadAction<WorkflowExecution>) => {
      state.executions.unshift(action.payload);
    },
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.isLoading = action.payload;
    },
    setError: (state, action: PayloadAction<string | null>) => {
      state.error = action.payload;
    },
  },
});

export const {
  setWorkflows,
  addWorkflow,
  updateWorkflow,
  removeWorkflow,
  toggleWorkflow,
  setExecutions,
  addExecution,
  setLoading,
  setError,
} = automationSlice.actions;

export default automationSlice.reducer;
