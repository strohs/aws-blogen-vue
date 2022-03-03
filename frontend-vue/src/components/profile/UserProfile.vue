// Component for editing a users profile (i.e. their sign-up information
//
//

<template>

  <header id="user-profile-header" class="container-fluid bg-yellow-300">
    <div class="row py-2">
      <div class="col-md-6">
        <h2>
          <font-awesome-icon icon="user-cog" class="me-2" scale="2"></font-awesome-icon>
          Edit Profile
        </h2>
      </div>
    </div>
  </header>

  <div id="userProfile" class="container">

    <!-- alert message box -->
    <div class="row my-4 justify-content-center">
      <app-status-alert v-bind="status" @dismissed="dismissStatusAlert"></app-status-alert>
    </div>

    <!-- Profile Card -->
    <div class="row my-4">
      <div class="col-6">
        <b-card no-body border-variant="warning">
          <b-card-header class="text-start">
            <h4>Profile for {{ this.user.email }}</h4>
          </b-card-header>

          <b-card-body>
<!--            <app-edit-password></app-edit-password>-->
            <app-user-profile-form v-bind="user" @validated="doChangeProfile"></app-user-profile-form>
          </b-card-body>
        </b-card>
      </div>
    </div>


  </div>
</template>

<script>
import UserProfileForm from './UserProfileForm.vue'
import StatusAlert from '../common/StatusAlert.vue'
import EditPassword from './EditPassword.vue'

export default {
  name: 'UserProfile',
  components: {
    appUserProfileForm: UserProfileForm,
    appStatusAlert: StatusAlert,
    appEditPassword: EditPassword
  },
  data () {
    return {
      user: {
        id: '',
        firstName: '',
        lastName: '',
        email: '',
        userName: '',
        avatarImage: '',
        password: ''
      },
      status: {
        code: 200,
        message: '',
        show: false
      }
    }
  },
  methods: {
    doChangeProfile (user) {
      console.log('change profile user info:', user)
      this.$store.dispatch('updateUserAttributes', user)
        .then(res => {
          this.displayStatusAlert(200, 'Your profile was successfully changed');
        })
        .catch(error => {
          this.displayStatusAlert(400, error.message);
        })
    },
    doChangePassword ({ curPassword, newPassword }) {
      this.$store.dispatch('changeUserPassword', { curPassword, newPassword })
        .then(res => {
          // res can be; SUCCESS
          this.displayStatusAlert(200, 'Your password was successfully changed');
        })
        .catch(error => {
          this.displayStatusAlert(400, error.message);
        })
    },
    displayStatusAlert (code, message) {
      this.status.code = code;
      this.status.message = message;
      // display the status code from CRUD request
      this.status.show = true;
    },
    dismissStatusAlert () {
      this.status.show = false;
    }
  },
  created () {
    // get the currently authenticated user details and store them here
    this.user = this.$store.getters.getAuthUser;
  }
}
</script>

<style scoped>

</style>
