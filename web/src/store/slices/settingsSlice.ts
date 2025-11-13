import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { AppSettings } from '@types/index';

const initialState: AppSettings = {
  theme: 'system',
  defaultModel: '',
  maxResponseLength: 2048,
  temperature: 0.7,
  enableStreaming: true,
  enableNotifications: true,
};

const settingsSlice = createSlice({
  name: 'settings',
  initialState,
  reducers: {
    setTheme: (state, action: PayloadAction<'light' | 'dark' | 'system'>) => {
      state.theme = action.payload;
    },
    setDefaultModel: (state, action: PayloadAction<string>) => {
      state.defaultModel = action.payload;
    },
    setMaxResponseLength: (state, action: PayloadAction<number>) => {
      state.maxResponseLength = action.payload;
    },
    setTemperature: (state, action: PayloadAction<number>) => {
      state.temperature = action.payload;
    },
    setEnableStreaming: (state, action: PayloadAction<boolean>) => {
      state.enableStreaming = action.payload;
    },
    setEnableNotifications: (state, action: PayloadAction<boolean>) => {
      state.enableNotifications = action.payload;
    },
    updateSettings: (state, action: PayloadAction<Partial<AppSettings>>) => {
      return { ...state, ...action.payload };
    },
  },
});

export const {
  setTheme,
  setDefaultModel,
  setMaxResponseLength,
  setTemperature,
  setEnableStreaming,
  setEnableNotifications,
  updateSettings,
} = settingsSlice.actions;

export default settingsSlice.reducer;
