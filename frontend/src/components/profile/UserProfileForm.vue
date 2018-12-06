// Form for capturing details of a user: firstname, lastname, email, username, password
// Form info is validated and then emitted in an object to the parent component
<template>
  <b-form>

    <b-form-group id="firstNameGroup1" label="First Name" label-for="firstName1"
                  :state="$v.form.firstName.$dirty && !$v.form.firstName.$invalid"
                  :valid-feedback="firstNameValidText"
                  :invalid-feedback="firstNameInvalidText">

      <b-form-input id="firstName1"
                    type="text"
                    v-model="form.firstName"
                    @input="$v.form.firstName.$touch()"></b-form-input>
    </b-form-group>

    <b-form-group id="lastNameGroup1" label="Last Name" label-for="lastName1"
                  :state="$v.form.lastName.$dirty && !$v.form.lastName.$invalid"
                  :valid-feedback="lastNameValidText"
                  :invalid-feedback="lastNameInvalidText">

      <b-form-input id="lastName1"
                    type="text"
                    v-model="form.lastName"
                    @input="$v.form.lastName.$touch()"></b-form-input>
    </b-form-group>

    <b-form-group id="emailGroup1" label="Email" label-for="email1"
                  :state="$v.form.email.email && !$v.form.email.$invalid"
                  :valid-feedback="emailValidText"
                  :invalid-feedback="emailInvalidText">
      <b-form-input id="email1"
                    type="email"
                    v-model="form.email"
                    placeholder="Email"
                    @change="$v.form.email.$touch()"></b-form-input>
    </b-form-group>

    <b-form-group id="prefUsernameGroup1" label="Preferred Username" label-for="prefUsername1"
                  :state="$v.form.userName.$dirty && !$v.form.userName.$invalid"
                  :valid-feedback="preferredUsernameValidText"
                  :invalid-feedback="preferredUsernameInvalidText">

      <b-form-input id="prefUsername1"
                    type="text"
                    v-model="form.userName"
                    @change="validateUsername"></b-form-input>
    </b-form-group>

    <app-avatar-chooser v-model="form.avatarImage"></app-avatar-chooser>

    <b-button :to="{ name: 'posts'}" type="button" variant="secondary">Cancel</b-button>
    <b-button :disabled="$v.form.$invalid" type="submit" variant="primary" @click="emitFormData">Submit</b-button>

  </b-form>
</template>

<script>
import { validationMixin } from 'vuelidate'
import { required, email, minLength, alphaNum } from 'vuelidate/lib/validators'
import * as vt from '@/validators/validationText'
import { prefUsernameTaken, emailTaken } from '../../common/awsCognito'
import _ from 'lodash'
import AvatarChooser from './AvatarChooser'

export default {
  name: 'UserProfileForm',
  mixins: [validationMixin],
  components: {
    appAvatarChooser: AvatarChooser
  },
  props: {
    firstName: '',
    lastName: '',
    userName: '',
    email: '',
    avatarImage: ''
  },
  data () {
    return {
      form: {
        firstName: this.firstName,
        lastName: this.lastName,
        email: this.email,
        userName: this.userName,
        avatarImage: this.avatarImage
      }
    }
  },
  validations: {
    form: {
      email: {
        required,
        email,
        async isUnique (value) {
          // standalone validator ideally should not assume a field is required
          if (value === '' || value === this.email) return true
          const taken = await emailTaken(value)
          return !taken
        }
      },
      userName: {
        required,
        minLength: minLength(3),
        alphaNum,
        async isUnique (value) {
          // standalone validator ideally should not assume a field is required
          if (value === '' || value === this.userName) return true
          const taken = await prefUsernameTaken(value)
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
    emailValidText () { return vt.emailValidText(this.$v.form.email) },
    emailInvalidText () { return vt.emailInvalidText(this.$v.form.email) },
    preferredUsernameValidText () {
      return !this.$v.form.userName.$error ? 'Good' : ''
    },
    preferredUsernameInvalidText () {
      if (this.$v.form.userName.$dirty) {
        if (!this.$v.form.userName.minLength) {
          return `preferred username must have at least ${this.$v.form.userName.$params.minLength.min} characters`
        } else if (!this.$v.form.userName.alphaNum) {
          return `preferred username must contain only alpha-numeric characters`
        } else if (!this.$v.form.userName.required) {
          return 'preferred username is required'
        } else if (!this.$v.form.userName.isUnique) {
          return 'preferred username is already taken'
        }
      } else {
        return ''
      }
    },
    firstNameValidText () { return vt.firstNameValidText(this.$v.form.firstName) },
    firstNameInvalidText () { return vt.firstNameInvalidText(this.$v.form.firstName) },
    lastNameValidText () { return vt.lastNameValidText(this.$v.form.lastName) },
    lastNameInvalidText () { return vt.lastNameInvalidText(this.$v.form.lastName) }
  },
  methods: {
    emitFormData (evt) {
      evt.preventDefault()
      const evtData = Object.assign({}, this.form)
      this.$emit('validated', evtData)
    }
  },
  created () {
    // create a debounced function that validates the preferred username field, need to limit the amount of calls to AWS
    this.validateUsername = _.debounce(() => {
      this.$v.form.userName.$touch()
    }, 1000)
  }
}
</script>

<style scoped>

</style>
