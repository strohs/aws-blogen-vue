// This is the main login handling component for the Blogen website
// it handles user SignUp and SignIn and orchestrates the transitions between the forms of the login process
//
<template>
  <!-- LOGIN -->
    <section id="clogin">
        <div class="">
            <div class="row justify-content-center my-2">
                <app-status-alert v-bind="statusObj" @dismissed="statusObj.show=false"></app-status-alert>
            </div>
            <div class="row my-2 no-gutters">
                <div class="col-md-6 mx-auto">
                    <transition name="flip" mode="out-in">
                        <!-- Sign-In Form -->
                        <div v-if="activeForm.signIn" key="signIn">
                            <appSignIn
                                    @resetPassword="doForgotPassword"
                                    @newPassword="doNewPasswordChallenge"
                                    @signUp="setFormState('SIGN_UP')"
                                    @error="handleError"
                            >
                            </appSignIn>
                        </div>

                        <!-- SignUp Form -->
                        <div v-else-if="activeForm.signUp" key="signUp">
                            <appSignUp
                                    @success="doPostSignUp"
                                    @cancel="doCancel"
                                    @error="handleError"
                            >
                            </appSignUp>
                        </div>

                        <!-- Enter Confirmation Code -->
                        <div v-else-if="activeForm.confirmCode" key="confirmCode">
                            <appConfirmationCode
                                    :email="user.email"
                                    @success="blogenSignIn"
                                    @error="handleError"
                            >
                            </appConfirmationCode>
                        </div>

                        <!-- Forgot Password Form -->
                        <b-card v-else-if="activeForm.forgotPassword" key="forgotPassword" no-body>
                            <b-card-header header-text-variant="dark" header-bg-variant="light">
                                <div class="row">
                                    <h4>
                                        <font-awesome-icon class="mx-2" icon="user-circle" scale="2"></font-awesome-icon>
                                        Forgot Password
                                    </h4>
                                </div>
                            </b-card-header>
                            <b-card-body>
                                <appForgotPasswordForm :email="user.email" @validated="doForgotPasswordSubmit">
                                </appForgotPasswordForm>
                            </b-card-body>
                        </b-card>

                        <!-- New Password Form -->
                        <b-card v-else-if="activeForm.newPassword" key="newPassword" no-body>
                            <b-card-header header-text-variant="dark" header-bg-variant="light">
                                <div class="row">
                                    <h4>
                                        <font-awesome-icon class="mx-2" icon="user-circle" scale="2"></font-awesome-icon>
                                        Fill in the following fields to complete sign-up
                                    </h4>
                                </div>
                            </b-card-header>
                            <b-card-body>
                                <appChallengeForm @validated="doChallenge" @cancel="doCancel">
                                </appChallengeForm>
                            </b-card-body>
                        </b-card>
                    </transition>
                </div>
            </div>
        </div>
    </section>
</template>

<script>
import StatusAlert from '@/components/common/StatusAlert'
import SignIn from '@/components/login/SignIn'
import SignUp from '@/components/login/SignUp'
import ConfirmationCode from '@/components/login/ConfirmationCode'
import ForgotPasswordForm from '@/components/login/ForgotPasswordForm'
import ChallengeForm from '@/components/login/ChallengeForm'

export default {
  name: 'Clogin',
  components: {
    appStatusAlert: StatusAlert,
    appSignIn: SignIn,
    appSignUp: SignUp,
    appForgotPasswordForm: ForgotPasswordForm,
    appConfirmationCode: ConfirmationCode,
    appChallengeForm: ChallengeForm
  },
  props: {
    // loginState will be one of SIGN_IN, SIGN_UP, CONFIRM_CODE, FORGOT_PASSWORD
    loginState: String,
    message: ''
  },
  data () {
    return {
      user: {
        email: '',
        password: '',
        preferredUsername: '',
        firstName: '',
        lastName: ''
      },
      confCode: '',
      newPassword: '',
      // holds cognito user data
      cogUser: null,
      statusObj: {
        code: 200,
        message: '',
        show: false
      },
      activeForm: {
        signIn: true,
        signUp: false,
        newPassword: false,
        confirmCode: false,
        forgotPassword: false
      }
    }
  },
  created () {
    if (this.loginState === 'LOGOUT') {
      this.doLogout()
    } else if (this.message) { // if a status message was set, show it
      this.setStatusMsg(400, this.message, true)
    } else {
      // begin the sign-in/sign-up process
      this.setFormState(this.loginState)
    }
  },
  methods: {
    // this is used to set the active form during the sign-in/sign-up process
    setFormState (newLoginState) {
      switch (newLoginState) {
        case 'SIGN_IN':
          this.switchForm(true, false, false, false, false)
          break
        case 'SIGN_UP':
          this.switchForm(false, true, false, false, false)
          break
        case 'CONFIRM_CODE':
          this.switchForm(false, false, true, false, false)
          break
        case 'NEW_PASSWORD_REQUIRED':
          this.switchForm(false, false, false, true, false)
          break
        case 'FORGOT_PASSWORD':
          this.switchForm(false, false, false, false, true)
          break
      }
    },
    switchForm (signIn, signUp, confirmCode, newPassword, forgotPassword) {
      this.activeForm = { signIn, signUp, confirmCode, newPassword, forgotPassword }
    },
    doCancel () {
      this.setFormState('SIGN_IN')
    },
    // copy the sign-up form data and then transition to the Confirmation Code form
    doPostSignUp (signUpFormData) {
      Object.assign(this.user, signUpFormData)
      this.setFormState('CONFIRM_CODE')
    },
    doNewPasswordChallenge (userData) {
      // save the cognito user data into our local data and then transition to the new password form
      this.cogUser = userData.cogUser
      this.user.email = userData.email
      this.user.password = userData.password
      this.setFormState('NEW_PASSWORD_REQUIRED')
    },
    doForgotPassword (email) {
      console.log('doForgotPassword:', email)
      this.user.email = email
      this.$store.dispatch('doForgotPassword', email)
        .then(data => {
          this.setFormState('FORGOT_PASSWORD')
        })
        .catch(err => {
          this.setStatusMsg(400, err.message, true)
        })
    },
    doForgotPasswordSubmit (evt) {
      console.log('doForgotPasswordSUBMIT', evt)
      this.$store.dispatch('doForgotPasswordSubmit', { email: this.user.email, confCode: evt.confCode, newPassword: evt.password })
        .then(data => {
          this.setFormState('SIGN_IN')
          this.setStatusMsg(200, 'Your Password has been changed. Please login', true)
        })
        .catch(err => {
          this.setStatusMsg(400, err.message, true)
        })
    },
    // this is for when a user has been issued a NEW_PASSWORD_REQUIRED challenge by cognito
    doChallenge (userAttrs) {
      this.user.password = userAttrs.password
      this.$store.dispatch('doCognitoChallenge', { cogUserData: this.cogUser, userAttrs: userAttrs })
        .then(data => this.blogenSignIn(this.user.email, userAttrs.password))
        .catch(err => {
          console.log('new password log-in flow got an error', err)
          this.setStatusMsg(400, err.message, true)
          this.setFormState('SIGN_IN')
        })
    },
    // sign in a user to blogen/cognito using their email address and password
    blogenSignIn (email = this.user.email, password = this.user.password) {
      this.$store.dispatch('doSignIn', { email, password })
        .then(authUser => this.$store.dispatch('setIdToken'))
        .then(userSession => this.$store.dispatch('setCurrentUserInfo'))
        .then(userInfo => this.$router.push({ name: 'posts' }))
        .catch(err => {
          this.setStatusMsg(400, err.message, true)
        })
    },
    handleError ({ code, message }) {
      this.setStatusMsg(code, message, true)
    },
    setStatusMsg (code, message, show) {
      this.statusObj = { code, message, show }
    },
    doLogout () {
      this.$store.dispatch('doLogout')
      this.setFormState('SIGN_IN')
      this.setStatusMsg(200, 'You have been logged out', true)
    }
  }
}
</script>

<style scoped>
    .flip-enter-active, .flip-leave-active {
        transition: 0.3s ease-out;
    }

    .flip-enter, .flip-leave-to {
        transform: scaleX(0);
    }

</style>
