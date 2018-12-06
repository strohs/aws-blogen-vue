// this component is for capturing the sign-in information of a Blogen user. it encapsulates the logic for validating
// the sign-in form inputs and then emits the inputs to the parent when the form's submit button is clicked
//
// Props:
//  you may pass the following optional props that will be used to pre-fill the input form
//   email
//   password
// Emits:
//  ('newPassword') - this event is emitted when cognito requires a new password to be entered
//  ('resetPassword', email) - indicates the user wants to reset their password
//  ('signUp') - indicates the Sign-Up link was clicked
//  ('error', { code, message }) - this event is emitted when some other error from Cognito
<template>
    <b-card border-variant="primary" no-body>
        <b-card-header>
            <div class="row">
                <h4>
                    <icon class="mx-2" name="user-circle" scale="2"></icon>
                    Blogen Sign-In
                </h4>
                <b-link class="ml-auto mx-2" @click="$emit('signUp')">
                    <p>New User? Sign-Up Here</p>
                </b-link>
            </div>
        </b-card-header>

        <b-card-body>
            <b-form>
                <b-form-group id="emailGroup1" label="Email" label-for="email1"
                              :state="$v.form.email.email && !$v.form.email.$invalid"
                              :valid-feedback="emailValidText"
                              :invalid-feedback="emailInvalidText">
                    <b-form-input id="email1"
                                  type="email"
                                  v-model="form.email"
                                  placeholder="Email"
                                  @input="$v.form.email.$touch()"></b-form-input>
                </b-form-group>

                <b-form-group id="passwordGroup1" label="Password" label-for="password1"
                              :state="!$v.form.password.$invalid"
                              :valid-feedback="passwordValidText"
                              :invalid-feedback="passwordInvalidText">

                    <b-form-input id="password1"
                                  type="password"
                                  v-model="form.password"
                                  @input="$v.form.password.$touch()"></b-form-input>
                    <b-link @click="forgotPassword = true">
                        <small>Forgot Password?</small>
                    </b-link>
                    <div>
                        <small v-if="forgotPassword === true">Enter your email address above and click the Reset Password button</small>
                    </div>

                </b-form-group>

                <b-button
                        block
                        :disabled="$v.form.$invalid"
                        size="lg"
                        type="submit"
                        variant="primary"
                        @click.stop.prevent="doSignIn"
                >
                    Login
                </b-button>

                <div v-if="forgotPassword === true">
                    <b-button class="my-2" :disabled="$v.form.email.$invalid"
                              block size="lg" type="submit" variant="warning"
                              @click="doForgotPassword"
                    >
                        Reset Password
                    </b-button>
                </div>

            </b-form>
        </b-card-body>

        <b-card-footer></b-card-footer>
    </b-card>
</template>

<script>
import { validationMixin } from 'vuelidate'
import { required, email, minLength } from 'vuelidate/lib/validators'
import * as vt from '@/validators/validationText'

export default {
  name: 'SignIn',
  mixins: [validationMixin],
  props: {
    email: String,
    password: String
  },
  data () {
    return {
      form: {
        email: this.email,
        password: this.password
      },
      forgotPassword: false,
      cogUser: null // holds user details returned from cognito
    }
  },
  validations: {
    form: {
      email: {
        required,
        email
      },
      password: {
        required,
        minLength: minLength(8),
        hasUpperCase: val => { return val.search(/[A-Z]/) >= 0 }
      }
    }
  },
  computed: {
    emailValidText () { return vt.emailValidText(this.$v.form.email) },
    emailInvalidText () { return vt.emailInvalidText(this.$v.form.email) },
    passwordValidText () { return vt.requiredFieldValidText(this.$v.form.password) },
    passwordInvalidText () { return vt.requiredFieldInvalidText(this.$v.form.password, 'password required') }
  },
  methods: {
    doForgotPassword (evt) {
      evt.preventDefault()
      this.$emit('resetPassword', this.form.email)
    },
    // try to sign in to Cognito using the user's email and password
    async doSignIn () {
      let evt = { email: this.form.email, password: this.form.password }
      console.log('do SIGN_IN with:', evt)
      // login to Cognito
      this.$store.dispatch('doSignIn', evt)
        .then(userData => {
          // check if any challenges were set, blogen can handle 'NEW_PASSWORD_REQUIRED'...for now
          if (userData.challengeName) {
            console.log(`challenge issued: ${userData.challengeName} with required attrs:`, userData.challengeParam.requiredAttributes)
            console.log('user attributes:', userData.challengeParam.userAttributes)
            this.cogUser = userData
            // going to force user to enter the required attributes, before logging them in
            return Promise.reject(userData.challengeName)
          } else {
            // user has provided valid credentials with no challenges, save their info in Vuex
            return this.$store.dispatch('setIdToken')
          }
        })
        .then(userSession => this.$store.dispatch('setCurrentUserInfo'))
        .then(userInfo => this.$router.push({ name: 'posts' }))
        .catch(err => {
          switch (err) {
            case ('NEW_PASSWORD_REQUIRED'):
              this.$emit('newPassword', { cogUser: this.cogUser, email: this.form.email, password: this.form.password })
              break
            default:
              console.log('sign-in error:', err)
              this.$emit('error', { code: 400, message: err.message })
              break
          }
        })
    }
  }
}
</script>

<style scoped>

</style>
