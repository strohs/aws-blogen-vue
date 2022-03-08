// PostForm
// A form component that validates the fields that make up a post and then emits the validated fields back
// to the parent component in a 'post' object.
//
// Props:
// title - holds the post's title
// text - holds the text of the post
// imageUrl - a url to an image that is associated with the post
// categoryName - the category that this post belongs to
//
// Emits:
// cancelPost - if the form's cancel button was clicked
// submitPost({ title, text, imageUrl, categoryName }) - when the form's submit button is clicked, this event will
//                                                      contain an object with the validated post properties
<template>
  <b-form>
    <b-form-group
id="titleGroup" label="Title" label-for="title"
                  :state="titleValidator.state" :invalid-feedback="titleValidator.invalidFeedback"
                  :valid-feedback="titleValidator.validFeedback">
      <b-form-input
id="title" v-model="post.title" type="text" placeholder="post title"
                    :state="titleValidator.state" required @input="validateTitle"></b-form-input>
    </b-form-group>

    <b-form-group
id="categoryGroup" label="Category" label-for="category"
                  :state="categoryValidator.state" :invalid-feedback="categoryValidator.invalidFeedback"
                  :valid-feedback="categoryValidator.validFeedback">
      <b-form-select
id="category" v-model="post.categoryName" :state="categoryValidator.state" :options="categoryOptions"
                     @input="validateCategory"></b-form-select>
    </b-form-group>

    <b-form-group
id="imageUrlGroup" label="Image URL" label-for="imageUrl"
                  :state="imageUrlValidator.state" :invalid-feedback="imageUrlValidator.invalidFeedback"
                  :valid-feedback="imageUrlValidator.validFeedback">
      <b-form-input
id="imageUrl" v-model="post.imageUrl" type="text"
                    placeholder="image link URL (optional)"
                    :state="imageUrlValidator.state" @input="validateImageUrl"></b-form-input>
      <img
class="image-preview"
           :src="post.imageUrl"
           width="100" height="100"
           alt="image preview"/>
    </b-form-group>

    <b-form-group
id="textGroup" label="Text" label-for="text"
                  :state="textValidator.state" :invalid-feedback="textValidator.invalidFeedback"
                  :valid-feedback="textValidator.validFeedback">
      <b-form-textarea
id="text" v-model="post.text" type="text" placeholder="the text of your post" rows="3"
                       :state="textValidator.state" required @input="validateText"></b-form-textarea>
    </b-form-group>
    <b-button type="button" variant="secondary" @click="$emit('cancelPost')">Cancel</b-button>
    <b-button type="button" variant="primary" :disabled="!allModalFieldsValid" @mouseover="validateAllFields" @click="$emit('submitPost',post)">Submit</b-button>
  </b-form>

</template>

<script>
import textLengthValidator from '../../validators/textLengthValidator'
import selectValidator from '../../validators/selectValidator'

export default {
  name: 'PostForm',
  props: {
    title: {
      type: String,
      default: '',
    },
    text: {
      type: String,
      default: '',
    },
    imageUrl: {
      type: String,
      default: '',
    },
    categoryName: {
      type: String,
      default: '',
    },
  },
  emits: ['cancelPost', 'submitPost'],
  data () {
    return {
      // make a copy of the props
      post: {
        title: this.title,
        text: this.text,
        imageUrl: this.imageUrl,
        categoryName: this.categoryName
      },
      fieldsValid: false,
      titleValidator: { state: null, invalidFeedback: '', validFeedback: '' },
      textValidator: { state: null, invalidFeedback: '', validFeedback: '' },
      categoryValidator: { state: null, invalidFeedback: '', validFeedback: '' },
      imageUrlValidator: { state: true, invalidFeedback: '', validFeedback: '' }
    }
  },
  computed: {
    allModalFieldsValid () {
      return (this.titleValidator.state && this.categoryValidator.state && this.imageUrlValidator.state && this.textValidator.state)
    },
    categoryOptions () {
      let options = [{ value: null, text: 'Please Select a Category' }]
      this.$store.getters.getCategories.forEach(cat => options.push({ value: cat.name, text: cat.name }))
      return options
    }
  },
  created () {
    // pre-validate any filled in form fields
    // this.validateTitle(this.title)
    // this.validateText(this.text)
    // this.validateImageUrl(this.imageUrl)
    // this.validateCategory(this.categoryName)
  },
  methods: {
    validateAllFields () {
      this.validateTitle(this.post.title)
      this.validateText(this.post.text)
      this.validateCategory(this.post.categoryName)
      this.validateImageUrl(this.post.imageUrl)
    },
    validateTitle (val) {
      this.titleValidator = textLengthValidator(val, 1)
    },
    validateText (val) {
      this.textValidator = textLengthValidator(val, 1)
    },
    validateImageUrl () {
      // TODO possibly implement an image link validator
      // this.imageUrlValidator = textLengthValidator(val, 0)
    },
    validateCategory (val) {
      this.categoryValidator = selectValidator(val)
    }
  }
}
</script>

<style scoped>

</style>
