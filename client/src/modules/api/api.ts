import axios, { AxiosInstance, AxiosResponse } from "axios";
import { LocalLoginRequestDTO, UserInfoResponse } from "src/types/auth/user";

// Base URL for the API
const BASE_URL = import.meta.env.VITE_API_BASE_URL;
// Custom event name for invalid token
const INVALID_TOKEN_EVENT = import.meta.env.VITE_INVALID_TOKEN_EVENT;

// Create an Axios instance
export const api: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: parseInt(import.meta.env.VITE_API_TIMEOUT, 10),
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});

// Response interceptor to check for specific 401 error
api.interceptors.response.use(
  (response: AxiosResponse) => {
    return response;
  },
  (error) => {
    if (
      error.response?.status === 401 &&
      error.response?.data?.error === "Invalid Token"
    ) {
      // trigger the logout
      window.dispatchEvent(new CustomEvent(INVALID_TOKEN_EVENT));
    }
    // Always reject the error for downstream catch blocks to handle
    return Promise.reject(new Error(error.message));
  },
);

// Utility function to handle API responses
export async function handleResponse<T>(
  response: AxiosResponse<T>,
): Promise<T> {
  if (response.status >= 200 && response.status < 300) {
    return response.data;
  }
  throw new Error(`HTTP error! Status: ${response.status}`);
}

// Auth API Calls
export const login = async (data: LocalLoginRequestDTO): Promise<string> => {
  const response = await api.post<string>("users/login", data);
  return response.data;
};

export const getUserData = async (): Promise<UserInfoResponse> => {
  const response = await api.post<UserInfoResponse>("/users/me");
  return response.data;
};

export const logout = async (): Promise<string> => {
  const response = await api.post<string>("/users/logout");
  return response.data;
};
