/* eslint-disable @typescript-eslint/no-explicit-any */
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  Box,
  Button,
  chakra,
  Divider,
  Flex,
  FormControl,
  FormLabel,
  Heading,
  HStack,
  Input,
  useToast,
  VStack,
} from "@chakra-ui/react";

import Loader from "~components/loader/Loader";

import { api } from "~features/api";

const ViewUserPage = () => {
  const { userid } = useParams();
  const navigate = useNavigate();
  const toast = useToast();
  const [userDetails, setUserDetails] = useState<any>(null);
  const [pointsAccounts, setPointsAccounts] = useState<any[]>([]);

  useEffect(() => {
    const fetchUserAndPoints = async () => {
      try {
        const userResponse = await api.get(`/api/v1/users/${userid}`);
        setUserDetails(userResponse.data);
        const pointsResponse = await api.get(`/api/v1/users/${userid}/points`);
        setPointsAccounts(pointsResponse.data.data.pointAccount || []);
      } catch (error: any) {
        // Handling the case where the user ID is not found
        if (error.response && error.response.status === 404) {
          toast({
            title: "User not found",
            description: "The requested user was not found.",
            status: "error",
            duration: 9000,
            isClosable: true,
          });
          // Redirecting to the /users/viewall page
          navigate("/users/viewall");
          return;
        }
        // Handle other errors
        toast({
          title: "Error fetching data",
          description: error.message || "Could not fetch data.",
          status: "error",
          duration: 9000,
          isClosable: true,
        });
      }
    };

    fetchUserAndPoints();
  }, [userid, toast, navigate]); // Add navigate to the dependency array

  const handleEditPoints = (pointsid: string) => {
    navigate(`/users/view/${userid}/points/${pointsid}/edit`);
  };

  if (!userDetails) {
    return <Loader></Loader>;
  }

  return (
    <Flex width="full" align="center" justifyContent="center" mt={10}>
      <Box
        p={8}
        maxWidth="700px"
        w="full"
        borderWidth={1}
        borderRadius={8}
        boxShadow="lg"
      >
        <VStack spacing={4} align="flex-start">
          <Heading as="h1" size="xl">
            {userDetails.firstName} {userDetails.lastName}
          </Heading>
          <Divider />
          <FormControl isReadOnly>
            <FormLabel htmlFor="first-name">First Name</FormLabel>
            <Input
              id="first-name"
              type="text"
              value={userDetails.firstName}
              isReadOnly
              isDisabled
            />
          </FormControl>
          <FormControl isReadOnly>
            <FormLabel htmlFor="last-name">Last Name</FormLabel>
            <Input
              id="last-name"
              type="text"
              value={userDetails.lastName}
              isReadOnly
              isDisabled
            />
          </FormControl>
          <FormControl isReadOnly>
            <FormLabel htmlFor="email">Email</FormLabel>
            <Input
              id="email"
              type="email"
              value={userDetails.email}
              isReadOnly
              isDisabled
            />
          </FormControl>
          <FormControl isReadOnly>
            <FormLabel htmlFor="role">Role</FormLabel>
            <Input
              id="role"
              type="text"
              value={userDetails.role}
              isReadOnly
              isDisabled
            />
          </FormControl>
          <Heading as="h2" size="lg">
            Points Accounts
          </Heading>
          <Divider />
          {pointsAccounts.length === 0 ? (
            <chakra.span>No points accounts available</chakra.span>
          ) : (
            pointsAccounts.map((account, index) => (
              <Box key={index} w="full">
                <HStack justify="space-between" align="center" w="full">
                  <VStack align="flex-start">
                    <FormControl isReadOnly>
                      <FormLabel htmlFor={`account-bank-${index}`}>
                        {account.bank} - {account.pointsId}
                      </FormLabel>
                      <Input
                        id={`account-bank-${index}`}
                        type="text"
                        value={account.points}
                        isReadOnly
                        isDisabled
                      />
                    </FormControl>
                  </VStack>
                  <Button
                    size="sm"
                    bg="branding.100"
                    onClick={() => handleEditPoints(account.pointsId)}
                    _hover={{ bg: "branding.200" }}
                  >
                    Edit
                  </Button>
                </HStack>
              </Box>
            ))
          )}
        </VStack>
      </Box>
    </Flex>
  );
};

export default ViewUserPage;
