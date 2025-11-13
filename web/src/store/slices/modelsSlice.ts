import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { Model } from '@types/index';

interface ModelsState {
  models: Model[];
  activeModelId: string | null;
  isLoading: boolean;
  error: string | null;
}

const initialState: ModelsState = {
  models: [],
  activeModelId: null,
  isLoading: false,
  error: null,
};

const modelsSlice = createSlice({
  name: 'models',
  initialState,
  reducers: {
    setModels: (state, action: PayloadAction<Model[]>) => {
      state.models = action.payload;
    },
    addModel: (state, action: PayloadAction<Model>) => {
      state.models.push(action.payload);
    },
    updateModel: (state, action: PayloadAction<Model>) => {
      const index = state.models.findIndex((m) => m.id === action.payload.id);
      if (index !== -1) {
        state.models[index] = action.payload;
      }
    },
    removeModel: (state, action: PayloadAction<string>) => {
      state.models = state.models.filter((m) => m.id !== action.payload);
    },
    setActiveModel: (state, action: PayloadAction<string | null>) => {
      state.activeModelId = action.payload;
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
  setModels,
  addModel,
  updateModel,
  removeModel,
  setActiveModel,
  setLoading,
  setError,
} = modelsSlice.actions;

export default modelsSlice.reducer;
