// Form for capturing Cognito Challenge information. Specifically a new password plus any other user information that
// wasn't captured when a user signed up
// Form info is validated and then emitted in an object to the parent component
// Input Props: none
// Emitted Events and Values:
//   'validated', newPassword  - this event will be emitted when the form fields are valid and the
//   'cancel' - if the user cancels the form
// submit button of the form is clicked. This event will contain the users new password
<template>
  <b-form>

    <b-form-group
id="passwordGroup1" label="New Password" label-for="password1"
                  :state="!$v.form.password.$invalid"
                  :valid-feedback="passwordValidText"
                  :invalid-feedback="passwordInvalidText">

      <b-form-input
id="password1"
                    v-model="form.password"
                    type="password"
                    @input="$v.form.password.$touch()"></b-form-input>
    </b-form-group>

    <b-form-group
id="confPasswordGroup1" label="Confirm Password" label-for="confPassword1"
                  :state="!$v.form.confPassword.$invalid"
                  :valid-feedback="confPasswordValidText"
                  :invalid-feedback="confPasswordInvalidText">

      <b-form-input
id="confPassword1"
                    v-model="form.confPassword"
                    type="password"
                    @input="$v.form.confPassword.$touch()"></b-form-input>
    </b-form-group>

    <b-form-group
id="prefUsernameGroup1" label="Nickname" label-for="prefUsername1"
                  :state="$v.form.preferredUsername.$dirty && !$v.form.preferredUsername.$invalid"
                  :valid-feedback="preferredUsernameValidText"
                  :invalid-feedback="preferredUsernameInvalidText">

      <b-form-input
id="prefUsername1"
                    v-model="form.preferredUsername"
                    type="text"
                    placeholder="nickname other users will know you by"
                    @input="validatePrefUsername"></b-form-input>
    </b-form-group>

    <b-form-group
id="firstNameGroup1" label="First Name" label-for="firstName1"
                  :state="$v.form.firstName.$dirty && !$v.form.firstName.$invalid"
                  :valid-feedback="firstNameValidText"
                  :invalid-feedback="firstNameInvalidText">

      <b-form-input
id="firstName1"
                    v-model="form.firstName"
                    type="text"
                    @input="$v.form.firstName.$touch()"></b-form-input>
    </b-form-group>

    <b-form-group
id="lastNameGroup1" label="Last Name" label-for="lastName1"
                  :state="$v.form.lastName.$dirty && !$v.form.lastName.$invalid"
                  :valid-feedback="lastNameValidText"
                  :invalid-feedback="lastNameInvalidText">

      <b-form-input
id="lastName1"
                    v-model="form.lastName"
                    type="text"
                    @input="$v.form.lastName.$touch()"></b-form-input>
    </b-form-group>

    <b-button block :disabled="$v.form.$invalid" size="lg" type="submit" variant="primary" @click="emitFormData">Submit</b-button>
    <b-button block size="lg" type="submit" variant="danger" @click="emitCancel">Cancel</b-button>

  </b-form>
</template>

<script>
import { validationMixin } from 'vuelidate'
import { required, minLength, sameAs, alphaNum } from 'vuelidate/lib/validators'
import { prefUsernameIsUnique } from '../../src/common/awsCognito'
import * as vt from '@/validators/validationText'
import _ from 'lodash'

export default {
  name: 'ChallengeForm',
  mixins: [validationMixin],
  props: {
    // this is the user object returned by the Cognito sign-in attempt
    userData: Object
  },
  data () {
    return {
      form: {
        password: null,
        confPassword: null,
        preferredUsername: null,
        firstName: null,
        lastName: null
      }
    }
  },
  validations: {
    form: {
      password: {
        required,
        minLength: minLength(8),
        hasUpperCase: val => { return val.search(/[A-Z]/) >= 0 }
      },
      confPassword: {
        required,
        sameAsPassword: sameAs('password')
      },
      preferredUsername: {
        required,
        minLength: minLength(3),
        alphaNum,
        async isUnique (value) {
          // standalone validator ideally should not assume a field is required
          if (value === '') return true
          const taken = await prefUsernameIsUnique(value)
          return !taken
        }
      },
      firstName: {
        required
      },
      lastName: {
        required
      }
    }
  },
  computed: {
    passwordValidText () { return vt.passwordValidText(this.$v.form.password) },
    passwordInvalidText () { return vt.passwordInvalidText(this.$v.form.password) },
    confPasswordValidText () {
      return !this.$v.form.confPassword.$error ? 'Ok' : ''
    },
    confPasswordInvalidText () {
      if (this.$v.form.confPassword.$dirty) {
        if (!this.$v.form.confPassword.sameAsPassword) {
          return 'passwords must match'
        }
      } else {
        return ''
      }
    },
    preferredUsernameValidText () { return vt.preferredUsernameValidText(this.$v.form.preferredUsername) },
    preferredUsernameInvalidText () { return vt.preferredUsernameInvalidText(this.$v.form.preferredUsername) },
    firstNameValidText () { return vt.firstNameValidText(this.$v.form.firstName) },
    firstNameInvalidText () { return vt.firstNameInvalidText(this.$v.form.firstName) },
    lastNameValidText () { return vt.lastNameValidText(this.$v.form.lastName) },
    lastNameInvalidText () { return vt.lastNameInvalidText(this.$v.form.lastName) }
  },
  created () {
    // create a debounced function that validates the preferred username field, need to limit the amount of calls to AWS
    this.validatePrefUsername = _.debounce(() => {
      this.$v.form.preferredUsername.$touch()
    }, 1000)
  },
  methods: {
    emitFormData (evt) {
      evt.preventDefault()
      this.$emit('validated', this.form)
    },
    emitCancel (evt) {
      evt.preventDefault()
      this.$emit('cancel')
    }
  }
}
</script>

<style scoped>

</style>
