// captures sign-up information for a new user. If all form fields are valid,
then this component will initiate // the sign-up process with cognito // //
Events: // 'success', { formData } - emitted if the user was signed-up
successfully with cognito // 'cancel' - emitted if the user clicked the forms
cancel button // 'error',{ code, message } - emitted if an error occured signing
up with Cognito
<template>
  <b-card border-variant="primary" no-body>
    <b-card-header>
      <div class="row">
        <h4>
          <font-awesome-icon
            class="mx-2"
            icon="user-circle"
            scale="2"
          ></font-awesome-icon>
          Sign-Up for Blogen
        </h4>
      </div>
    </b-card-header>

    <b-card-body>
      <b-form>
        <b-form-group
          id="emailGroup1"
          label="Email"
          label-for="email1"
          :state="$v.form.email.email && !$v.form.email.$invalid"
          :valid-feedback="emailValidText"
          :invalid-feedback="emailInvalidText"
        >
          <b-form-input
            id="email1"
            v-model="form.email"
            type="email"
            placeholder="Email"
            @input="$v.form.email.$touch()"
          ></b-form-input>
        </b-form-group>

        <b-form-group
          id="passwordGroup1"
          label="Password"
          label-for="password1"
          :state="!$v.form.password.$invalid"
          :valid-feedback="passwordValidText"
          :invalid-feedback="passwordInvalidText"
        >
          <b-form-input
            id="password1"
            v-model="form.password"
            type="password"
            @input="$v.form.password.$touch()"
          ></b-form-input>
        </b-form-group>

        <b-form-group
          id="confPasswordGroup1"
          label="Confirm Password"
          label-for="confPassword1"
          :state="!$v.form.confPassword.$invalid"
          :valid-feedback="confPasswordValidText"
          :invalid-feedback="confPasswordInvalidText"
        >
          <b-form-input
            id="confPassword1"
            v-model="form.confPassword"
            type="password"
            @input="$v.form.confPassword.$touch()"
          ></b-form-input>
        </b-form-group>

        <b-form-group
          id="prefUsernameGroup1"
          label="Preferred Username"
          label-for="prefUsername1"
          :state="
            $v.form.preferredUsername.$dirty &&
            !$v.form.preferredUsername.$invalid
          "
          :valid-feedback="preferredUsernameValidText"
          :invalid-feedback="preferredUsernameInvalidText"
        >
          <b-form-input
            id="prefUsername1"
            v-model="form.preferredUsername"
            type="text"
            @input="validatePrefUsername"
          ></b-form-input>
        </b-form-group>

        <b-form-group
          id="firstNameGroup1"
          label="First Name"
          label-for="firstName1"
          :state="$v.form.firstName.$dirty && !$v.form.firstName.$invalid"
          :valid-feedback="firstNameValidText"
          :invalid-feedback="firstNameInvalidText"
        >
          <b-form-input
            id="firstName1"
            v-model="form.firstName"
            type="text"
            @input="$v.form.firstName.$touch()"
          ></b-form-input>
        </b-form-group>

        <b-form-group
          id="lastNameGroup1"
          label="Last Name"
          label-for="lastName1"
          :state="$v.form.lastName.$dirty && !$v.form.lastName.$invalid"
          :valid-feedback="lastNameValidText"
          :invalid-feedback="lastNameInvalidText"
        >
          <b-form-input
            id="lastName1"
            v-model="form.lastName"
            type="text"
            @input="$v.form.lastName.$touch()"
          ></b-form-input>
        </b-form-group>

        <b-button
          block
          :disabled="$v.form.$invalid"
          size="lg"
          type="submit"
          variant="primary"
          @click.stop.prevent="doSignUp"
        >
          Sign-UP!
        </b-button>
        <b-button block size="lg" type="submit" variant="danger" @click="cancel"
          >Cancel</b-button
        >
      </b-form>
    </b-card-body>
  </b-card>
</template>

<script>
import { validationMixin } from "vuelidate";
import {
  required,
  email,
  minLength,
  alphaNum,
  sameAs,
} from "vuelidate/lib/validators";
import * as vt from "@/validators/validationText";
import {
  prefUsernameIsUnique,
  emailIsUnique,
} from "../../src/common/awsCognito";
import _ from "lodash";

export default {
  name: "SignUp",
  mixins: [validationMixin],
  props: {
    email: String,
    password: String,
    preferredUsername: String,
    firstName: String,
    lastName: String,
  },
  data() {
    return {
      form: {
        email: this.email,
        password: this.password,
        preferredUsername: this.preferredUsername,
        firstName: this.firstName,
        lastName: this.lastName,
      },
    };
  },
  validations: {
    form: {
      email: {
        required,
        email,
        async isUnique(value) {
          // standalone validator ideally should not assume a field is required
          if (value === "") return true;
          const taken = await emailIsUnique(value);
          return !taken;
        },
      },
      password: {
        required,
        minLength: minLength(8),
        hasUpperCase: (val) => {
          return val.search(/[A-Z]/) >= 0;
        },
      },
      confPassword: {
        required,
        sameAsPassword: sameAs("password"),
      },
      preferredUsername: {
        required,
        minLength: minLength(3),
        alphaNum,
        async isUnique(value) {
          // standalone validator ideally should not assume a field is required
          if (value === "") return true;
          const taken = await prefUsernameIsUnique(value);
          return !taken;
        },
      },
      firstName: {
        required,
      },
      lastName: {
        required,
      },
    },
  },
  computed: {
    emailValidText() {
      return vt.emailValidText(this.$v.form.email);
    },
    emailInvalidText() {
      return vt.emailInvalidText(this.$v.form.email);
    },
    passwordValidText() {
      return vt.passwordValidText(this.$v.form.password);
    },
    passwordInvalidText() {
      return vt.passwordInvalidText(this.$v.form.password);
    },
    confPasswordValidText() {
      return !this.$v.form.confPassword.$error ? "Ok" : "";
    },
    confPasswordInvalidText() {
      if (this.$v.form.confPassword.$dirty) {
        if (!this.$v.form.confPassword.sameAsPassword) {
          return "passwords must match";
        }
      } else {
        return "";
      }
    },
    preferredUsernameValidText() {
      return vt.preferredUsernameValidText(this.$v.form.preferredUsername);
    },
    preferredUsernameInvalidText() {
      return vt.preferredUsernameInvalidText(this.$v.form.preferredUsername);
    },
    firstNameValidText() {
      return vt.firstNameValidText(this.$v.form.firstName);
    },
    firstNameInvalidText() {
      return vt.firstNameInvalidText(this.$v.form.firstName);
    },
    lastNameValidText() {
      return vt.lastNameValidText(this.$v.form.lastName);
    },
    lastNameInvalidText() {
      return vt.lastNameInvalidText(this.$v.form.lastName);
    },
  },
  created() {
    // create a debounced function that validates the preferred username field, need to limit the amount of calls to AWS
    this.validatePrefUsername = _.debounce(() => {
      this.$v.form.preferredUsername.$touch();
    }, 1000);
  },
  methods: {
    cancel(evt) {
      evt.preventDefault();
      this.$emit("cancel");
    },
    doSignUp() {
      console.log("do SIGN_UP", this.form);
      this.$store
        .dispatch("doSignUp", this.form)
        .then((data) => {
          // for future reference, data could contain a CHALLENGE, but it shouldn't in our case
          return this.$emit("success", this.form);
        })
        .catch((err) => {
          console.log("sign-up error:", err);
          this.$emit("error", { code: 400, message: err.message });
        });
    },
  },
};
</script>

<style scoped></style>
