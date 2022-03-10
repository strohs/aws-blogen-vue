<template>
  <header id="user-profile-header" class="container-fluid bg-yellow-300">
    <div class="row py-2">
      <div class="col-md-6">
        <h2>
          <font-awesome-icon
            icon="user-cog"
            class="me-2"
            scale="2"
          ></font-awesome-icon>
          Edit Profile
        </h2>
      </div>
    </div>
  </header>

  <div id="userProfile" class="container">
    <!-- Profile Card -->
    <div class="row my-4">
      <div class="col-6">
        <b-card no-body border-variant="warning">
          <b-card-header
            header-class="d-flex justify-content-between align-items-center"
          >
            <h5>Profile for {{ user.email }}</h5>
            <app-change-password
              @submit="doChangePassword"
            ></app-change-password>
          </b-card-header>

          <b-card-body>
            <app-status-alert
              v-model="showStatus"
              v-bind="status"
            ></app-status-alert>

            <app-user-profile-form
              v-bind="user"
              @validated="doChangeProfile"
            ></app-user-profile-form>
          </b-card-body>
        </b-card>
      </div>
    </div>
  </div>
</template>

<script>
// UserProfile
// Enables a user to edit their profile information
//
// Props: NONE
// Emits: NONE
//
import UserProfileForm from "./UserProfileForm.vue";
import StatusAlert from "../common/StatusAlert.vue";
import ChangePassword from "./ChangePassword.vue";
import logger from "../../configs/logger";

export default {
  name: "UserProfile",
  components: {
    appUserProfileForm: UserProfileForm,
    appStatusAlert: StatusAlert,
    appChangePassword: ChangePassword,
  },
  data() {
    return {
      user: {
        id: "",
        firstName: "",
        lastName: "",
        email: "",
        userName: "",
        avatarImage: "",
        password: "",
      },
      status: {
        code: 200,
        message: "",
      },
      showStatus: false,
    };
  },
  created() {
    // get the currently authenticated user details and store them here
    this.user = this.$store.getters.getAuthUser;
  },
  methods: {
    doChangeProfile(user) {
      logger.debug("change profile user info:", user);
      this.$store
        .dispatch("updateUserAttributes", user)
        .then(() => {
          this.displayStatusAlert(200, "Your profile was successfully changed");
        })
        .catch((error) => {
          this.displayStatusAlert(400, error.message);
        });
    },
    doChangePassword({ curPassword, newPassword }) {
      this.$store
        .dispatch("changeUserPassword", { curPassword, newPassword })
        .then(() => {
          // res can be; SUCCESS
          this.displayStatusAlert(
            200,
            "Your password was successfully changed"
          );
        })
        .catch((error) => {
          this.displayStatusAlert(400, error.message);
        });
    },
    displayStatusAlert(code, message) {
      this.status.code = code;
      this.status.message = message;
      // display the status code from CRUD request
      this.showStatus = true;
    },
    dismissStatusAlert() {
      //this.showStatus = false;
    },
  },
};
</script>

<style scoped></style>
