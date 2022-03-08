// UserProfileForm
// Form for capturing a user's profile: firstname, lastname, email, username, password
// The form is validated and then emitted in an object to the parent component
<template>
  <b-form @submit.prevent="emitFormData(v$)">

    <b-form-group id="firstNameGroup1" label="First Name" label-for="firstName1"
                  :state="!v$.firstName.$error"
                  :valid-feedback="validMessage"
                  :invalid-feedback="invalidMessage(v$.firstName)">

      <b-form-input id="firstName1"
                    type="text"
                    v-model="v$.firstName.$model"
      >
      </b-form-input>
    </b-form-group>

    <b-form-group id="lastNameGroup1" label="Last Name" label-for="lastName1"
                  :state="!v$.lastName.$error"
                  :valid-feedback="validMessage"
                  :invalid-feedback="invalidMessage(v$.lastName)">

      <b-form-input id="lastName1"
                    type="text"
                    v-model="v$.lastName.$model"
      ></b-form-input>
    </b-form-group>

    <b-form-group id="emailGroup1" label="Email" label-for="email1"
                  :state="!v$.email.$error"
                  :valid-feedback="validMessage"
                  :invalid-feedback="invalidMessage(v$.email)">
      <b-form-input id="email1"
                    type="email"
                    v-model="v$.email.$model"
      ></b-form-input>
    </b-form-group>

    <b-form-group id="prefUsernameGroup1" label="Preferred Username" label-for="prefUsername1"
                  :state="!v$.userName.$error"
                  :valid-feedback="validMessage"
                  :invalid-feedback="invalidMessage(v$.userName)">

      <b-form-input id="prefUsername1"
                    type="text"
                    v-model="v$.userName.$model"
      ></b-form-input>
    </b-form-group>

    <avatar-chooser v-model="v$.avatarImage.$model"></avatar-chooser>

    <b-button :to="{ name: 'posts'}" type="button" variant="secondary">Cancel</b-button>
    <b-button type="submit" variant="primary">Submit</b-button>

  </b-form>
</template>

<script setup>
import { reactive, computed } from 'vue';
import useVuelidate from '@vuelidate/core'
import {required, email, minLength, alphaNum, helpers} from '@vuelidate/validators'
import {prefUsernameIsUnique, emailIsUnique} from '../../common/awsCognito'
import AvatarChooser from './AvatarChooser.vue'

const props = defineProps({
  firstName: {
    type: String,
    default: '',
  },
  lastName: {
    type: String,
    default: '',
  },
  userName: {
    type: String,
    default: '',
  },
  email: {
    type: String,
    default: '',
  },
  avatarImage: {
    type: String,
    default: '',
  }
});

const emit = defineEmits(['validated']);

// reactive state
const form = reactive({
  firstName: props.firstName,
  lastName: props.lastName,
  email: props.email,
  userName: props.userName,
  avatarImage: props.avatarImage,
});

const {withAsync} = helpers;

function emailUnique(value) {
  if (value === props.email) {
    return true;
  } else {
    return emailIsUnique(value);
  }
}

function userNameUnique(value) {
  if (value === props.userName) {
    return true;
  } else {
    return prefUsernameIsUnique(value);
  }
}

// vuelidate validation rules
const rules = {
  email: {
    required,
    email,
    emailUnique: withAsync(helpers.withMessage('email is taken', emailUnique), props),
  },
  userName: {
    required,
    minLength: minLength(3),
    alphaNum,
    userNameUnique: withAsync(helpers.withMessage('username is taken', userNameUnique), props),
  },
  firstName: {
    required,
    alphaNum
  },
  lastName: {
    required,
    alphaNum
  },
  avatarImage: {}
};

const v$ = useVuelidate(rules, form);

// the validation message that is displayed for a form field that passed validation
const validMessage = computed(() => 'Good');

// the message that displays when any form field is invalid. This will simply
//  concatentate all errors on the field with a comma
function invalidMessage(vuelidateFormField) {
  return vuelidateFormField.$errors.map(error => error.$message).join(', ');
}

async function emitFormData(v$) {
  const isFormValid = await v$.$validate();
  if (isFormValid) {
    console.log('user profile form is valid', form);
    emit('validated', form);
  }
}

</script>

<style scoped>

</style>
