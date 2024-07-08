import { useState } from "react";
import { Helmet } from "react-helmet-async";
import { CgAddR } from "react-icons/cg";
import {
  Box,
  Button,
  Flex,
  Heading,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalFooter,
  ModalHeader,
  ModalOverlay,
  Text,
  useDisclosure,
  useToast,
} from "@chakra-ui/react";
import axios from "axios";
import {
  MRT_ColumnFiltersState,
  MRT_PaginationState,
  MRT_Row,
  MRT_SortingState,
} from "mantine-react-table";

import PrimaryButton from "../../../modules/shared/components/buttons/PrimaryButton";
import { UserTableType } from "../../../types/auth/user";
import { api } from "../../api";
import UserTable from "../components/table/UserTable";
import useGetUsers from "../hooks/useGetUsers";

const ViewUser = () => {
  const toast = useToast();
  const { isOpen, onOpen, onClose } = useDisclosure();
  const [activeDeleteId, setActiveDeleteId] = useState<string | null>(null);

  const handleDelete = (row: MRT_Row<UserTableType>) => {
    setActiveDeleteId(row.original.id);
    onOpen();
  };

  const deleteUser = async (id: string) => {
    try {
      const response = await api.delete(`/users/${id}`);
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error(
          error.response?.data?.message || "Failed to delete user",
        );
      }
    }
  };

  const onDelete = async () => {
    if (activeDeleteId) {
      try {
        await deleteUser(activeDeleteId);
        toast({
          title: `User: ${activeDeleteId} deleted successfully.`,
          status: "success",
        });
        refetch();
      } catch (error) {
        let errorMessage =
          "An error occurred while trying to delete the user. Please try again.";
        if (axios.isAxiosError(error)) {
          if (error.response?.status === 400) {
            errorMessage = "You cannot delete your own account.";
          } else if (error.response?.status === 500) {
            errorMessage = "A server error occurred while deleting the user.";
          } else {
            errorMessage = error.response?.data?.message || errorMessage;
          }
        }
        toast({
          title: "Failed to delete user",
          description: errorMessage,
          status: "error",
        });
      } finally {
        onClose();
        setActiveDeleteId(null);
      }
    }
  };

  const [columnFilters, setColumnFilters] = useState<MRT_ColumnFiltersState>(
    [],
  );
  const [sorting, setSorting] = useState<MRT_SortingState>([]);
  const [pagination, setPagination] = useState<MRT_PaginationState>({
    pageIndex: 0,
    pageSize: 10,
  });

  const { data, isError, isFetching, isLoading, refetch } = useGetUsers({
    columnFilters,
    pagination,
    sorting,
  });

  const fetchedUsers = data?.data ?? [];
  const totalRowCount = data?.meta?.totalRowCount ?? 0;

  return (
    <Box mt={0} p={1}>
      <Helmet>
        <title>User Table</title>
        <meta name="description" content="User Table" />
      </Helmet>
      <Flex justifyContent="space-between" m={4} alignItems="center">
        <Heading as="h3" size="lg">
          Users
        </Heading>
        <PrimaryButton href="/users/create" leftIcon={<CgAddR />}>
          Enroll new user
        </PrimaryButton>
      </Flex>
      <UserTable
        data={fetchedUsers}
        isError={isError}
        isLoading={isLoading}
        isFetching={isFetching}
        refetch={refetch}
        columnFilters={columnFilters}
        sorting={sorting}
        pagination={pagination}
        totalRowCount={totalRowCount}
        setColumnFilters={setColumnFilters}
        setSorting={setSorting}
        setPagination={setPagination}
        handleDelete={handleDelete}
      />
      <Modal blockScrollOnMount={false} isOpen={isOpen} onClose={onClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Confirmation</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <Text fontWeight="bold" mb="1rem">
              Are you sure you want to delete User: {activeDeleteId}?
            </Text>
          </ModalBody>
          <ModalFooter>
            <Button colorScheme="blue" mr={3} onClick={onClose}>
              Close
            </Button>
            <Button variant="outline" colorScheme="red" onClick={onDelete}>
              Delete
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </Box>
  );
};

export default ViewUser;
