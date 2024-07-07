import { FiCompass, FiUser } from "react-icons/fi";

import { LinkItem } from "./interface";

export const linkItems: LinkItem[] = [
  {
    name: "View All Users",
    icon: FiUser,
    href: "/users/viewall",
    permissions: {
      resource: "UserStorage",
      action: "read",
    },
  },
  {
    name: "View Logs",
    icon: FiCompass,
    href: "/logs/viewall",
    permissions: {
      resource: "Logs",
      action: "read",
    },
  },
];
