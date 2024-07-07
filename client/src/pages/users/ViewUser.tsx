/* eslint-disable @typescript-eslint/no-explicit-any */
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  Box,
  Divider,
  Flex,
  FormControl,
  FormLabel,
  Heading,
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

  useEffect(() => {
    const fetchUserDetails = async () => {
      try {
        const userResponse = await api.get(`users/${userid}`);
        setUserDetails(userResponse.data);
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

    fetchUserDetails();
  }, [userid, toast, navigate]);

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
            {userDetails.name}
          </Heading>
          <Divider />
          <FormControl isReadOnly>
            <FormLabel htmlFor="username">Username</FormLabel>
            <Input
              id="username"
              type="text"
              value={userDetails.username}
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
            <FormLabel htmlFor="phoneNumber">Phone Number</FormLabel>
            <Input
              id="phoneNumber"
              type="text"
              value={userDetails.phoneNumber}
              isReadOnly
              isDisabled
            />
          </FormControl>
          <FormControl isReadOnly>
            <FormLabel htmlFor="dateOfBirth">Date of Birth</FormLabel>
            <Input
              id="dateOfBirth"
              type="text"
              value={userDetails.dateOfBirth}
              isReadOnly
              isDisabled
            />
          </FormControl>
          <FormControl isReadOnly>
            <FormLabel htmlFor="roles">Roles</FormLabel>
            <Input
              id="roles"
              type="text"
              value={userDetails.roles.map((role: any) => role.name).join(", ")}
              isReadOnly
              isDisabled
            />
          </FormControl>
        </VStack>
      </Box>
    </Flex>
  );
};

export default ViewUserPage;
