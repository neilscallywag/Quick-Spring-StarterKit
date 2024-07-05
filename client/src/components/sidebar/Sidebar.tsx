"use client";
import { ReactNode, useEffect, useState } from "react";
import { IconType } from "react-icons";
import { FiChevronDown, FiCompass, FiMenu, FiUser } from "react-icons/fi";
import { useNavigate } from "react-router-dom";
import {
  Avatar,
  Box,
  BoxProps,
  CloseButton,
  Drawer,
  DrawerContent,
  Flex,
  FlexProps,
  HStack,
  Icon,
  IconButton,
  Menu,
  MenuButton,
  MenuItem,
  MenuList,
  Text,
  useColorModeValue,
  useDisclosure,
  VStack,
} from "@chakra-ui/react";

import { useAuth } from "~features/auth";

/*
 * interface LinkItemProps {
 *   name: string;
 *   icon: IconType;
 *   href: string;
 *   requireAdmin: boolean;
 * }
 */

interface NavItemProps extends FlexProps {
  icon: IconType;
  children: React.ReactNode;
}

interface MobileProps extends FlexProps {
  onOpen: () => void;
}

interface SidebarProps extends BoxProps {
  onClose: () => void;
}

interface LinkItem {
  name: string;
  icon: IconType;
  href: string;
  permissions?: {
    resource: string;
    action: string;
  };
}
const LinkItems = [
  {
    name: "View All Users",
    icon: FiUser,
    href: "/users/viewall",
    permissions: {
      resource: "UserStorage",
      action: "read",
    }, // Example: Requires permission to read User data
  },
  {
    name: "View Logs",
    icon: FiCompass,
    href: "/logs/viewall",
    permissions: {
      resource: "Logs",
      action: "read",
    }, // Requires permission to read Log data
  },
];

const SidebarContent = ({ onClose, ...rest }: SidebarProps) => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [availableLinks, setAvailableLinks] = useState<LinkItem[]>([]);

  useEffect(() => {
    const checkPermissions = async () => {
      const filteredLinks = [];
      for (const link of LinkItems) {
        if (link.permissions) {
          // If the link has specific permissions defined, check them
          const hasAccess = true;
          if (hasAccess) {
            filteredLinks.push(link);
          }
        } else {
          // If no specific permissions are required, add the link
          filteredLinks.push(link);
        }
      }
      setAvailableLinks(filteredLinks);
    };

    if (user) {
      checkPermissions();
    }
  }, [user]);
  return (
    <Box
      transition="3s ease"
      bg={useColorModeValue("white", "gray.900")}
      borderRight="1px"
      borderRightColor={useColorModeValue("gray.200", "gray.700")}
      w={{ base: "full", md: 60 }}
      pos="fixed"
      h="full"
      {...rest}
    >
      <Flex h="20" alignItems="center" mx="8" justifyContent="space-between">
        <Text
          fontSize="4xl"
          fontFamily="monospace"
          fontWeight="bold"
          color={"branding.100"}
        >
          CSD Project
        </Text>
        <CloseButton display={{ base: "flex", md: "none" }} onClick={onClose} />
      </Flex>
      {availableLinks.map((link) => (
        <NavItem
          key={link.name}
          icon={link.icon}
          onClick={() => navigate(link.href)}
        >
          {link.name}
        </NavItem>
      ))}
    </Box>
  );
};

const NavItem = ({ icon, children, ...rest }: NavItemProps) => {
  return (
    <Box
      as="a"
      href="#"
      style={{ textDecoration: "none" }}
      _focus={{ boxShadow: "none" }}
    >
      <Flex
        align="center"
        p="4"
        mx="4"
        borderRadius="lg"
        role="group"
        cursor="pointer"
        _hover={{
          bg: "branding.100",
          color: "white",
        }}
        {...rest}
      >
        {icon && (
          <Icon
            mr="4"
            fontSize="16"
            _groupHover={{
              color: "white",
            }}
            as={icon}
          />
        )}
        {children}
      </Flex>
    </Box>
  );
};

const MobileNav = ({ onOpen, ...rest }: MobileProps) => {
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
                  {/* TODO: Make the type bigger to incorporate more 
                  this one to be Name */}
                  {user?.username}
                </Avatar>{" "}
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

const SidebarWithHeader = ({ children }: { children: ReactNode }) => {
  const { isOpen, onOpen, onClose } = useDisclosure();

  return (
    <Box minH="100vh" bg={useColorModeValue("gray.100", "gray.900")}>
      <SidebarContent
        onClose={() => onClose}
        display={{ base: "none", md: "block" }}
      />
      <Drawer
        isOpen={isOpen}
        placement="left"
        onClose={onClose}
        returnFocusOnClose={false}
        onOverlayClick={onClose}
        size="full"
      >
        <DrawerContent>
          <SidebarContent onClose={onClose} />
        </DrawerContent>
      </Drawer>
      {/* mobilenav */}
      <MobileNav onOpen={onOpen} />
      <Box ml={{ base: 0, md: 60 }} p="4">
        {children}
      </Box>
    </Box>
  );
};
export default SidebarWithHeader;
