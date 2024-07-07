import { ReactElement, forwardRef } from "react";
import { Button as ChakraButton, ButtonProps as ChakraButtonProps } from "@chakra-ui/react";

interface PrimaryButtonProps extends ChakraButtonProps {
  leftIcon?: ReactElement;
  rightIcon?: ReactElement;
  href?: string;
  size?: "small" | "medium" | "large";
  isFullWidth?: boolean; 
}

const PrimaryButton = forwardRef<HTMLButtonElement, PrimaryButtonProps>(
  ({ children, leftIcon, rightIcon, href, size = "medium", isFullWidth = false, ...props }, ref) => {
    const sizeStyles = {
      small: {
        minHeight: "32px",
        minWidth: "80px",
        fontSize: "sm",
      },
      medium: {
        minHeight: "40px",
        minWidth: "100px",
        fontSize: "md",
      },
      large: {
        minHeight: "48px",
        minWidth: "120px",
        fontSize: "lg",
      },
    };

    return (
      <ChakraButton
        as={href ? "a" : "button"}
        ref={ref}
        href={href}
        leftIcon={leftIcon}
        rightIcon={rightIcon}
        color="white"
        bg="#1A1E43"
        width={isFullWidth ? "100%" : undefined}
        border="none"
        _hover={{ bg: "#282e69", color: "white" }}
        _active={{ bg: "#3c4a8b", color: "white" }}
        _disabled={{ bg: "#d3d3d3", color: "#a0a0a0" }}
        {...sizeStyles[size]}
        {...props}
      >
        {children}
      </ChakraButton>
    );
  },
);

PrimaryButton.displayName = "PrimaryButton";

export default PrimaryButton;
