import { configureStore } from '@reduxjs/toolkit';
import chatReducer from './slices/chatSlice';
import modelsReducer from './slices/modelsSlice';
import settingsReducer from './slices/settingsSlice';
import automationReducer from './slices/automationSlice';

export const store = configureStore({
  reducer: {
    chat: chatReducer,
    models: modelsReducer,
    settings: settingsReducer,
    automation: automationReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        // Ignore these action types
        ignoredActions: ['chat/sendMessage'],
        // Ignore these field paths in all actions
        ignoredActionPaths: ['payload.generator'],
        // Ignore these paths in the state
        ignoredPaths: ['chat.streamGenerator'],
      },
    }),
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
