import { UserData } from "../data";

export type AuthContextType = {
  isAuthenticated: boolean;
  user: UserData | null;
  signOut: () => void;
  getUserData: () => void;
};
