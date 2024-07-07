// auth.tsx
import React, { createContext, useContext, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useToast } from "@chakra-ui/react";

import useAuthStore from "../../store/AuthStore";
import { LocalLoginRequestDTO, UserInfoResponse } from "../../types/auth/user";
import {
  getUserData as apiGetUserData,
  login as apiLogin,
  logout as apiLogout,
} from "../api";

interface AuthContextType {
  isAuthenticated: boolean;
  user: UserInfoResponse | null;
  login: (data: LocalLoginRequestDTO) => Promise<void>;
  logout: () => Promise<void>;
  getUserData: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const auth = useProvideAuth();
  return <AuthContext.Provider value={auth}>{children}</AuthContext.Provider>;
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};

const useProvideAuth = (): AuthContextType => {
  const {
    isAuthenticated,
    user,
    login: storeLogin,
    logout: storeLogout,
  } = useAuthStore();
  const toast = useToast();
  const navigate = useNavigate();

  useEffect(() => {
    const handleInvalidToken = () => {
      signOut();
    };

    window.addEventListener("invalid-token-detected", handleInvalidToken);
    return () => {
      window.removeEventListener("invalid-token-detected", handleInvalidToken);
    };
  }, [isAuthenticated]);

  const login = async (data: LocalLoginRequestDTO): Promise<void> => {
    try {
      await apiLogin(data);
      await getUserData();
    } catch (error) {
      toast({
        title: "Login Failed",
        description: "Invalid username or password.",
        status: "error",
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const getUserData = async (): Promise<void> => {
    try {
      const userData: UserInfoResponse = await apiGetUserData();
      storeLogin(userData);
      navigate(`/users/view/${userData.username}`);
    } catch (error) {
      console.error(error);
      toast({
        title: "Error",
        description: "Failed to fetch user data.",
        status: "error",
        duration: 5000,
        isClosable: true,
      });
      navigate("/login");
    }
  };

  const signOut = async (): Promise<void> => {
    try {
      await apiLogout();
    } catch (error) {
      console.error("Logout failed:", error);
    }
    storeLogout();
    navigate("/");
    toast({
      title: "Logged Out",
      description: "You have been logged out.",
      status: "info",
      duration: 5000,
      isClosable: true,
    });
  };

  return {
    isAuthenticated,
    user,
    login,
    logout: signOut,
    getUserData,
  };
};

export default AuthProvider;
