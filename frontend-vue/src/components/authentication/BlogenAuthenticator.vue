<template>
  <div class="container">
    <div class="row mt-4">
      <div class="col">
        <authenticator
          :services="services"
          :initial-state="initialState"
          :login-mechanisms="['email']"
          :sign-up-attributes="[
            'email',
            'preferred_username',
            'given_name',
            'family_name',
          ]"
        >
          <template #header>
            <div class="d-flex m-4 justify-content-center">
              <blogen-logo width="100" height="100"></blogen-logo>
            </div>
          </template>

          <!-- Template for when user is already signed in -->
          <template #default="{ user }">
            <div
              class="d-flex flex-column justify-content-center align-items-center"
            >
              <blogen-logo class="m-4" width="100" height="100"></blogen-logo>
              <p>Confirm sign out for {{ user?.attributes?.email }}</p>
              <b-button class="bg-primary" @click="doSignOut"
                >Sign Out</b-button
              >
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
import { Auth } from "aws-amplify";
import "@aws-amplify/ui-vue/styles.css";
import BlogenLogo from "../common/BlogenLogo.vue";
import {watch, toRefs} from "vue";
import { useRouter } from "vue-router";
import { useStore } from "vuex";

const { route } = toRefs(useAuthenticator());
const router = useRouter();
const store = useStore();
import logger from "../../configs/logger";

const props = defineProps({
  initialState: {
    type: String,
    default: "signIn",
  },
});

// override Amplify's default signUp call to use our custom one that sets the users default avatar image
const services = {
  async handleSignUp(formData) {
    let { username, password, attributes } = formData;
    // default the user's avatar image to avatar0.jpg
    attributes["custom:avatar_file_name"] = "avatar0.jpg";
    return Auth.signUp({
      username,
      password,
      attributes,
    });
  },
};

// watch for Authenticator.route changes and automatically send a user to the Posts page
// when they successfully sign in
watch(route, async (newRoute, oldRoute) => {
  logger.debug("oldRoute", oldRoute, " newRoute", newRoute);

  // if this is a new user sign-in or a password change then after successfully authenticating,
  // send the user to the list-posts page
  if (
    (oldRoute === "signIn" && newRoute === "authenticated") ||
    (oldRoute === "forceNewPassword" && newRoute === "authenticated")
  ) {
    // authenticate the user locally so that we store their data in vuex
    await store.dispatch("authenticateUser");
    await router.push({ name: "posts" });
  }

  // if a new user has finished registration, call the REST API to complete their registration and
  // set their cognito Group(s). Typically, in a production environment, this could be handled by an AWS Lambda
  // function. We are manually doing it here to keep aws resource usage low
  // NOTE: this if block is not needed if the PostConfirmationLambda is running. We are specifically checking for
  // development because
  if (
    import.meta.env.MODE === "development" &&
    oldRoute === "confirmSignUp" &&
    newRoute === "setup"
  ) {
    await store.dispatch("registerNewUser", Auth.user.username);
    // refresh tokens
    await store.dispatch("forceTokenRefresh");
    await router.push({ name: "posts" });
  }
});

// sign out user from Cognito and this website
async function doSignOut() {
  await store.dispatch("doLogout");
}
</script>

<style scoped></style>
