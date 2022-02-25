// Component for editing a users profile (i.e. their sign-up information
//
//

<template>
  <div id="userProfile">
    <header id="user-profile-header" class="py-2 bg-info text-white">
      <div class="container">
        <div class="row">
          <div class="col-md-6">
            <h1>
              <font-awesome-icon icon="user-cog" class="me-2" scale="2"></font-awesome-icon>
              User Profile
            </h1>
          </div>
        </div>
      </div>
    </header>

    <div class="container">
      <!-- alert message box -->
      <div class="row my-4 justify-content-center">
        <app-status-alert v-bind="status" @dismissed="dismissStatusAlert"></app-status-alert>
      </div>
      <b-card class="my-4" no-body border-variant="info">
        <b-card-header class="text-start">
          <div class="row">
            <h4>Profile</h4>
            <app-edit-password class="ms-auto" @submit="doChangePassword"></app-edit-password>
          </div>
        </b-card-header>
        <b-card-body>
          <div class="row">

            <div class="col">
              <app-user-profile-form v-bind="user" @validated="doChangeProfile"></app-user-profile-form>
            </div>
            <div class="col">
            </div>
          </div>

        </b-card-body>
      </b-card>
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
          this.displayStatusAlert(200, 'Your profile was successfully changed')
        })
        .catch(error => {
          this.displayStatusAlert(400, error.message)
        })
    },
    doChangePassword ({ curPassword, newPassword }) {
      this.$store.dispatch('changeUserPassword', { curPassword, newPassword })
        .then(res => {
          // res can be; SUCCESS
          this.displayStatusAlert(200, 'Your password was successfully changed')
        })
        .catch(error => {
          this.displayStatusAlert(400, error.message)
        })
    },
    displayStatusAlert (code, message) {
      this.status.code = code
      this.status.message = message
      // display the status code from CRUD request
      this.status.show = true
    },
    dismissStatusAlert () {
      this.status.show = false
    }
  },
  created () {
    this.user = this.$store.getters.getAuthUser
  }
}
</script>

<style scoped>

</style>
