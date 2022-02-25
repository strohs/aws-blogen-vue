import {createRouter, createWebHistory} from 'vue-router'
import HomePage from '../components/home/HomePage.vue'
import BlogenAuthenticator from '../components/login/BlogenAuthenticator.vue'
import Signup from '../components/signup/Signup.vue'
import UserProfile from '../components/profile/UserProfile.vue'
import ListPosts from '../components/posts/ListPosts.vue'
import ListCategories from '../components/categories/ListCategories.vue'
import UserPosts from '../components/users/UserPosts.vue';
import NotFound from "../components/common/NotFound.vue";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'homepage', component: HomePage },
    { path: '/clogin',
      name: 'clogin',
      component: BlogenAuthenticator
    },
    { path: '/signup', name: 'signup', component: Signup },
    { path: '/users', name: 'users', component: UserPosts, props: true },
    { path: '/userProfile', name: 'userProfile', component: UserProfile },
    { path: '/posts', name: 'posts', component: ListPosts },
    { path: '/categories', name: 'categories', component: ListCategories },
    // this is the catch-all route that handles any URL not explicitly handled above it
    // { path: '/:pathMatch(.*)', name: 'not-found', component: NotFound },
  ]
});

export default router;
