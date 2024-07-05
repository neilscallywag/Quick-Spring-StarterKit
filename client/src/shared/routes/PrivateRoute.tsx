import React, { useEffect, useState } from "react";
import { Navigate, Outlet } from "react-router-dom";

import Loader from "~components/loader/Loader";
import SidebarWithHeader from "~components/sidebar/Sidebar";

import { useAuth } from "~features/auth";

const PrivateRoute: React.FC = () => {
  const { isAuthenticated } = useAuth();
  const [checkingAuth, setCheckingAuth] = useState(true);

  useEffect(() => {
    if (isAuthenticated !== undefined) {
      setCheckingAuth(false);
    }
  }, [isAuthenticated]);

  if (checkingAuth) {
    return <Loader />;
  }

  if (!isAuthenticated) {
    return <Navigate to={"/login"} />;
  }

  return (
    <SidebarWithHeader>
      <Outlet />
    </SidebarWithHeader>
  );
};

export default PrivateRoute;
