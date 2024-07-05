/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable id-length */
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
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
import { AxiosResponse } from "axios";

import AccessControlTable from "~components/acl/ACL";

import { api } from "~features/api";

interface UserDetails {
  firstName: string;
  lastName: string;
  email: string;
  role: string;
}

interface RolePermissions {
  actions: string[];
  constraints?: string[];
}

interface ModulePermissions {
  [module: string]: RolePermissions;
}

interface Roles {
  [role: string]: ModulePermissions;
}

const CreateUserPage = () => {
  const navigate = useNavigate();
  const toast = useToast();
  const [userDetails, setUserDetails] = useState<UserDetails>({
    firstName: "",
    lastName: "",
    email: "",
    role: "",
  });

  const [roles, setRoles] = useState<string[]>([]);
  const [policy, setPolicy] = useState<Roles | null>(null);

  useEffect(() => {
    const fetchRolesAndPolicy = async () => {
      try {
        // Fetch roles
        const rolesResponse: AxiosResponse<string[]> =
          await api.get("/api/v1/roles");
        setRoles(rolesResponse.data);

        // Fetch policy
        const policyResponse: AxiosResponse<Roles> =
          await api.get("/api/v1/policy");
        if (policyResponse.status === 200) {
          setPolicy(policyResponse.data);
        } else {
          throw new Error("Failed to fetch policy document");
        }
      } catch (error) {
        toast({
          title: "Error fetching data.",
          description: "Could not fetch roles or policy from the server.",
          status: "error",
          duration: 9000,
          isClosable: true,
        });
      }
    };

    fetchRolesAndPolicy();
  }, []);

  const handleChange = (
    e:
      | React.ChangeEvent<HTMLInputElement>
      | React.ChangeEvent<HTMLSelectElement>,
  ) => {
    const { name, value } = e.target;
    setUserDetails((prevDetails) => ({ ...prevDetails, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      // POST request to create a new user
      const response: AxiosResponse = await api.post(
        "/api/v1/users/createNewUser",
        userDetails,
      );
      toast({
        title: "User created.",
        description: `User ${response.data.name} created successfully.`,
        status: "success",
        duration: 9000,
        isClosable: true,
      });
      navigate("/users/viewall");
    } catch (error: any) {
      // Error handling
      let message = "Failed to create new user.";
      // Handle error response from the server
      if (error.response) {
        if (error.response.status === 401) {
          message = "Unauthorized: You cannot perform this action.";
        } else {
          message = error.response.data.message || error.response.statusText;
        }
      } else if (error.request) {
        message = "No response was received from the server.";
      } else {
        message = error.message;
      }
      toast({
        title: "Error creating user.",
        description: message,
        status: "error",
        duration: 9000,
        isClosable: true,
      });
    }
  };

  return (
    <>
      <Flex width="full" align="center" justifyContent="center">
        <Box
          p={8}
          maxWidth="500px"
          borderWidth={1}
          borderRadius={8}
          boxShadow="lg"
        >
          <Box textAlign="center">
            <Box my={4} textAlign="left">
              <form onSubmit={handleSubmit}>
                <FormControl isRequired>
                  <FormLabel>First Name:</FormLabel>
                  <Input
                    type="text"
                    placeholder="John"
                    name="firstName"
                    onChange={handleChange}
                  />
                </FormControl>
                <FormControl isRequired mt={6}>
                  <FormLabel>Last Name:</FormLabel>
                  <Input
                    type="text"
                    placeholder="Doe"
                    name="lastName"
                    onChange={handleChange}
                  />
                </FormControl>
                <FormControl isRequired mt={6}>
                  <FormLabel>Email:</FormLabel>
                  <Input
                    type="email"
                    placeholder="johndoe@example.com"
                    name="email"
                    onChange={handleChange}
                  />
                </FormControl>
                <FormControl isRequired mt={6}>
                  <FormLabel>Role:</FormLabel>
                  <Select
                    placeholder="Select role"
                    name="role"
                    onChange={handleChange}
                  >
                    {/* Dynamically generate options from the fetched roles */}
                    {roles.map((role) => (
                      <option key={role} value={role}>
                        {role}
                      </option>
                    ))}
                  </Select>
                </FormControl>
                <Button bg="branding.100" width="full" mt={4} type="submit">
                  Save
                </Button>
              </form>
            </Box>
          </Box>
        </Box>
        {/* AccessControlTable is only rendered when policy data is available */}
      </Flex>
      <Flex mt={3} width="full" align="center" justifyContent="center">
        {policy && <AccessControlTable roles={policy} />}
      </Flex>
    </>
  );
};

export default CreateUserPage;
