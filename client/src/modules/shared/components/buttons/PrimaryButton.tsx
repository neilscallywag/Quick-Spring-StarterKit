// PrimaryButton.tsx
import { ReactElement } from "react";
import { Button, ButtonProps } from "@chakra-ui/react";

interface PrimaryButtonProps extends ButtonProps {
  leftIcon?: ReactElement;
  href?: string;
}

const PrimaryButton: React.FC<PrimaryButtonProps> = ({
  leftIcon,
  href,
  children,
  ...props
}) => {
  return (
    <Button
      as={href ? "a" : "button"}
      href={href}
      leftIcon={leftIcon}
      color="white"
      bg="#1A1E43"
      border="none"
      _hover={{ bg: "#282e69", color: "white" }}
      {...props}
    >
      {children}
    </Button>
  );
};

export default PrimaryButton;
