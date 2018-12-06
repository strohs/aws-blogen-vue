import Vue from 'vue'
import Router from 'vue-router'
import Home from '../components/home/Home'
import Clogin from '../components/login/Clogin'
import Signup from '../components/signup/Signup'
import UserProfile from '../components/profile/UserProfile'
import Posts from '../components/posts/Posts'
import Categories from '../components/categories/Categories'
import Users from '../components/users/Users'

Vue.use(Router)

export default new Router({
  routes: [
    { path: '/', name: 'home', component: Home },
    // this path is used for redirects from the cognito hosted UI
    { path: '/cognito-signin', name: 'home', component: Home },
    { path: '/clogin',
      name: 'clogin',
      component: Clogin,
      props: true
    },
    { path: '/signup', name: 'signup', component: Signup },
    { path: '/users', name: 'users', component: Users, props: true },
    { path: '/userProfile', name: 'userProfile', component: UserProfile },
    { path: '/posts', name: 'posts', component: Posts },
    { path: '/categories', name: 'categories', component: Categories },
    // this is the catch all route that handles any URL not explicitly handled above it
    { path: '*', redirect: '/' }
  ],
  mode: 'history'
})
