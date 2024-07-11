import React, { ReactNode } from "react";
import { Helmet } from "react-helmet-async";
import { Box, BoxProps, Flex, Heading } from "@chakra-ui/react";

interface ContainerProps extends BoxProps {
  children: ReactNode;
  pageTitle: string;
  metaDescription: string;
  headingText: string;
  primaryButton?: ReactNode;
}

const Container: React.FC<ContainerProps> = ({
  children,
  pageTitle,
  metaDescription,
  headingText,
  primaryButton,
  ...boxProps
}) => {
  return (
    <Box mt={0} p={1} {...boxProps}>
      <Helmet>
        <title>{pageTitle}</title>
        <meta name="description" content={metaDescription} />
      </Helmet>
      <Flex justifyContent="space-between" m={4} alignItems="center">
        <Heading as="h3" size="lg">
          {headingText}
        </Heading>
        {primaryButton}
      </Flex>
      {children}
    </Box>
  );
};

export default Container;
