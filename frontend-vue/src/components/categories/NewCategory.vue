// NewCategory
// This component encapsulates the logic for creating a new Blogen category.
// It provided the 'New Category' button that, when clicked, will launch a modal containing
// the 'CategoryForm' component.
//
// Emits:
// submit({ name, oldName }) - this event will contain an object containing the new category name, as well as
//                             the old category name
<template>
  <div>

    <b-button block variant="success" data-bs-toggle="modal" data-bs-target="#bsModal">
      <font-awesome-icon class="mx-2" icon="plus" />
      New Category
    </b-button>

    <blogen-modal header-bg-color="bg-success" header-text-color="text-white" :show-footer="false">
      <template #header-title>New Category</template>
      <template #body>
        <app-category-form v-bind="category" @cancel="dismissModal" @submit="submitCategory"></app-category-form>
      </template>
    </blogen-modal>

  </div>

</template>

<script>
import CategoryForm from './CategoryForm.vue'
import BlogenModal from "../common/BlogenModal.vue";

export default {
  name: 'NewCategory',
  components: {
    appCategoryForm: CategoryForm,
    BlogenModal,
  },
  emits: ['submit'],
  data () {
    return {
      category: {
        name: ''
      },
      // result of creating the category which is emitted to calling component
      result: {
        code: 0,
        message: '',
        category: {}
      }
    }
  },
  methods: {
    submitCategory (newCategory) {
      this.dismissModal()
      this.$emit('submit', newCategory)
    },
    dismissModal () {
      const bsModal = new bootstrap.Modal(document.getElementById('bsModal'));
      bsModal.hide();
    }
  }
}
</script>

<style scoped>

</style>
