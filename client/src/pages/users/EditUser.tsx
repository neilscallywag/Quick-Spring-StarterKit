/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable id-length */
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  Box,
  Button,
  Flex,
  FormControl,
  FormLabel,
  Input,
  Select,
  useToast,
} from "@chakra-ui/react";
import axios from "axios";

import { api } from "~features/api";
import { useAuth } from "~features/auth";

const EditUserPage = () => {
  const [userDetails, setUserDetails] = useState({
    firstName: "",
    lastName: "",
    email: "",
    role: "",
  });
  const { userid } = useParams();
  const toast = useToast();
  const navigate = useNavigate();
  const [roles, setRoles] = useState<string[]>([]);
  const { user } = useAuth();

  useEffect(() => {
    // Fetch roles and user details
    const fetchData = async () => {
      try {
        const rolesResponse = await api.get("/api/v1/roles");
        setRoles(rolesResponse.data);
        const userDetailsResponse = await api.get(`/api/v1/users/${userid}`);
        setUserDetails(userDetailsResponse.data);
      } catch (error) {
        toast({
          title: "Error fetching data.",
          description: "Could not fetch user details or roles from the server.",
          status: "error",
          duration: 9000,
          isClosable: true,
        });
      }
    };

    fetchData();
  }, [userid, toast]);

  const handleChange = (e: { target: { name: any; value: any } }) => {
    const { name, value } = e.target;
    setUserDetails((prevDetails) => ({
      ...prevDetails,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: { preventDefault: () => void }) => {
    e.preventDefault();
    try {
      await api.post(`/api/v1/users/${userid}/edit`, userDetails);
      toast({
        title: "User updated.",
        description: "User details updated successfully.",
        status: "success",
        duration: 9000,
        isClosable: true,
      });
      navigate(`/users/view/${userid}`);
    } catch (error) {
      if (axios.isAxiosError(error)) {
        if (
          error.response &&
          error.response.status === 400 &&
          error.response.data.error === "You cannot edit your own role"
        ) {
          toast({
            title: "Error updating user.",
            description: "You cannot edit your own role.",
            status: "error",
            duration: 9000,
            isClosable: true,
          });
        } else {
          toast({
            title: "Error updating user.",
            description:
              error.message || "There was an error updating the user.",
            status: "error",
            duration: 9000,
            isClosable: true,
          });
        }
      }
    }
  };
  return (
    <Flex width="full" align="center" justifyContent="center">
      <Box
        p={8}
        maxWidth="500px"
        borderWidth={1}
        borderRadius={8}
        boxShadow="lg"
      >
        <Box textAlign="center" my={4}>
          <form onSubmit={handleSubmit}>
            <FormControl isRequired>
              <FormLabel>First Name</FormLabel>
              <Input
                type="text"
                placeholder="First Name"
                name="firstName"
                value={userDetails.firstName}
                onChange={handleChange}
              />
            </FormControl>
            <FormControl isRequired mt={6}>
              <FormLabel>Last Name</FormLabel>
              <Input
                type="text"
                placeholder="Last Name"
                name="lastName"
                value={userDetails.lastName}
                onChange={handleChange}
              />
            </FormControl>
            <FormControl isRequired mt={6}>
              <FormLabel>Email</FormLabel>
              <Input
                type="email"
                placeholder="Email"
                name="email"
                value={userDetails.email}
                onChange={handleChange}
              />
            </FormControl>
            <FormControl isRequired mt={6}>
              <FormLabel>Role</FormLabel>
              <Select
                placeholder="Select role"
                name="role"
                value={userDetails.role}
                onChange={handleChange}
                // Disable if the user being edited is the current user
                isDisabled={user?.user_id === userid}
              >
                {roles.map((role) => (
                  <option key={role} value={role}>
                    {role}
                  </option>
                ))}
              </Select>
            </FormControl>
            <Button colorScheme="blue" width="full" mt={4} type="submit">
              Save Changes
            </Button>
          </form>
        </Box>
      </Box>
    </Flex>
  );
};

export default EditUserPage;
