import React, { ReactNode } from "react";
import { Box } from "@chakra-ui/react";

interface UserCardContainerProps {
  children: ReactNode;
}

const UserCardContainer: React.FC<UserCardContainerProps> = ({ children }) => {
  return (
    <Box
      bg="white"
      w="100%"
      maxW="1200px"
      mx="auto"
      p={4}
      borderRadius="0.22rem"
      border={"0.0625rem solid #dee2e6"}
      display={"block"}
      boxSizing="border-box"
      outline={0}
      transition={"all 100ms ease-in-out"}
      boxShadow="0 0.0625rem 0.1875rem rgba(0, 0, 0, 0.05),0 0.0625rem 0.125rem rgba(0, 0, 0, 0.1)"
    >
      {children}
    </Box>
  );
};

export default UserCardContainer;
