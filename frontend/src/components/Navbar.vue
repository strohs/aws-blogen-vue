<template>

  <b-navbar toggleable="sm" type="dark" variant="dark">

    <b-navbar-toggle target="nav_collapse"></b-navbar-toggle>

    <b-navbar-brand to="/">
      <app-blogen-logo :height="30" :width="30"></app-blogen-logo>
      Blogen
    </b-navbar-brand>

    <b-collapse is-nav id="nav_collapse">
      <b-navbar-nav>
        <b-nav-item to="/posts" class="mx-2" active-class v-if="isAuthenticated">Recent Posts</b-nav-item>
        <b-nav-item :to="{ name: 'users', params: { id: getAuthUser.id, userName: getAuthUser.userName, avatarImage: getAuthUser.avatarImage } }" class="mx-2" active-class v-if="isAuthenticated">User Posts</b-nav-item>
        <b-nav-item to="/categories" class="mx-2" active-class v-if="isAuthenticated && isAdmin">Categories</b-nav-item>
      </b-navbar-nav>

      <b-navbar-nav class="ml-auto">
        <b-nav-item-dropdown :text="'Welcome ' + getAuthUser.userName" left v-if="isAuthenticated">
          <b-dropdown-item to="/userProfile">
            <icon name="user-cog"></icon>
            Profile
          </b-dropdown-item>
          <b-dropdown-divider></b-dropdown-divider>
          <b-dropdown-item-button v-b-modal.authTokenModal>
            <icon name="lock"></icon>
            Authentication Token
          </b-dropdown-item-button>
        </b-nav-item-dropdown>

        <b-nav-item :to="{ name: 'clogin', params: { loginState: 'SIGN_IN' } }" class="mx-2" v-if="!isAuthenticated">
          <icon name="user-plus"></icon>
          Login or Sign-Up
        </b-nav-item>

        <!--<b-nav-item :to="{ name: 'clogin', params: { loginState: 'SIGN_IN' } }" class="mx-2" v-if="!isAuthenticated">-->
          <!--<icon name="user-circle"></icon>-->
          <!--Login-->
        <!--</b-nav-item>-->

        <b-nav-item :to="{ name: 'clogin', params: { loginState: 'LOGOUT' } }" class="mx-2" v-if="isAuthenticated">
          <icon name="user-times"></icon>
          Logout
        </b-nav-item>

      </b-navbar-nav>
    </b-collapse>

    <!-- Modal Component -->
    <b-modal id="authTokenModal" title="Your Current ID Token:">
      <b-form-textarea size="sm" rows="4" plaintext :value="getIdToken"></b-form-textarea>
    </b-modal>

  </b-navbar>

</template>

<script>
import { mapGetters } from 'vuex'
import BlogenLogo from './common/BlogenLogo'

export default {
  name: 'Navbar',
  components: {
    appBlogenLogo: BlogenLogo
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
