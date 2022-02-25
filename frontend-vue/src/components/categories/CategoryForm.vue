// A form component that validates the fields that make up a category and then emits the validated fields back
// to the parent component
//
<template>
  <!-- v-on:submit.prevent added to prevent form from erroneously submitting data when enter key is pressed -->
  <b-form v-on:submit.prevent>
    <b-form-group id="categoryNameGroup3" label="Category Name" label-for="categoryName3"
                  :state="nameValidator.state" :invalid-feedback="nameValidator.invalidFeedback"
                  :valid-feedback="nameValidator.validFeedback">
      <b-form-input v-model="category.name" id="categoryName3" type="text" placeholder="category name"
                    :state="nameValidator.state" @input="validateName" required></b-form-input>
    </b-form-group>

    <b-button type="button" variant="secondary" @click="$emit('cancel')">Cancel</b-button>
    <b-button type="button" variant="success" @mouseover="validateAllFields" @click="submit" :disabled="!allModalFieldsValid">Submit</b-button>
  </b-form>

</template>

<script>
import textLengthValidator from '../../validators/textLengthValidator'

export default {
  name: 'CategoryForm',
  props: {
    name: {
      type: String,
      default: '',
    }
  },
  emits: ['submit', 'cancel'],
  data () {
    return {
      // make a copy of the props so we don't mutate them
      category: {
        name: this.name,
        oldName: this.name
      },
      fieldsValid: false,
      nameValidator: { state: null, invalidFeedback: '', validFeedback: '' }
    }
  },
  computed: {
    allModalFieldsValid () {
      return (this.nameValidator.state)
    }
  },
  methods: {
    validateAllFields () {
      this.validateName(this.category.name)
    },
    validateName (val) {
      this.nameValidator = textLengthValidator(val, 3)
    },
    submit (evt) {
      evt.preventDefault();
      this.$emit('submit', this.category)
    }
  }
}
</script>

<style scoped>

</style>
