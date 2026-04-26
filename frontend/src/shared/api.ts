import axios from "axios";

const apiConfig = {
  headers: {
    "Content-Type": "application/json",
  },
};

const publicApiConfig = {
  ...apiConfig,
  baseURL: "/api/public",
};

const privateApiConfig = {
  ...apiConfig,
  baseURL: "/api",
};

export const api = axios.create(publicApiConfig);

export const privateApi = axios.create({
  ...privateApiConfig,
  withCredentials: true,
});
