import { defineConfig } from "vite";
import react, { reactCompilerPreset } from "@vitejs/plugin-react";
import babel from "@rolldown/plugin-babel";

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), babel({ presets: [reactCompilerPreset()] })],
  server: {
    proxy: {
      // 경로가 '/api'로 시작하는 요청을 대상으로 설정
      "/api": {
        target: "http://localhost:8080", // 백엔드 서버 주소
        changeOrigin: true, // 대상 서버의 호스트 헤더를 target 주소로 변경 (CORS 회피 핵심)
        secure: false, // https 가짜 인증서 사용 시 false로 설정
        headers: {
          Origin: "http://localhost:8080",
        },
      },
    },
  },
  build: {
    outDir: "../src/main/resources/static",
    emptyOutDir: true,
  },
});
