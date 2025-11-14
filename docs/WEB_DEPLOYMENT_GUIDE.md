# Web Deployment Guide

Complete guide for deploying the LocalAI Chat web application to various hosting platforms.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Build Process](#build-process)
- [Deployment Platforms](#deployment-platforms)
- [Environment Configuration](#environment-configuration)
- [Custom Domain Setup](#custom-domain-setup)
- [Performance Optimization](#performance-optimization)
- [Monitoring & Analytics](#monitoring--analytics)

## Prerequisites

### Development Environment
- Node.js 18+ installed
- npm 9+ or yarn 1.22+
- Git configured
- Code editor (VS Code recommended)

### Production Requirements
- HTTPS certificate (required for PWA)
- Modern hosting platform
- CDN (optional but recommended)

## Build Process

### Local Build

```bash
# Navigate to web directory
cd web

# Install dependencies
npm install

# Type check
npm run type-check

# Run tests
npm run test

# Create production build
npm run build

# Preview production build
npm run preview
```

### Build Output

```
dist/
├── index.html           # Entry HTML
├── assets/
│   ├── index.[hash].js # Main app bundle
│   ├── index.[hash].css # Styles
│   └── *.chunk.js      # Code-split chunks
├── manifest.json        # PWA manifest
├── sw.js               # Service worker
└── workbox-*.js        # Workbox runtime
```

### Build Optimization

The build is optimized with:
- **Code Splitting** - Separate vendor bundles
- **Tree Shaking** - Remove unused code
- **Minification** - Compress JS/CSS
- **Asset Optimization** - Compress images
- **Lazy Loading** - Load routes on demand

## Deployment Platforms

### 1. Vercel (Recommended)

**Why Vercel?**
- Zero configuration
- Automatic HTTPS
- Global CDN
- Serverless functions support
- Free tier available

**Deployment Steps:**

```bash
# Install Vercel CLI
npm i -g vercel

# Login
vercel login

# Deploy
cd web
vercel

# Production deployment
vercel --prod
```

**Or use GitHub integration:**

1. Push code to GitHub
2. Import project in Vercel dashboard
3. Configure:
   - **Framework Preset**: Vite
   - **Root Directory**: `web`
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`
4. Deploy

**vercel.json** configuration:
```json
{
  "buildCommand": "npm run build",
  "outputDirectory": "dist",
  "framework": "vite",
  "rewrites": [
    {
      "source": "/(.*)",
      "destination": "/index.html"
    }
  ],
  "headers": [
    {
      "source": "/sw.js",
      "headers": [
        {
          "key": "Service-Worker-Allowed",
          "value": "/"
        }
      ]
    }
  ]
}
```

### 2. Netlify

**Deployment via CLI:**

```bash
# Install Netlify CLI
npm install -g netlify-cli

# Login
netlify login

# Initialize
netlify init

# Deploy
netlify deploy --prod
```

**netlify.toml** configuration:
```toml
[build]
  base = "web"
  command = "npm run build"
  publish = "dist"

[[redirects]]
  from = "/*"
  to = "/index.html"
  status = 200

[[headers]]
  for = "/sw.js"
  [headers.values]
    Service-Worker-Allowed = "/"
    Cache-Control = "public, max-age=0, must-revalidate"
```

### 3. GitHub Pages

```bash
# Install gh-pages
npm install --save-dev gh-pages

# Add to package.json scripts
"scripts": {
  "deploy": "npm run build && gh-pages -d dist"
}

# Deploy
npm run deploy
```

**Note**: GitHub Pages doesn't support true SPA routing by default. Add `404.html` redirect workaround.

### 4. Firebase Hosting

```bash
# Install Firebase CLI
npm install -g firebase-tools

# Login
firebase login

# Initialize
firebase init hosting

# Deploy
firebase deploy --only hosting
```

**firebase.json**:
```json
{
  "hosting": {
    "public": "web/dist",
    "ignore": ["firebase.json", "**/.*", "**/node_modules/**"],
    "rewrites": [
      {
        "source": "**",
        "destination": "/index.html"
      }
    ],
    "headers": [
      {
        "source": "/sw.js",
        "headers": [
          {
            "key": "Service-Worker-Allowed",
            "value": "/"
          }
        ]
      }
    ]
  }
}
```

### 5. Self-Hosted (Nginx)

**Install Nginx:**
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install nginx

# CentOS/RHEL
sudo yum install nginx
```

**Nginx configuration** (`/etc/nginx/sites-available/localai-chat`):
```nginx
server {
    listen 80;
    server_name your-domain.com;

    # Redirect HTTP to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com;

    # SSL configuration
    ssl_certificate /etc/letsencrypt/live/your-domain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;

    # Security headers
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;

    # Root directory
    root /var/www/localai-chat;
    index index.html;

    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_types text/plain text/css text/xml text/javascript application/x-javascript application/xml+rss application/json;

    # SPA routing
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Service worker
    location /sw.js {
        add_header Service-Worker-Allowed "/";
        add_header Cache-Control "public, max-age=0, must-revalidate";
    }

    # Cache static assets
    location ~* \.(js|css|png|jpg|jpeg|gif|svg|ico|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

**Deploy steps:**
```bash
# Copy files
sudo cp -r dist/* /var/www/localai-chat/

# Set permissions
sudo chown -R www-data:www-data /var/www/localai-chat
sudo chmod -R 755 /var/www/localai-chat

# Enable site
sudo ln -s /etc/nginx/sites-available/localai-chat /etc/nginx/sites-enabled/

# Test configuration
sudo nginx -t

# Reload Nginx
sudo systemctl reload nginx
```

## Environment Configuration

### Environment Variables

Create `.env` files for different environments:

**.env.development**:
```env
VITE_API_BASE_URL=http://localhost:5173
VITE_ENABLE_DEV_TOOLS=true
VITE_LOG_LEVEL=debug
```

**.env.production**:
```env
VITE_API_BASE_URL=https://your-domain.com
VITE_ENABLE_DEV_TOOLS=false
VITE_LOG_LEVEL=error
```

### Using Environment Variables

```typescript
// In code
const apiUrl = import.meta.env.VITE_API_BASE_URL;
const isDev = import.meta.env.DEV;
const isProd = import.meta.env.PROD;
```

## Custom Domain Setup

### DNS Configuration

Add A record or CNAME:

**A Record**:
```
Type: A
Name: @
Value: [Your server IP]
TTL: 3600
```

**CNAME** (for platforms like Vercel):
```
Type: CNAME
Name: www
Value: cname.vercel-dns.com
TTL: 3600
```

### SSL/TLS Certificate

**Using Let's Encrypt (Free)**:

```bash
# Install certbot
sudo apt install certbot python3-certbot-nginx

# Obtain certificate
sudo certbot --nginx -d your-domain.com -d www.your-domain.com

# Auto-renewal
sudo certbot renew --dry-run
```

## Performance Optimization

### CDN Configuration

Use CDN for static assets:

**Cloudflare** (Recommended):
1. Add domain to Cloudflare
2. Update nameservers
3. Enable:
   - Auto Minify (JS, CSS, HTML)
   - Brotli compression
   - HTTP/3
   - Caching

### Caching Strategy

```nginx
# Nginx caching example
location ~* \.(js|css)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}

location ~* \.(png|jpg|jpeg|gif|svg|webp)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}

location /index.html {
    add_header Cache-Control "no-cache, must-revalidate";
}
```

### Compression

Enable Brotli and Gzip:

```nginx
# Gzip
gzip on;
gzip_vary on;
gzip_comp_level 6;
gzip_types text/plain text/css text/xml text/javascript application/json application/javascript application/xml+rss;

# Brotli (if module installed)
brotli on;
brotli_comp_level 6;
brotli_types text/plain text/css text/xml text/javascript application/json application/javascript;
```

## Monitoring & Analytics

### Error Tracking

**Sentry Integration**:

```typescript
import * as Sentry from '@sentry/react';

Sentry.init({
  dsn: 'YOUR_SENTRY_DSN',
  environment: import.meta.env.MODE,
  tracesSampleRate: 1.0,
});
```

### Web Vitals

```typescript
import { getCLS, getFID, getFCP, getLCP, getTTFB } from 'web-vitals';

function sendToAnalytics(metric) {
  console.log(metric);
  // Send to your analytics service
}

getCLS(sendToAnalytics);
getFID(sendToAnalytics);
getFCP(sendToAnalytics);
getLCP(sendToAnalytics);
getTTFB(sendToAnalytics);
```

### Uptime Monitoring

Use services like:
- UptimeRobot (free tier available)
- Pingdom
- StatusCake

## Continuous Deployment

### GitHub Actions

Create `.github/workflows/deploy.yml`:

```yaml
name: Deploy to Vercel

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'

      - name: Install dependencies
        run: |
          cd web
          npm ci

      - name: Run tests
        run: |
          cd web
          npm run test

      - name: Build
        run: |
          cd web
          npm run build

      - name: Deploy to Vercel
        uses: amondnet/vercel-action@v20
        with:
          vercel-token: ${{ secrets.VERCEL_TOKEN }}
          vercel-org-id: ${{ secrets.ORG_ID }}
          vercel-project-id: ${{ secrets.PROJECT_ID }}
          working-directory: ./web
```

## Troubleshooting

### Common Issues

**1. Build fails with memory error**:
```bash
# Increase Node memory
NODE_OPTIONS=--max_old_space_size=4096 npm run build
```

**2. Routes not working (404)**:
- Ensure SPA rewrites are configured
- Check server configuration for `try_files`

**3. Service Worker not updating**:
- Check Cache-Control headers
- Clear browser cache
- Update service worker versioning

**4. PWA not installable**:
- Verify HTTPS is enabled
- Check manifest.json is served correctly
- Ensure service worker is registered

## Checklist

Before deploying to production:

- [ ] Run `npm run type-check`
- [ ] Run `npm run lint`
- [ ] Run `npm run test`
- [ ] Build succeeds (`npm run build`)
- [ ] Test production build locally (`npm run preview`)
- [ ] Configure environment variables
- [ ] Set up SSL/TLS certificate
- [ ] Configure CDN (optional)
- [ ] Enable compression
- [ ] Set up monitoring
- [ ] Test on mobile devices
- [ ] Verify PWA installation works
- [ ] Test offline functionality

## Support

For deployment issues:
1. Check deployment platform docs
2. Review server logs
3. Test locally with production build
4. Create issue on GitHub

---

Happy deploying! 🚀
