// EditCategory
// A Component that provides an edit button that when clicked opens a modal containing a form that
// can be used to edit a category name
//
// Props:
// name - the initial category name that will be used to populate the form
//
// Emits:
// submit({ name, oldName }) - the submit event contains an object consisting of a 'name' property and a
//                             'oldName' property. name is the new category name, oldName is the old category name
<template>
  <div class="mx-1">

    <b-button size="sm" variant="outline-success" data-bs-toggle="modal" data-bs-target="#bsModal">
      Edit
    </b-button>

    <blogen-modal header-bg-color="bg-success" header-text-color="text-white" :show-footer="false">
      <template #header-title>Edit Category</template>
      <template #body>
        <app-category-form v-bind="category" @cancel="dismissModal" @submit="submit"></app-category-form>
      </template>
    </blogen-modal>

  </div>

</template>

<script>
import CategoryForm from './CategoryForm.vue';
import BlogenModal from "../common/BlogenModal.vue";

export default {
  name: 'EditCategory',
  components: {
    appCategoryForm: CategoryForm,
    BlogenModal
  },
  props: {
    name: {
      type: String,
      default: '',
    },
    categoryUrl: {
      type: String,
      default: '',
    }
  },
  emits: ['submit'],
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
      console.log('edit category submitting data:', data);
      this.$emit('submit', data);
      this.dismissModal()
    },
    dismissModal () {
      // hide the bootstrap modal
      const bsModal = new bootstrap.Modal(document.getElementById('bsModal'));
      bsModal.hide();
    }
  }
}
</script>

<style scoped>

</style>
