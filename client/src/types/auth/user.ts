import {
  MRT_ColumnFiltersState,
  MRT_PaginationState,
  MRT_SortingState,
} from "mantine-react-table";

export interface LocalLoginRequestDTO {
  username: string;
  password: string;
}

export interface RoleDTO {
  id: string;
  name: string;
}

export interface UserResponseDTO {
  id: string;
  username: string;
  name: string;
  email: string;
  phoneNumber: string;
  dateOfBirth: Date;
  roles: RoleDTO[];
  provider: string;
  imageUrl: string;
  emailVerified: boolean;
  authProvider: string;
}

export interface UserInfoResponse {
  username: string;
  roles: string[];
  issuedAt: Date;
  expiresAt: Date;
}

export interface UserTableType {
  id: string;
  name: string;
  email: string;
  createdAt: string;
  username?: string;
  phoneNumber?: string;
  dateOfBirth?: string;
  roles?: string[];
  provider?: string;
  imageUrl?: string;
  emailVerified?: boolean;
  authProvider?: string;
}

export type UserApiResponse = {
  data: Array<UserTableType>;
  meta: {
    totalRowCount: number;
  };
};

export interface Params {
  columnFilters: MRT_ColumnFiltersState;
  sorting: MRT_SortingState;
  pagination: MRT_PaginationState;
}
