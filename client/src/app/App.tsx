import { lazy, Suspense, useEffect } from "react";
import { Route, Routes } from "react-router-dom";
import { Box, Flex } from "@chakra-ui/react";
import Cookies from "js-cookie";

import { useAuth } from "../modules/auth";
import useAuthStore from "../store/AuthStore";

const Loader = lazy(() => import("../modules/shared/components/loader/Loader"));
const NotFound = lazy(() => import("../modules/auth/pages/NotFound"));
const Footer = lazy(() => import("../modules/shared/components/footer/Footer"));

const PublicRoute = lazy(() => import("./routes/PublicRoute"));
const PrivateRoute = lazy(() => import("./routes/PrivateRoute"));

// Public Page
const LoginPage = lazy(() => import("../modules/auth/pages/Login"));
// Private Page
const ViewAllPage = lazy(() => import("../modules/users/pages/ViewAll"));
const ViewUserPage = lazy(() => import("../modules/users/pages/ViewUser"));

const App = () => {
  const { isAuthenticated } = useAuth();
  useEffect(() => {
    const authTokenCookie = Cookies.get("auth_token");

    const handleStorageChange = (event: StorageEvent) => {
      if (
        (event.key === "auth-storage" && !event.newValue) ||
        !authTokenCookie
      ) {
        useAuthStore.getState().logout();
      }
    };

    window.addEventListener("storage", handleStorageChange);

    return () => window.removeEventListener("storage", handleStorageChange);
  }, []);
  return (
    <Flex direction="column" minH="100vh" bg={"white"}>
      <Suspense fallback={<Loader />}>
        <Box flex="1" bg="white">
          <Suspense fallback={<Loader />}>
            <Routes>
              <Route
                element={
                  <PublicRoute
                    strict={true}
                    isAuthenticated={isAuthenticated}
                  />
                }
              >
                <Route path="/" element={<LoginPage />} />
                <Route path="/login" element={<LoginPage />} />
              </Route>

              <Route element={<PrivateRoute />}>
                <Route path="/users/viewall" element={<ViewAllPage />} />
              </Route>
              <Route element={<PrivateRoute />}>
                <Route path="/users/view/:userid" element={<ViewUserPage />} />
              </Route>
              <Route element={<PrivateRoute />}></Route>
              <Route path="*" element={<NotFound />} />
            </Routes>
          </Suspense>
        </Box>
        <Footer />
      </Suspense>
    </Flex>
  );
};

export default App;
