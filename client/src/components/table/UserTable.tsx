// src/components/UserTable.tsx
import { useMemo } from "react";
import { FaPencilAlt } from "react-icons/fa";
import { DeleteIcon, ViewIcon } from "@chakra-ui/icons";
import { Box, HStack, IconButton, Tooltip } from "@chakra-ui/react";
import { ActionIcon } from "@mantine/core";
import { IconRefresh } from "@tabler/icons-react";
import {
  MantineReactTable,
  MRT_ColumnDef,
  MRT_Row,
  useMantineReactTable,
} from "mantine-react-table";

import { UserTableType } from "~shared/types/auth/user";

interface UserTableProps {
  data: UserTableType[];
  isError: boolean;
  isLoading: boolean;
  isFetching: boolean;
  refetch: () => void;
  columnFilters: any;
  sorting: any;
  pagination: any;
  totalRowCount: number;
  setColumnFilters: (filters: any) => void;
  setSorting: (sorting: any) => void;
  setPagination: (pagination: any) => void;
  handleDelete: (row: MRT_Row<UserTableType>) => void;
}

const UserTable: React.FC<UserTableProps> = ({
  data,
  isError,
  isLoading,
  isFetching,
  refetch,
  columnFilters,
  sorting,
  pagination,
  totalRowCount,
  setColumnFilters,
  setSorting,
  setPagination,
  handleDelete,
}) => {
  const columns = useMemo<MRT_ColumnDef<UserTableType>[]>(
    () => [
      {
        id: "id",
        accessorKey: "id",
        header: "User ID",
      },
      {
        id: "name",
        accessorKey: "name",
        header: "Name",
        filterVariant: "autocomplete",
      },
      {
        id: "email",
        accessorKey: "email",
        header: "Email",
      },
      {
        id: "createdAt",
        accessorKey: "createdAt",
        header: "Enrollment Date",
        filterVariant: "date-range",
        Cell: ({ cell }) => (
          <Box textAlign="center">
            {new Date(cell.getValue<string>()).toLocaleDateString()}
          </Box>
        ),
      },
      {
        id: "actions",
        header: "Actions",
        Cell: ({ row }) => (
          <HStack>
            <Tooltip hasArrow label="Edit User" bg="gray.300" color="black">
              <IconButton
                aria-label="EditUser"
                as="a"
                icon={<FaPencilAlt />}
                href={`/users/view/${row.original.id}/edit/`}
              />
            </Tooltip>
            <Tooltip hasArrow label="View User" bg="#1A1E43">
              <IconButton
                aria-label="ViewUser"
                as="a"
                icon={<ViewIcon />}
                href={`/users/view/${row.original.id}`}
              />
            </Tooltip>
            <Tooltip hasArrow label="Delete User" bg="red.600">
              <IconButton
                aria-label="Delete"
                icon={<DeleteIcon />}
                onClick={() => handleDelete(row)}
                colorScheme="red"
              />
            </Tooltip>
          </HStack>
        ),
      },
    ],
    [handleDelete],
  );

  const table = useMantineReactTable({
    columns,
    data,
    manualFiltering: true,
    manualPagination: true,
    paginationDisplayMode: "pages",
    manualSorting: true,
    rowCount: totalRowCount,
    state: {
      columnFilters,
      isLoading,
      pagination,
      showAlertBanner: isError,
      showProgressBars: isFetching,
      sorting,
    },
    onColumnFiltersChange: setColumnFilters,
    onPaginationChange: setPagination,
    onSortingChange: setSorting,
    renderTopToolbarCustomActions: () => (
      <Tooltip label="Refresh Data" placement="top">
        <ActionIcon onClick={() => refetch()}>
          <IconRefresh />
        </ActionIcon>
      </Tooltip>
    ),
  });

  return <MantineReactTable table={table} />;
};

export default UserTable;
