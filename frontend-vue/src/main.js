import { createApp } from 'vue';
import App from './App.vue';
import router from './router/routes';
import { store } from './store/store';
import BootstrapVue3 from 'bootstrap-vue-3';
import { library } from '@fortawesome/fontawesome-svg-core';
import {
    faUserCircle, faUserCog, faUserTimes, faUserPlus, faLock, faCloud, faCamera,
    faDollarSign, faQuestionCircle, faPencilAlt, faKeyboard, faPlus, faFolder, faKey
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

library.add(faUserCircle, faUserCog, faUserTimes, faUserPlus, faLock, faCloud, faCamera, faDollarSign, faQuestionCircle, faPencilAlt, faKeyboard, faPlus, faFolder, faKey);
app.component('FontAwesomeIcon', FontAwesomeIcon);


app.mount('#app');