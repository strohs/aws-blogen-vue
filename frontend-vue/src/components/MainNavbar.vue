<script>
// MainNavbar
// The navbar that displays across the top of all blogen pages.
// It provides a user the ability to navigate to all pages of the site,
// as well as the ability to sign-in or sign-up

import { mapGetters } from "vuex";
import BlogenLogo from "./common/BlogenLogo.vue";
import Modal from "./common/BaseModal.vue";

export default {
  name: "MainNavbar",
  components: {
    appBlogenLogo: BlogenLogo,
    appModal: Modal,
  },
  data() {
    return {
      isModalVisible: false,
    };
  },
  computed: {
    ...mapGetters([
      "isAuthenticated",
      "isAdmin",
      "getAuthUser",
      "getAuthUserId",
      "getIdToken",
    ]),
  },
  methods: {
    dismissModal() {
      this.isModalVisible = false;
    },
  },
};
</script>

<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container-fluid">
      <router-link to="/" class="navbar-brand">
        <app-blogen-logo :height="'30'" :width="'30'"></app-blogen-logo>
        Blogen
      </router-link>

      <button
        class="navbar-toggler"
        type="button"
        data-bs-toggle="collapse"
        data-bs-target="#navbarSupportedContent"
        aria-controls="navbarSupportedContent"
        aria-expanded="false"
        aria-label="Toggle navigation"
      >
        <span class="navbar-toggler-icon"></span>
      </button>

      <div id="navbarSupportedContent" class="collapse navbar-collapse">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item">
            <router-link
              v-if="isAuthenticated"
              to="/posts"
              class="nav-link"
              active-class="active"
              >All Posts</router-link
            >
          </li>
          <li class="nav-item">
            <router-link
              v-if="isAuthenticated"
              :to="{
                name: 'users',
                params: {
                  id: getAuthUser.id,
                  userName: getAuthUser.userName,
                  avatarImage: getAuthUser.avatarImage,
                },
              }"
              class="nav-link"
              active-class="active"
              >Your Posts</router-link
            >
          </li>
          <li class="nav-item">
            <router-link
              v-if="isAuthenticated && isAdmin"
              to="/categories"
              class="nav-link"
              active-class="active"
              >Edit Categories</router-link
            >
          </li>

          <li v-if="isAuthenticated" class="nav-item dropdown">
            <a
              id="navbarDropdown"
              class="nav-link dropdown-toggle"
              href="#"
              role="button"
              data-bs-toggle="dropdown"
              aria-expanded="false"
            >
              Welcome {{ getAuthUser.userName }}
            </a>
            <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
              <li>
                <router-link to="/userProfile" class="dropdown-item">
                  <font-awesome-icon icon="user-cog"></font-awesome-icon>
                  Edit User Profile
                </router-link>
              </li>
              <li><hr class="dropdown-divider" /></li>
              <li>
                <a
                  class="dropdown-item"
                  @click="isModalVisible = !isModalVisible"
                >
                  <font-awesome-icon icon="key"></font-awesome-icon>
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
  <Teleport to="body">
    <transition name="modal-fade">
      <app-modal
        v-show="isModalVisible"
        @cancel="dismissModal"
        @confirm="dismissModal"
      >
        <template #header>
          <h4 class="modal-title">Your Token</h4>
        </template>

        <template #body>
          <b-form-textarea
            id="authTokenTextArea"
            rows="4"
            plaintext
            :value="getIdToken"
          ></b-form-textarea>
        </template>
      </app-modal>
    </transition>
  </Teleport>
</template>

<style scoped></style>
