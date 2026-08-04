import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';

// Entry point for the FactoryOps360 UI.
// Providers (theme, router, query client, etc.) should be wired up here
// as the application grows; intentionally left minimal in this starter kit.
ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
);
