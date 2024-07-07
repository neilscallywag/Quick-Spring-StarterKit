import { UserInfoResponse } from "../auth/user";

export type AuthStateType = {
  isAuthenticated: boolean;
  user: UserInfoResponse | null;
  login: (userData: UserInfoResponse) => void;
  logout: () => void;
};
