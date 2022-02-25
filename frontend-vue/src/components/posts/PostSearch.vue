// PostSearch
// This is a vue.js custom component that contains an input field and button.
// The input field will capture the string to search for, the button, when clicked, will submit the contents of
// the input field.
//
// Emits
// input (search string) - will contain the string being searched for
//
<template>

  <b-input-group id="postSearchGroup">
    <b-form-input type="text" v-model="searchStr" placeholder="Search Posts"></b-form-input>
    <b-input-group-append>
      <b-button variant="primary" type="submit" @click="emitSearchStr">Search</b-button>
    </b-input-group-append>
  </b-input-group>

</template>

<script>
export default {
  name: 'PostSearch',
  props: {
    value: {
      type: String,
      default: '',
    }
  },
  emits: ['input'],
  data () {
    return {
      searchStr: ''
    }
  },
  methods: {
    emitSearchStr () {
      console.log('post search val:', this.searchStr)
      this.$emit('input', this.searchStr)
    }
  }
}
</script>

<style scoped>

</style>
