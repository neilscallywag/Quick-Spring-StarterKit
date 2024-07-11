/* eslint-disable @typescript-eslint/no-explicit-any */
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  FormControl,
  FormLabel,
  Input,
  useToast,
  VStack,
} from "@chakra-ui/react";

import { api } from "../../../modules/api";
import UserCardContainer from "../../../modules/shared/components/card/card";
import Loader from "../../../modules/shared/components/loader/Loader";
import Container from "../../shared/components/container/container";

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
    return <Loader />;
  }

  return (
    <Container
      pageTitle="User Details"
      metaDescription="Detailed view of a user"
      headingText={userDetails.name}
    >
      <UserCardContainer>
        <VStack spacing={4} align="flex-start">
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
      </UserCardContainer>
    </Container>
  );
};

export default ViewUserPage;
