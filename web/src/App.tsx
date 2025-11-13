import { Routes, Route, Navigate } from 'react-router-dom';
import { Box } from '@mui/material';
import Layout from './components/Layout';
import HomePage from './features/home/HomePage';
import ChatPage from './features/chat/ChatPage';
import AskImagePage from './features/askimage/AskImagePage';
import AskAudioPage from './features/askaudio/AskAudioPage';
import PromptLabPage from './features/promptlab/PromptLabPage';
import ModelsPage from './features/models/ModelsPage';
import AutomationPage from './features/automation/AutomationPage';
import SettingsPage from './features/settings/SettingsPage';

function App() {
  return (
    <Box sx={{ display: 'flex', height: '100vh' }}>
      <Layout>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/chat/:sessionId?" element={<ChatPage />} />
          <Route path="/ask-image" element={<AskImagePage />} />
          <Route path="/ask-audio" element={<AskAudioPage />} />
          <Route path="/prompt-lab" element={<PromptLabPage />} />
          <Route path="/models" element={<ModelsPage />} />
          <Route path="/automation" element={<AutomationPage />} />
          <Route path="/settings" element={<SettingsPage />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Layout>
    </Box>
  );
}

export default App;
