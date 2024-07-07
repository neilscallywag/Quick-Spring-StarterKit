import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Box,
  CloseButton,
  Flex,
  Text,
  useColorModeValue,
} from "@chakra-ui/react";

import { useAuth } from "../../../../modules/auth";

import { LinkItem, SidebarProps } from "./interface";
import { linkItems } from "./LinkItems";
import { NavItem } from "./NavItem";

export const SidebarContent = ({ onClose, ...rest }: SidebarProps) => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [availableLinks, setAvailableLinks] = useState<LinkItem[]>([]);

  useEffect(() => {
    const checkPermissions = async () => {
      const filteredLinks = linkItems.filter((link) => {
        if (link.permissions) {
          const hasAccess = true; // Placeholder for actual permission check
          return hasAccess;
        }
        return true;
      });
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
      <Flex
        h="20"
        mb="4"
        alignItems="center"
        mx="8"
        justifyContent="space-between"
      >
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
