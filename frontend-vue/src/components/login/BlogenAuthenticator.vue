<template>

  <div class="container">
    <div class="row mt-4">
      <div class="col">

        <authenticator
            :login-mechanisms="['email']"
            :sign-up-attributes="['email','preferred_username','given_name','family_name']"
        >
          <template v-slot:header>
            <div class="d-flex m-4 justify-content-center">
              <blogen-logo width="100" height="100"></blogen-logo>
            </div>
          </template>

          <template v-slot="{ user, signOut }">
            <div class="d-flex flex-column justify-content-center align-items-center">
              <blogen-logo class="m-4" width="100" height="100"></blogen-logo>
              <p>Confirm sign out for {{ user?.attributes?.email }}</p>
              <b-button class="bg-primary" @click="doSignOut">Sign Out</b-button>
            </div>
          </template>

        </authenticator>

      </div>
    </div>
  </div>

</template>

<script setup>
// You can use useAuthenticator composable to access current signed in user. If no user is authenticated, it'll return undefined.
import { Authenticator, useAuthenticator } from "@aws-amplify/ui-vue";
import { Auth } from 'aws-amplify';
import '@aws-amplify/ui-vue/styles.css';
import BlogenLogo from '../common/BlogenLogo.vue';
import {watch, toRefs} from "vue";
import { useRouter } from 'vue-router';
import { useStore } from "vuex";


const { route, user } = toRefs(useAuthenticator());
const router = useRouter();
const store = useStore();

// watch for Authenticator.route changes and automatically send a user to the Posts page
// when they successfully sign in
watch(route, async (newRoute, oldRoute) => {
  console.log('oldRoute', oldRoute, ' newRoute', newRoute);

  // if this is a new user sign-in or a password change then after successfully authenticating,
  // send the user to the list-posts page
  if ((oldRoute === 'signIn' && newRoute === 'authenticated') ||
      (oldRoute === 'forceNewPassword' && newRoute === 'authenticated')) {
    // authenticate the user locally so that we store their data in vuex
    await store.dispatch('authenticateUser');
    await router.push({ name: 'posts'});
  }
});


// sign out user from Cognito and this website
async function doSignOut() {
  await store.dispatch('doLogout');
}



</script>

<style scoped>

</style>