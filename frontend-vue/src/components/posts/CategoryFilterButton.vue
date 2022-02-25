// CategoryFilterButton
//  This is a Vue.js custom input component. Its a dropdown button that lists all the categories currently
//  in the blogen database.
//  The selected value is emitted as the value of the 'input' event.
//
<template>
  <b-dropdown id="categoryFilterButton" text="Filter By Category" variant="success">
    <b-dropdown-item v-for="cat in categories" :key="cat.name" :value="value" @click="$emit('input', cat)">
      {{cat.name}}
    </b-dropdown-item>
  </b-dropdown>
</template>

<script>
export default {
  name: 'CategoryFilterButton',
  props: {
    value: {
      type: String,
      default: '',
    }
  },
  emits: ['input'],
  computed: {
    categories () {
      let cats = [{ name: 'All', categoryUrl: '' }]
      this.$store.state.blogenRestApi.categories.forEach(cat => cats.push(cat))
      return cats
    }
  }
}
</script>

<style scoped>

</style>
