// src/hooks/useGetUsers.ts
/* eslint-disable max-lines */
import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import axios from "axios";

import { Params, UserApiResponse } from "~shared/types/auth/user";

import { api } from "~features/api";

const useGetUsers = ({ columnFilters, sorting, pagination }: Params) => {
  console.log(sorting);
  const [lastQueryData, setLastQueryData] = useState<
    UserApiResponse | undefined
  >();

  const requestData = {
    page: pagination.pageIndex,
    size: pagination.pageSize,
    nameFilter:
      columnFilters.find((filter) => filter.id === "name")?.value || "",
    emailFilter:
      columnFilters.find((filter) => filter.id === "email")?.value || "",
  };

  return useQuery<UserApiResponse>({
    queryKey: ["userId", requestData],
    queryFn: async ({ signal }): Promise<UserApiResponse> => {
      const source = axios.CancelToken.source();
      signal.addEventListener("abort", () => {
        source.cancel();
      });
      try {
        const { data: response } = await api.get("users", {
          params: requestData,
          cancelToken: source.token,
        });
        setLastQueryData(response);
        return {
          data: response.map((user: any) => ({
            id: user.id,
            name: user.name,
            email: user.email,
            createdAt: user.createdAt,
            username: user.username,
            phoneNumber: user.phoneNumber,
            dateOfBirth: user.dateOfBirth,
            roles: user.roles,
            provider: user.provider,
            imageUrl: user.imageUrl,
            emailVerified: user.emailVerified,
            authProvider: user.authProvider,
          })),
          meta: { totalRowCount: response.length },
        };
      } catch (error) {
        console.error(error);
        throw new Error("Error fetching data");
      }
    },
    placeholderData: lastQueryData,
    staleTime: 30_000,
  });
};

export default useGetUsers;
