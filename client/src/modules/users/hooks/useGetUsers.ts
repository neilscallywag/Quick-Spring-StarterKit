import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import axios from "axios";

import { api } from "../../../modules/api";
import { Params, UserApiResponse } from "../../../types/auth/user";

const useGetUsers = ({ columnFilters, sorting, pagination }: Params) => {
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
    sortField: sorting[0]?.id || "id",
    sortOrder: sorting[0]?.desc ? "desc" : "asc",
  };

  return useQuery<UserApiResponse>({
    queryKey: ["users", requestData],
    queryFn: async ({ signal }) => {
      const source = axios.CancelToken.source();
      signal.addEventListener("abort", () => {
        source.cancel();
      });
      try {
        const { data: response, headers } = await api.get("users", {
          params: requestData,
          cancelToken: source.token,
        });
        console.log(headers);
        const totalRowCount = headers["x-total-count"]
          ? parseInt(headers["x-total-count"], 10)
          : response.length;
        setLastQueryData({
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
          meta: { totalRowCount },
        });
        console.log(totalRowCount);
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
          meta: { totalRowCount },
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
