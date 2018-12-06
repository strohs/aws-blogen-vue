import Vue from 'vue'
import Vuex from 'vuex'
import userAuthenticationModule from './modules/userAuthentication'
import blogenRestApiModule from './modules/blogenRestApi'

Vue.use(Vuex)

export const store = new Vuex.Store({
  modules: {
    userAuth: userAuthenticationModule,
    blogenRestApi: blogenRestApiModule
  }
})
