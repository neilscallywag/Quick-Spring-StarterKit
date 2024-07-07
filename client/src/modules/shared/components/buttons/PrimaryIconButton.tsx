// PrimaryIconButton.tsx
import { ReactElement } from "react";
import { IconButton, IconButtonProps, Tooltip } from "@chakra-ui/react";

interface PrimaryIconButtonProps extends IconButtonProps {
  tooltipLabel: string;
  icon: ReactElement;
  href?: string;
}

const PrimaryIconButton: React.FC<PrimaryIconButtonProps> = ({
  tooltipLabel,
  icon,
  href,
  ...props
}) => {
  return (
    <Tooltip hasArrow label={tooltipLabel} bg="gray.300" color="black">
      <IconButton
        as={href ? "a" : "button"}
        icon={icon}
        backgroundColor={props.colorScheme === "red" ? "red.600" : "#1A1E43"}
        _hover={{
          bg: props.colorScheme === "red" ? "red.700" : "#282e69",
          color: "white",
        }}
        border="none"
        href={href}
        {...props}
      />
    </Tooltip>
  );
};

export default PrimaryIconButton;
