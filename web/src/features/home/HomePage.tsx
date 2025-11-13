import {
  Container,
  Typography,
  Grid,
  Card,
  CardContent,
  CardActions,
  Button,
  Box,
} from '@mui/material';
import {
  Chat,
  Image,
  AudioFile,
  Science,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';

const features = [
  {
    title: 'AI Chat',
    description: 'Have natural conversations with AI, powered entirely in your browser',
    icon: <Chat sx={{ fontSize: 48 }} />,
    path: '/chat',
    color: '#6200EE',
  },
  {
    title: 'Ask Image',
    description: 'Upload images and get detailed analysis and insights',
    icon: <Image sx={{ fontSize: 48 }} />,
    path: '/ask-image',
    color: '#03DAC6',
  },
  {
    title: 'Ask Audio',
    description: 'Process audio files with transcription and analysis',
    icon: <AudioFile sx={{ fontSize: 48 }} />,
    path: '/ask-audio',
    color: '#FF6B6B',
  },
  {
    title: 'Prompt Lab',
    description: 'Use pre-built templates for common AI tasks',
    icon: <Science sx={{ fontSize: 48 }} />,
    path: '/prompt-lab',
    color: '#4ECDC4',
  },
];

export default function HomePage() {
  const navigate = useNavigate();

  return (
    <Container maxWidth="lg">
      <Box sx={{ my: 4 }}>
        <Typography variant="h3" component="h1" gutterBottom align="center">
          Welcome to LocalAI Chat
        </Typography>
        <Typography variant="h6" color="text.secondary" paragraph align="center">
          AI-powered chat with multimodal capabilities - 100% offline and privacy-first
        </Typography>
      </Box>

      <Grid container spacing={3} sx={{ mt: 4 }}>
        {features.map((feature) => (
          <Grid item xs={12} sm={6} md={3} key={feature.title}>
            <Card
              sx={{
                height: '100%',
                display: 'flex',
                flexDirection: 'column',
                transition: 'transform 0.2s',
                '&:hover': {
                  transform: 'translateY(-8px)',
                },
              }}
            >
              <CardContent sx={{ flexGrow: 1 }}>
                <Box
                  sx={{
                    display: 'flex',
                    justifyContent: 'center',
                    mb: 2,
                    color: feature.color,
                  }}
                >
                  {feature.icon}
                </Box>
                <Typography variant="h5" component="h2" gutterBottom align="center">
                  {feature.title}
                </Typography>
                <Typography color="text.secondary" align="center">
                  {feature.description}
                </Typography>
              </CardContent>
              <CardActions>
                <Button
                  fullWidth
                  variant="contained"
                  onClick={() => navigate(feature.path)}
                  sx={{ bgcolor: feature.color }}
                >
                  Get Started
                </Button>
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Box sx={{ mt: 6, p: 3, bgcolor: 'background.paper', borderRadius: 2 }}>
        <Typography variant="h5" gutterBottom>
          Key Features
        </Typography>
        <Grid container spacing={2} sx={{ mt: 2 }}>
          <Grid item xs={12} md={6}>
            <Typography variant="body1" paragraph>
              • <strong>100% Offline</strong> - All processing happens in your browser
            </Typography>
            <Typography variant="body1" paragraph>
              • <strong>Privacy First</strong> - No data sent to external servers
            </Typography>
            <Typography variant="body1" paragraph>
              • <strong>Powerful AI</strong> - Advanced language models running locally
            </Typography>
          </Grid>
          <Grid item xs={12} md={6}>
            <Typography variant="body1" paragraph>
              • <strong>Multi-Modal</strong> - Text, image, and audio support
            </Typography>
            <Typography variant="body1" paragraph>
              • <strong>Progressive Web App</strong> - Install and use offline
            </Typography>
            <Typography variant="body1" paragraph>
              • <strong>Automation</strong> - Create workflows to automate tasks
            </Typography>
          </Grid>
        </Grid>
      </Box>
    </Container>
  );
}
