import { useState } from "react";
import { Helmet } from "react-helmet-async";
import {
  Box,
  Center,
  Flex,
  FormControl,
  FormLabel,
  Heading,
  Input,
  Stack,
  Text,
} from "@chakra-ui/react";
import { LocalLoginRequestDTO } from "src/types/auth/user";

import PrimaryButton from "../../../modules/shared/components/buttons/PrimaryButton";
import { useAuth } from "../../auth/AuthContext";

const LoginPage = () => {
  const { login } = useAuth();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const handleLogin = async (event: React.FormEvent) => {
    event.preventDefault();
    setIsLoading(true);
    try {
      const data: LocalLoginRequestDTO = { username, password };
      await login(data);
    } catch (error) {
      console.error("Login failed:", error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Flex minH="100vh" align="center" justify="center" bg={"branding.100"}>
      <Helmet>
        <title>Sign in</title>
        <meta name="description" content="Sign in" />
      </Helmet>
      <Stack
        spacing={8}
        mx="auto"
        maxW="lg"
        w={{ base: "90%", sm: "full" }}
        p={1}
      >
        <Stack align="center" mt="5" mb="10">
          <Heading color={"white"}>CSD Project</Heading>
        </Stack>
        <Box rounded="lg" bg={"white"} boxShadow="lg" p={6}>
          <Stack align="center" mt="5" mb="10">
            <Heading fontSize="3xl" color={"black"}>
              Sign in
            </Heading>
          </Stack>
          <form onSubmit={handleLogin}>
            <Stack spacing={4} alignItems="center" mb="6">
              <FormControl id="username" isRequired>
                <FormLabel>Username</FormLabel>
                <Input
                  type="text"
                  value={username}
                  onChange={(event) => setUsername(event.target.value)}
                />
              </FormControl>
              <FormControl id="password" isRequired>
                <FormLabel>Password</FormLabel>
                <Input
                  type="password"
                  value={password}
                  onChange={(event) => setPassword(event.target.value)}
                />
              </FormControl>
              <PrimaryButton
                type="submit"
                w={{ base: "full", sm: "100%" }}
                size="lg"
                isLoading={isLoading}
              >
                <Center>
                  <Text>Sign in</Text>
                </Center>
              </PrimaryButton>
            </Stack>
          </form>
        </Box>
      </Stack>
    </Flex>
  );
};

export default LoginPage;
