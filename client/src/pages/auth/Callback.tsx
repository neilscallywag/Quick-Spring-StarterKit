import { useEffect } from "react";

import { useAuth } from "~features/auth";

const CallbackPage = () => {
  const { getUserData } = useAuth();

  useEffect(() => {
    getUserData();
  }, []);

  return <></>;
};

export default CallbackPage;
