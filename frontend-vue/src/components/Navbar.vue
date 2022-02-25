<template>

  <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container-fluid">

      <router-link to="/" class="navbar-brand">
        <app-blogen-logo :height="'30'" :width="'30'"></app-blogen-logo>
        Blogen
      </router-link>

      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item">
            <router-link to="/posts" class="nav-link" active-class="active" v-if="isAuthenticated">Recent Posts</router-link>
          </li>
          <li class="nav-item">
            <router-link :to="{ name: 'users', params: { id: getAuthUser.id, userName: getAuthUser.userName, avatarImage: getAuthUser.avatarImage } }" class="nav-link" active-class="active" v-if="isAuthenticated">User Posts</router-link>
          </li>
          <li class="nav-item">
            <router-link to="/categories" class="nav-link" active-class="active" v-if="isAuthenticated && isAdmin">Categories</router-link>
          </li>

          <li v-if="isAuthenticated" class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
              Welcome {{ getAuthUser.userName }}
            </a>
            <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
              <li>
                <router-link to="/userProfile" class="dropdown-item">
                  <font-awesome-icon icon="user-cog"></font-awesome-icon>
                  Profile
                </router-link>
              </li>
              <li><hr class="dropdown-divider"></li>
              <li>
                <a class="dropdown-item" data-bs-toggle="modal" data-bs-target="#bsModal">
                  <font-awesome-icon icon="lock"></font-awesome-icon>
                  Display Token
                </a>
              </li>
            </ul>
          </li>
        </ul>

        <!-- login/logout links at end of navbar -->
        <ul class="navbar-nav mb-2 mb-lg-0">
          <li v-if="!isAuthenticated" class="nav-item">
            <router-link :to="{ name: 'clogin' }">
              <font-awesome-icon icon="user-plus"></font-awesome-icon>
              Sign-In or Sign-Up
            </router-link>
          </li>

          <li v-if="isAuthenticated" class="nav-item">
            <router-link :to="{ name: 'clogin' }">
              <font-awesome-icon icon="user-times"></font-awesome-icon>
              Sign Out
            </router-link>
          </li>
        </ul>

      </div>
    </div>
  </nav>

  <!-- Modal for auth token -->
  <blogen-modal header-bg-color="bg-primary">
    <template #header-title>Auth token</template>
    <template #body>
      <b-form-textarea id="authTokenTextArea" rows="4" plaintext :value="getIdToken"></b-form-textarea>
    </template>
  </blogen-modal>

</template>

<script>
import { mapGetters } from 'vuex'
import BlogenLogo from './common/BlogenLogo.vue'
import BlogenModal from "./common/BlogenModal.vue";
import BlogenAuthenticator from "./login/BlogenAuthenticator.vue";

export default {
  name: 'Navbar',
  components: {
    appBlogenLogo: BlogenLogo,
    BlogenModal,
    BlogenAuthenticator,

  },
  computed: {
    ...mapGetters([
      'isAuthenticated',
      'isAdmin',
      'getAuthUser',
      'getAuthUserId',
      'getIdToken'
    ])
  }
}
</script>

<style scoped>

</style>
