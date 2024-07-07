import { FiChevronDown, FiMenu } from "react-icons/fi";
import {
  Avatar,
  Box,
  Flex,
  HStack,
  IconButton,
  Menu,
  MenuButton,
  MenuItem,
  MenuList,
  Text,
  useColorModeValue,
  VStack,
} from "@chakra-ui/react";

import { useAuth } from "../../../..//modules/auth";

import { MobileNavProps } from "./interface";

export const MobileNav = ({ onOpen, ...rest }: MobileNavProps) => {
  const { logout, user } = useAuth();

  return (
    <Flex
      ml={{ base: 0, md: 60 }}
      px={{ base: 4, md: 4 }}
      height="20"
      alignItems="center"
      bg={useColorModeValue("white", "gray.900")}
      borderBottomWidth="1px"
      borderBottomColor={useColorModeValue("gray.200", "gray.700")}
      justifyContent={{ base: "space-between", md: "flex-end" }}
      {...rest}
    >
      <IconButton
        display={{ base: "flex", md: "none" }}
        onClick={onOpen}
        variant="outline"
        colorScheme="branding.100"
        aria-label="open menu"
        icon={<FiMenu />}
      />
      <Text
        display={{ base: "flex", md: "none" }}
        fontSize="4xl"
        fontFamily="monospace"
        fontWeight="bold"
        color="branding.100"
      >
        CSD Project
      </Text>
      <HStack spacing={{ base: "4", md: "6" }}>
        <Flex alignItems={"center"}>
          <Menu>
            <MenuButton
              py={2}
              transition="all 0.3s"
              _focus={{ boxShadow: "none" }}
            >
              <HStack>
                <Avatar size={"sm"} src={user?.username}>
                  {user?.username}
                </Avatar>
                <VStack
                  display={{ base: "none", md: "flex" }}
                  alignItems="flex-start"
                  spacing="1px"
                  ml="2"
                >
                  <Text fontSize="md" color={"branding.100"}>
                    {user?.username}
                  </Text>
                  <Text fontSize="xs" color="gray.600">
                    {user?.roles[0]}
                  </Text>
                </VStack>
                <Box display={{ base: "none", md: "flex" }}>
                  <FiChevronDown />
                </Box>
              </HStack>
            </MenuButton>
            <MenuList
              bg={useColorModeValue("white", "gray.900")}
              borderColor={useColorModeValue("gray.200", "gray.700")}
              width={{ base: "full", md: "xs" }}
              zIndex={9999}
            >
              <MenuItem color={"branding.100"} onClick={logout}>
                Sign out
              </MenuItem>
            </MenuList>
          </Menu>
        </Flex>
      </HStack>
    </Flex>
  );
};
