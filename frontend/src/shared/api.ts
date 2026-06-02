import axios from "axios";

const apiConfig = {
  headers: {
    "Content-Type": "application/json",
  },
  // csrf 설정을 axios 기본값과 다르게 설정하려면 여기에 설정
  // xsrfCookieName: 'XSRF-TOKEN', // 읽어올 쿠키 이름
  // xsrfHeaderName: 'X-XSRF-TOKEN' // 보낼 헤더 이름
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
