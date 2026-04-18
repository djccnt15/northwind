import axios from "axios";

const apiConfig = {
  baseURL: "/api",
  headers: {
    "Content-Type": "application/json",
  },
};

export const api = axios.create(apiConfig);

export const privateApi = axios.create({
  ...apiConfig,
  withCredentials: true,
});
