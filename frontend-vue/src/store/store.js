import { createStore } from "vuex";
import userAuthenticationModule from "./modules/userAuthentication.js";
import blogenRestApiModule from "./modules/blogenRestApi.js";

export const store = createStore({
  modules: {
    userAuth: userAuthenticationModule,
    blogenRestApi: blogenRestApiModule,
  },
});
