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
        backgroundColor={props.colorScheme === "red" ? "red.600" : "branding.100"}
        _hover={{
          bg: props.colorScheme === "red" ? "red.700" : "#branding.200",
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
