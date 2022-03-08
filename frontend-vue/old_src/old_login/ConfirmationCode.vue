// component for entering the confirmation code
//
// Props:
//  email - the email address of the user that must enter the confirmation code
// emits:
//  'success' - if the confirmation code was successfully validated by Cognito
//  'error',{ code, message } - if some error occured with Cognito
<template>
    <b-card no-body>
        <b-card-header header-text-variant="dark" header-bg-variant="light">
            <div class="row">
                <p>
                    <font-awesome-icon class="mx-2" icon="user-circle" scale="2"></font-awesome-icon>
                    Enter the sign-up confirmation code emailed to: <i>{{ email }}</i>
                </p>
            </div>
        </b-card-header>
        <b-card-body>
            <b-form>
                <b-form-group
id="confCodeGroup1" label="Confirmation Code" label-for="confCode1"
                              :state="!$v.form.confCode.$invalid"
                              :valid-feedback="confCodeValidText"
                              :invalid-feedback="confCodeInvalidText">
                    <b-form-input
id="confCode1"
                                  v-model="form.confCode"
                                  type="text"
                                  placeholder="confirmation code"
                                  @input="$v.form.confCode.$touch()"></b-form-input>
                </b-form-group>

                <b-button
block
                          :disabled="$v.form.$invalid"
                          size="lg" type="submit" variant="primary"
                          @click.stop.prevent="validateConfirmationCode"
                >
                    Submit
                </b-button>

            </b-form>
        </b-card-body>
    </b-card>
</template>

<script>
import { validationMixin } from 'vuelidate'
import { required } from 'vuelidate/lib/validators'
import * as vt from '@/validators/validationText'

export default {
  name: 'ConfirmationCode',
  mixins: [validationMixin],
  props: {
    email: ''
  },
  data () {
    return {
      form: {
        confCode: ''
      }
    }
  },
  validations: {
    form: {
      confCode: {
        required
      }
    }
  },
  computed: {
    confCodeValidText () { return vt.confCodeValidText(this.$v.form.confCode) },
    confCodeInvalidText () { return vt.confCodeInvalidText(this.$v.form.confCode) }
  },
  methods: {
    validateConfirmationCode () {
      console.log('validating confirmation code: ', this.email, this.form.confCode)
      this.$store.dispatch('doConfirmSignUp', { email: this.email, confCode: this.form.confCode })
        .then(data => {
          // cognito returns SUCCESS if the confirmation code was accepted
          if (data === 'SUCCESS') {
            this.$emit('success')
          } else {
            // unknown response from cognito
            return Promise.reject(new Error('unknown response from cognito ' + data))
          }
        })
        .catch(err => {
          this.$emit('error', { code: 400, message: err.message })
        })
    }
  }
}
</script>

<style scoped>

</style>
