import "inter-ui/inter.css";

import * as React from "react";
import * as ReactDOM from "react-dom/client";
import { HelmetProvider } from "react-helmet-async";
import { BrowserRouter } from "react-router-dom";
import { ThemeProvider } from "@opengovsg/design-system-react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

import { AuthProvider } from "~features/auth";

import App from "./App";
import customTheme from "./theme";

const helmetContext = {};

const tableClient = new QueryClient();

const rootElement = document.getElementById("root");
if (rootElement) {
  ReactDOM.createRoot(rootElement).render(
    <React.StrictMode>
      <QueryClientProvider client={tableClient}>
        <ThemeProvider theme={customTheme}>
          <BrowserRouter>
            <AuthProvider>
              <HelmetProvider context={helmetContext}>
                <App />
              </HelmetProvider>
            </AuthProvider>
          </BrowserRouter>
        </ThemeProvider>
      </QueryClientProvider>
    </React.StrictMode>,
  );
}
