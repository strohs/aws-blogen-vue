// Handles the edit category button and edit category modal window
<template>
  <div class="mx-1">

    <b-btn size="sm" variant="outline-success" @click="showModal">
      Edit
    </b-btn>

    <b-modal ref="editCategoryModalRef"
             title="Edit Category"
             :hide-footer="true"
             header-bg-variant="success"
             header-text-variant="white">

      <app-category-form v-bind="category" @cancel="hideModal" @submit="submit"></app-category-form>

    </b-modal>
  </div>

</template>

<script>
import CategoryForm from './CategoryForn'

export default {
  name: 'EditCategory',
  components: {
    appCategoryForm: CategoryForm
  },
  props: {
    name: String,
    categoryUrl: String
  },
  data () {
    return {
      category: {
        name: this.name,
        oldName: this.name
      }
    }
  },
  methods: {
    submit (data) {
      // submit the form data to parent component
      console.log('cat submitting data:', data)
      this.$emit('submit', data)
      this.hideModal()
    },
    showModal () {
      this.$refs.editCategoryModalRef.show()
    },
    hideModal () {
      // console.log('hide modal')
      this.$refs.editCategoryModalRef.hide()
    }
  }
}
</script>

<style scoped>

</style>
