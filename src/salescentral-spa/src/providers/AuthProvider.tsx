import { AuthProvider } from "@refinedev/core";

// https://github.com/refinedev/refine/blob/master/examples/auth-mantine/src/App.tsx

export const authProvider: AuthProvider = {
         login: async ({ username, password }) => {
                  const data = await fetch("http://localhost:8080/api/auth/login", {
                           method: "POST",
                           body: JSON.stringify({ username, password }),
                           headers: { "Content-Type": "application/json", "Accept": "application/json" },
                  }).then((res) => res.json());

                  if (data) {
                           localStorage.setItem('token', data.token);
                           return {
                                    success: true, redirectTo: "/teams", successNotification: {
                                             message: "Login Successful",
                                             description: "You have successfully logged in.",
                                    }
                           };
                  } else {
                           return {
                                    success: false,
                                    error: { name: "Login Error", message: "Invalid credentials" },
                                    key: ["fodassse"]
                           };
                  }
         },
         check: async () => {
                  console.log("AUTH CHECKING")
                  const token = localStorage.getItem("token");
                  if (token) {
                           console.log("AUTH TOKEN")
                           return {
                                    authenticated: true,
                           }
                  } else {
                           console.log("AUTH NO TOKEN")
                           return {
                                    authenticated: false,
                                    error: {
                                             message: "Check failed",
                                             name: "Not authenticated",
                                    },
                                    logout: true,
                                    redirectTo: "/login",
                           };
                  }
         },
         logout: async () => {
                  localStorage.removeItem("token");
                  return {
                           success: true,
                           redirectTo: "/login",
                  };
         },
         onError: async (params) => ({}),

         forgotPassword: async (params) => ({
                  success: false,
                  error: { name: "Login Error", message: "Invalid credentials" },
         }),
         updatePassword: async (params) => ({
                  success: false,
                  error: { name: "Login Error", message: "Invalid credentials" },
         }),
};


