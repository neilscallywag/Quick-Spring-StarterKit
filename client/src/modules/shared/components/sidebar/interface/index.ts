import { IconType } from "react-icons";
import { BoxProps, FlexProps } from "@chakra-ui/react";

export interface NavItemProps extends FlexProps {
  icon: IconType;
  children: React.ReactNode;
}

export interface MobileNavProps extends FlexProps {
  onOpen: () => void;
}

export interface SidebarProps extends BoxProps {
  onClose: () => void;
}

export interface LinkItem {
  name: string;
  icon: IconType;
  href: string;
  permissions?: {
    resource: string;
    action: string;
  };
}
