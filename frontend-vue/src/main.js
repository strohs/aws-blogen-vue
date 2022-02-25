import { createApp } from 'vue';
import App from './App.vue';
import router from './router/routes';
import { store } from './store/store';
import BootstrapVue3 from 'bootstrap-vue-3';
import { library } from '@fortawesome/fontawesome-svg-core';
import {
    faUserCircle, faUserCog, faUserTimes, faUserPlus, faLock, faCloud, faCamera,
    faDollarSign, faQuestionCircle, faPencilAlt, faKeyboard, faPlus, faFolder
} from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';

import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap-vue-3/dist/bootstrap-vue-3.css';
import './assets/styles/blogen-styles.scss';
// trigger Amplify configurations
import "./amplify-config";

const app = createApp(App);

app.use(router);
app.use(store);
app.use(BootstrapVue3);

library.add(faUserCircle, faUserCog, faUserTimes, faUserPlus, faLock, faCloud, faCamera, faDollarSign, faQuestionCircle, faPencilAlt, faKeyboard, faPlus, faFolder);
app.component('font-awesome-icon', FontAwesomeIcon);

app.mount('#app');




// import Amplify, * as AmplifyModules from 'aws-amplify'
// import { AmplifyPlugin } from 'aws-amplify-vue'
// import ampConfig from './amplify-config'

// // configure AWS Amplify with Cognito user pool ID, region, App Client ID
// Amplify.configure(ampConfig)
//
//
// // Vuelidate must appear first in this list and BootstapVue must appear last in this list
// Vue.use(AmplifyPlugin, AmplifyModules, BootstrapVue)
//
// /* eslint-disable no-new */
// new Vue({
//     router,
//     store,
//     render: h => h(App)
// }).$mount('#app')