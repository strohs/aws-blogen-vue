// CategoryFilterButton
//  This is a custom input component. It's a dropdown button that lists all the categories currently
//  in the blogen database and let's the user select one of them.
//  The selected value is emitted as the value of the 'update:modelValue' event.
//
<template>
  <b-dropdown id="categoryFilterButton" text="Filter By Category" variant="success">
    <b-dropdown-item v-for="cat in categories" :key="cat.name" :value="modelValue" @click="itemClicked(cat)">
      {{cat.name}}
    </b-dropdown-item>
  </b-dropdown>
</template>

<script>
export default {
  name: 'CategoryFilterButton',
  props: {
    modelValue: {
      type: Object,
      default: { name: 'All', categoryUrl: '' },
    }
  },
  emits: ['update:modelValue'],
  computed: {
    // retrieves a list of categories from the store
    categories () {
      let cats = [{ name: 'All', categoryUrl: '' }];
      this.$store.state.blogenRestApi.categories.forEach(cat => cats.push(cat));
      return cats
    }
  },
  created() {
    this.$store.dispatch('fetchAndStoreCategories', { pageNumber: 0, pageLinit: 20 });
  },
  methods: {
    itemClicked(category) {
      this.$emit('update:modelValue', category);
    }
  }
}
</script>

<style scoped>

</style>
