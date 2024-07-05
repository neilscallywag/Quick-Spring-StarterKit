import { Box, Flex, Stack, Text } from "@chakra-ui/react";

const Footer = () => {
  return (
    <Box bg={"white"} color={"branding.100"} ml={0}>
      <Flex
        // maxW={"6xl"}
        py={4}
        p={5}
        direction={{ base: "column", md: "row" }}
        justify={{ base: "center", md: "space-between" }}
        align={{ base: "center", md: "center" }}
      >
        <Text ml="20%">
          © {new Date().getFullYear()} Neil. All rights reserved
        </Text>
        <Stack direction={"row"} spacing={6}></Stack>
      </Flex>
    </Box>
  );
};

export default Footer;
