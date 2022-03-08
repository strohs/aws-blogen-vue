// Form for capturing details of a user: firstname, lastname, email, username, password
// Form info is validated and then emitted in an object to the parent component
<template>
  <b-form>
    <b-form-group
id="firstNameGroup2" label="First Name" label-for="firstName2"
                  :state="firstNameValidator.state" :invalid-feedback="firstNameValidator.invalidFeedback"
                  :valid-feedback="firstNameValidator.validFeedback">
      <b-form-input
id="firstName2" v-model.trim="user.firstName" type="text" placeholder="First Name"
                    :state="firstNameValidator.state" required @input="validateFirstName"></b-form-input>
    </b-form-group>

    <b-form-group
id="lastNameGroup2" label="Last Name" label-for="lastName2"
                  :state="lastNameValidator.state" :invalid-feedback="lastNameValidator.invalidFeedback"
                  :valid-feedback="lastNameValidator.validFeedback">
      <b-form-input
id="lastName2" v-model="user.lastName" type="text" placeholder="Last Name"
                    :state="lastNameValidator.state" required @input="validateLastName">
      </b-form-input>
    </b-form-group>

    <b-form-group
id="emailGroup2" label="Email" label-for="emailName2"
                  :state="emailValidator.state" :invalid-feedback="emailValidator.invalidFeedback"
                  :valid-feedback="emailValidator.validFeedback">
      <b-form-input
id="emailName2" v-model="user.email" type="email" placeholder="Email"
                    :state="emailValidator.state" required @input="validateEmail"></b-form-input>
    </b-form-group>

    <b-form-group
id="userNameGroup2" label="User Name" label-for="userName2"
                  :state="userNameValidator.state" :invalid-feedback="userNameValidator.invalidFeedback"
                  :valid-feedback="userNameValidator.validFeedback">
      <b-form-input
id="userName2" v-model="user.userName" type="text" placeholder="username"
                    :state="userNameValidator.state" required @input="validateUserName"></b-form-input>
    </b-form-group>

    <b-form-group
id="passwordGroup2" label="Password" label-for="password2"
                  :state="passwordValidator.state" :invalid-feedback="passwordValidator.invalidFeedback"
                  :valid-feedback="passwordValidator.validFeedback">
      <b-form-input
id="password2" v-model="user.password" type="password" placeholder=""
                    :state="passwordValidator.state" required @input="validatePassword">
      </b-form-input>
    </b-form-group>

    <b-button :to="{ name: 'homepage'}" type="button" variant="secondary">Cancel</b-button>
    <b-button  :disabled="!allFieldsValid" type="button" variant="primary" @click="doSubmit">Submit</b-button>

  </b-form>
</template>

<script>
import textLengthValidator from '../../../src/validators/textLengthValidator'
import emailValidator from '../../../src/validators/emailValidator'
import userNameExistsValidator from '../../../src/validators/userNameExistsValidator'

export default {
  name: 'SignupForm',
  props: {
    firstName: '',
    lastName: '',
    userName: '',
    email: '',
    password: ''
  },
  data () {
    return {
      user: {
        firstName: this.firstName,
        lastName: this.lastName,
        email: this.email,
        userName: this.userName,
        password: this.password
      },
      formError: false,
      userNameValidator: { state: null, invalidFeedback: '', validFeedback: '' },
      firstNameValidator: { state: null, invalidFeedback: '', validFeedback: '' },
      lastNameValidator: { state: null, invalidFeedback: '', validFeedback: '' },
      passwordValidator: { state: null, invalidFeedback: '', validFeedback: '' },
      emailValidator: { state: null, invalidFeedback: '', validFeedback: '' }
    }
  },
  computed: {
    allFieldsValid () {
      return (this.userNameValidator.state && this.firstNameValidator.state &&
          this.lastNameValidator.state && this.emailValidator.state && this.passwordValidator.state)
    }
  },
  methods: {
    doSubmit () {
      this.$emit('submit', this.user)
    },
    validateUserName (val) {
      this.userNameValidator = textLengthValidator(val, 2)
      if (this.userNameValidator.state) {
        userNameExistsValidator(val)
          .then(validator => {
            this.userNameValidator = validator
          })
          .catch(validator => {
            this.userNameValidator = validator
          })
      }
    },
    validateFirstName (val) {
      this.firstNameValidator = textLengthValidator(val, 1)
    },
    validateLastName (val) {
      this.lastNameValidator = textLengthValidator(val, 1)
    },
    validatePassword (val) {
      this.passwordValidator = textLengthValidator(val, 8)
    },
    validateEmail (val) {
      this.emailValidator = emailValidator(val)
    }
  }
}
</script>

<style scoped>

</style>
