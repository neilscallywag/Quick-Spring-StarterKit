import { lazy, Suspense, useEffect } from "react";
import { Route, Routes } from "react-router-dom";
import { Box, Flex } from "@chakra-ui/react";
import Cookies from "js-cookie";

import useAuthStore from "~shared/store/AuthStore";

import { useAuth } from "~features/auth";

const Loader = lazy(() => import("~components/loader/Loader"));
const NotFound = lazy(() => import("~pages/notfound/NotFound"));
const Footer = lazy(() => import("~components/footer/Footer"));

const PublicRoute = lazy(() => import("~shared/routes/PublicRoute"));
const PrivateRoute = lazy(() => import("~shared/routes/PrivateRoute"));

// Public Page
const LoginPage = lazy(() => import("~pages/auth/Login"));
// Private Page
const ViewAllPage = lazy(() => import("~pages/users/ViewAll"));
const ViewUserPage = lazy(() => import("~pages/users/ViewUser"));

const EditUserPage = lazy(() => import("~pages/users/EditUser"));

const CreateUserPage = lazy(() => import("~pages/users/CreateUser"));
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
              <Route element={<PrivateRoute />}>
                <Route
                  path="/users/view/:userid/edit"
                  element={<EditUserPage />}
                />
              </Route>
              <Route element={<PrivateRoute />}>
                <Route path="/users/create" element={<CreateUserPage />} />
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
