import { Helmet } from "react-helmet-async";
import { Box, Flex, Heading, Text } from "@chakra-ui/react";
import PrimaryButton from "../../../modules/shared/components/buttons/PrimaryButton";

const NotFound = () => {
  return (
    <Box textAlign="center" py={10} px={6} height="100vh">
      <Helmet>
        <title>Not Found</title>
        <meta name="description" content="Not Found" />
      </Helmet>
      <Flex
        w="100%"
        h="90%"
        direction="column"
        justifyContent="center"
        alignItems="center"
        pt="5"
      >
        <Heading
          display="inline-block"
          as="h2"
          size="2xl"
          bg="branding.100"
          backgroundClip="text"
          mb={-2}
        >
          404
        </Heading>
        <Text fontSize="xl" fontWeight="bold" color="white" mt={0} mb={0}>
          Page Not Found
        </Text>
        <Text color={"gray.600"} mb={6}>
          The page you&apos;re looking for does not seem to exist
        </Text>

        <PrimaryButton href="/">
          Go to Home
        </PrimaryButton>
      </Flex>
    </Box>
  );
};

export default NotFound;
