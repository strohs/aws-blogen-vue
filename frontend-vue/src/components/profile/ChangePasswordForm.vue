// Form for changing a user's password
// Form info is validated and then emitted in an object to the parent component
// Input Props: none
// Emitted Events and Values:
//   'validated', {curPassword,newPassword}  - this event will be emitted when the form fields are valid, the event
//      data will contain the curPassword and newPassword input values
//   'cancel' - if the user cancels the form
//
<template>
  <b-form @submit.prevent="emitFormData(v$)">

    <b-form-group
id="curPasswordGroup1" label="Current Password" label-for="curPassword1"
                  :state="!v$.curPassword.$error"
                  :valid-feedback="validMessage(v$.curPassword)"
                  :invalid-feedback="invalidMessage(v$.curPassword)">

      <b-form-input
id="curPassword1"
                    v-model="v$.curPassword.$model"
                    type="password"
      ></b-form-input>
    </b-form-group>

    <b-form-group
id="newPasswordGroup1" label="New Password" label-for="newPassword1"
                  :state="!v$.newPassword.$error"
                  :valid-feedback="validMessage(v$.newPassword)"
                  :invalid-feedback="invalidMessage(v$.newPassword)">

      <b-form-input
id="newPassword1"
                    v-model="v$.newPassword.$model"
                    type="password"
      ></b-form-input>
    </b-form-group>

    <b-form-group
id="confPasswordGroup1" label="Confirm New Password" label-for="confPassword1"
                  :state="!v$.confPassword.$error"
                  :valid-feedback="validMessage(v$.confPassword)"
                  :invalid-feedback="invalidMessage(v$.confPassword)">

      <b-form-input
id="confPassword1"
                    v-model="v$.confPassword.$model"
                    type="password"
      ></b-form-input>
    </b-form-group>

    <b-button block size="sm" variant="secondary" @click="emitCancel">Cancel</b-button>
    <b-button block size="sm" type="submit" variant="primary">Submit</b-button>

  </b-form>
</template>

<script setup>
import { reactive, computed } from "vue";
import useVuelidate from '@vuelidate/core'
import { required, minLength, sameAs, helpers } from '@vuelidate/validators'

const emit = defineEmits(['validated', 'cancel']);

const form = reactive({
  curPassword: '',
  newPassword: '',
  confPassword: ''
});

const rules = {
  curPassword: {
    minLength: minLength(8),
    hasUpperCase: helpers.withMessage('at least one upper case character', helpers.regex(/[A-Z]/)),
    hasLowerCase: helpers.withMessage('at least one lower case character', helpers.regex(/[a-z]/)),
  },
  newPassword: {
    minLength: minLength(8),
    hasUpperCase: helpers.withMessage('at least one upper case character', helpers.regex(/[A-Z]/)),
    hasLowerCase: helpers.withMessage('at least one lower case character', helpers.regex(/[a-z]/)),
  },
  confPassword: {
    sameAsNewPassword: helpers.withMessage('must match new password', (value) => value === form.newPassword),
  }
};

const v$ = useVuelidate(rules, form);

async function emitFormData(v$) {
  const isFormValid = await v$.$validate();
  if (isFormValid) {
    console.log('change passord form is valid', form);
    emit('validated', { curPassword: form.curPassword, newPassword: form.newPassword })
  }
}

function emitCancel() {
  emit('cancel');
}

// the validation message that is displayed for a form field that passed validation
function validMessage (vuelidateFormField) {
  return vuelidateFormField.$dirty ? 'Good' : ''
}

// the message that displays when any form field is invalid. This will simply
//  concatentate all errors on the field with a comma
function invalidMessage(vuelidateFormField) {
  return vuelidateFormField.$errors.map(error => error.$message).join(', ');
}

// export default {
//   name: 'ChangePasswordForm',
//   emits: ['validated', 'cancel'],
//   setup () {
//     return { v$: useVuelidate() }
//   },
//   data () {
//     return {
//       form: {
//         curPassword: '',
//         newPassword: '',
//         confPassword: ''
//       }
//     }
//   },
//   validations() {
//     return {
//       form: {
//         curPassword: {
//           required,
//           minLength: minLength(8),
//           hasUpperCase: val => { return val.search(/[A-Z]/) >= 0 }
//         },
//         newPassword: {
//           required,
//           minLength: minLength(8),
//           hasUpperCase: val => { return val.search(/[A-Z]/) >= 0 }
//         },
//         confPassword: {
//           required,
//           sameAsNewPassword: sameAs(this.newPassword)
//         }
//       }
//     }
//   },
//   computed: {
//     curPasswordValidText () { return vt.passwordValidText(this.v$.form.curPassword) },
//     curPasswordInvalidText () { return vt.passwordInvalidText(this.v$.form.curPassword) },
//     newPasswordValidText () { return vt.passwordValidText(this.v$.form.newPassword) },
//     newPasswordInvalidText () { return vt.passwordInvalidText(this.v$.form.newPassword) },
//     confPasswordValidText (confPassword) {
//       return !confPassword.$error ? 'Ok' : ''
//     },
//     confPasswordInvalidText (confPassword) {
//       if (confPassword.$dirty) {
//         if (confPassword.$invalid) {
//           return 'passwords must match'
//         }
//       } else {
//         return ''
//       }
//     }
//   },
//   methods: {
//     emitFormData (evt) {
//       evt.preventDefault()
//       this.$emit('validated', { curPassword: this.form.curPassword, newPassword: this.form.newPassword })
//     },
//     emitCancel (evt) {
//       evt.preventDefault()
//       this.$emit('cancel')
//     }
//   }
// }
</script>

<style scoped>

</style>
