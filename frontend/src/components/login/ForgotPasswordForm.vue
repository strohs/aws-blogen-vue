// Form for capturing forgotten password details, specifically: confCode, tempPassword
// Form info is validated and then emitted in an object to the parent component
// Input Props: none
// Emitted Events and Values:
//   'validated',{ confCode, password } - this event will be emitted when the form fields are valid and the
// submit button of the form is clicked. This event will contain an object with the validated confirmation code and password
<template>
  <b-form>
    <b-form-group id="confCodeGroup1" :label="'Password Reset Code (emailed to: {{ email }})'" label-for="confCode1"
                  :state="!$v.form.confCode.$invalid"
                  :valid-feedback="confCodeValidText"
                  :invalid-feedback="confCodeInvalidText">
      <b-form-input id="confCode1"
                    type="text"
                    v-model="form.confCode"
                    placeholder="confirmation code"
                    @input="$v.form.confCode.$touch()"></b-form-input>
    </b-form-group>

    <b-form-group id="passwordGroup1" label="Password" label-for="password1"
                  :state="!$v.form.password.$invalid"
                  :valid-feedback="passwordValidText"
                  :invalid-feedback="passwordInvalidText">

      <b-form-input id="password1"
                    type="password"
                    v-model="form.password"
                    @input="$v.form.password.$touch()"></b-form-input>
    </b-form-group>

    <b-button block :disabled="$v.form.$invalid" size="lg" type="submit" variant="primary" @click="emitFormData">Submit</b-button>

  </b-form>
</template>

<script>
import { validationMixin } from 'vuelidate'
import { required } from 'vuelidate/lib/validators'
import * as vt from '@/validators/validationText'

export default {
  name: 'ForgotPasswordForm',
  mixins: [validationMixin],
  props: {
    email: String
  },
  data () {
    return {
      form: {
        confCode: '',
        password: ''
      }
    }
  },
  validations: {
    form: {
      confCode: {
        required
      },
      password: {
        required
      }
    }
  },
  computed: {
    confCodeValidText () { return vt.confCodeValidText(this.$v.form.confCode) },
    confCodeInvalidText () { return vt.confCodeInvalidText(this.$v.form.confCode) },
    passwordValidText () { return vt.requiredFieldValidText(this.$v.form.password) },
    passwordInvalidText () { return vt.requiredFieldInvalidText(this.$v.form.password, 'password is required') }
  },
  methods: {
    emitFormData (evt) {
      evt.preventDefault()
      this.$emit('validated', { confCode: this.form.confCode, password: this.form.password })
    }
  }
}
</script>

<style scoped>

</style>
