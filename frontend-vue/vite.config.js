import {defineConfig} from "vite";
import vue from "@vitejs/plugin-vue";

// https://vitejs.dev/config/
// the resolve property (below) is required by amplifyUI
export default defineConfig({
    plugins: [vue()],
    resolve: {
        alias: [
            {
                find: "./runtimeConfig",
                replacement: "./runtimeConfig.browser",
            },
        ],
    },
});
