import React, { useEffect, useState } from "react";
import { Navigate, Outlet } from "react-router-dom";

import { useAuth } from "../../modules/auth";
import Loader from "../../modules/shared/components/loader/Loader";
import SidebarWithHeader from "../../modules/shared/components/sidebar/Sidebar";

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
