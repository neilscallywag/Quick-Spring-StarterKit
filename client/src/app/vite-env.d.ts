// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string;
  readonly VITE_STORAGE_KEY: string;
  readonly VITE_ENV_MODE: string;
  readonly VITE_AUTH_TOKEN_KEY: string;
  readonly VITE_API_TIMEOUT: string;
  readonly VITE_ENABLE_LOGGING: string;
  readonly VITE_FEATURE_X_ENABLED: string;
  readonly VITE_INVALID_TOKEN_EVENT: string;
  readonly VITE_STORAGE_NAME: ßstring;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
