// Form for changing a user's password
// Form info is validated and then emitted in an object to the parent component
// Input Props: none
// Emitted Events and Values:
//   'validated', {curPassword,newPassword}  - this event will be emitted when the form fields are valid, the event
//      data will contain the curPassword and newPassword input values
//   'cancel' - if the user cancels the form
//
<template>
  <b-form>

    <b-form-group id="curPasswordGroup1" label="Current Password" label-for="curPassword1"
                  :state="!v$.form.curPassword.$invalid"
                  :valid-feedback="curPasswordValidText"
                  :invalid-feedback="curPasswordInvalidText">

      <b-form-input id="curPassword1"
                    type="password"
                    v-model="form.curPassword"
                    @input="v$.form.curPassword.$touch()"></b-form-input>
    </b-form-group>

    <b-form-group id="newPasswordGroup1" label="New Password" label-for="newPassword1"
                  :state="!v$.form.newPassword.$invalid"
                  :valid-feedback="newPasswordValidText"
                  :invalid-feedback="newPasswordInvalidText">

      <b-form-input id="newPassword1"
                    type="password"
                    v-model="form.newPassword"
                    @input="v$.form.newPassword.$touch()"></b-form-input>
    </b-form-group>

    <b-form-group id="confPasswordGroup1" label="Confirm New Password" label-for="confPassword1"
                  :state="!v$.form.confPassword.$invalid"
                  :valid-feedback="confPasswordValidText"
                  :invalid-feedback="confPasswordInvalidText">

      <b-form-input id="confPassword1"
                    type="password"
                    v-model="form.confPassword"
                    @input="v$.form.confPassword.$touch()"></b-form-input>
    </b-form-group>

    <b-button block size="sm" type="submit" variant="secondary" @click="emitCancel">Cancel</b-button>
    <b-button block :disabled="v$.form.$invalid" size="sm" type="submit" variant="primary" @click="emitFormData">Submit</b-button>

  </b-form>
</template>

<script>
import useVuelidate from '@vuelidate/core'
import { required, minLength, sameAs } from '@vuelidate/validators'
import * as vt from '../../validators/validationText'

export default {
  name: 'ChangePasswordForm',
  emits: ['validated', 'cancel'],
  setup () {
    return { v$: useVuelidate() }
  },
  data () {
    return {
      form: {
        curPassword: '',
        newPassword: '',
        confPassword: ''
      }
    }
  },
  validations() {
    return {
      form: {
        curPassword: {
          required,
          minLength: minLength(8),
          hasUpperCase: val => { return val.search(/[A-Z]/) >= 0 }
        },
        newPassword: {
          required,
          minLength: minLength(8),
          hasUpperCase: val => { return val.search(/[A-Z]/) >= 0 }
        },
        confPassword: {
          required,
          sameAsNewPassword: sameAs(this.newPassword)
        }
      }
    }
  },
  computed: {
    curPasswordValidText () { return vt.passwordValidText(this.v$.form.curPassword) },
    curPasswordInvalidText () { return vt.passwordInvalidText(this.v$.form.curPassword) },
    newPasswordValidText () { return vt.passwordValidText(this.v$.form.newPassword) },
    newPasswordInvalidText () { return vt.passwordInvalidText(this.v$.form.newPassword) },
    confPasswordValidText (confPassword) {
      return !confPassword.$error ? 'Ok' : ''
    },
    confPasswordInvalidText (confPassword) {
      if (confPassword.$dirty) {
        if (confPassword.$invalid) {
          return 'passwords must match'
        }
      } else {
        return ''
      }
    }
  },
  methods: {
    emitFormData (evt) {
      evt.preventDefault()
      this.$emit('validated', { curPassword: this.form.curPassword, newPassword: this.form.newPassword })
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
