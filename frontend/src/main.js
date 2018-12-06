import '@babel/polyfill'
// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
// import Vuelidate from 'vuelidate'
import './plugins/bootstrap-vue'
import BootstrapVue from 'bootstrap-vue'
import App from './App'
import router from './router'
import { store } from './store/store.js'
import Icon from 'vue-awesome/components/Icon'
import Amplify, * as AmplifyModules from 'aws-amplify'
import { AmplifyPlugin } from 'aws-amplify-vue'
import ampConfig from './amplify-config'
import 'vue-awesome/icons/user-circle'
import 'vue-awesome/icons/user-cog'
import 'vue-awesome/icons/user-times'
import 'vue-awesome/icons/user-plus'
import 'vue-awesome/icons/lock'
import 'vue-awesome/icons/cloud'
import 'vue-awesome/icons/camera'
import 'vue-awesome/icons/dollar-sign'
import 'vue-awesome/icons/question-circle'
import 'vue-awesome/icons/pencil-alt'
import 'vue-awesome/icons/keyboard'
import 'vue-awesome/icons/plus'
import 'vue-awesome/icons/folder'

import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

// configure AWS Amplify with Cognito user pool ID, region, App Client ID
Amplify.configure(ampConfig)

// Vue Awesome component that uses Font-Awesome icons
Vue.component('icon', Icon)

// Vuelidate must appear first in this list and BootstapVue must appear last in this list
Vue.use(AmplifyPlugin, AmplifyModules, BootstrapVue)

/* eslint-disable no-new */
new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
