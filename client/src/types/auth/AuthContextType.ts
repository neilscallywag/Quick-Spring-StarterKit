import { UserData } from "~types/data";

export type AuthContextType = {
  isAuthenticated: boolean;
  user: UserData | null;
  signOut: () => void;
  getUserData: () => void;
};
