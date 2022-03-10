// EditCategory // A Component that provides an edit button that when clicked
opens a modal containing a form that // can be used to edit a category name //
// Props: // name - the initial category name that will be used to populate the
form // // Emits: // submit({ name, oldName }) - the submit event contains an
object consisting of a 'name' property and a // 'oldName' property. name is the
new category name, oldName is the old category name
<template>
  <div class="mx-1">
    <b-button
      size="sm"
      variant="outline-success"
      @click="isModalVisible = !isModalVisible"
    >
      Edit
    </b-button>

    <Teleport to="body">
      <transition name="modal-fade">
        <app-modal
          v-show="isModalVisible"
          header-bg-color="bg-success"
          @cancel="dismissModal"
        >
          <template #header>
            <h4 class="modal-title">Edit Category</h4>
          </template>

          <template #body>
            <app-category-form
              v-bind="category"
              @cancel="dismissModal"
              @submit="submit"
            ></app-category-form>
          </template>

          <template #footer><br /></template>
        </app-modal>
      </transition>
    </Teleport>
  </div>
</template>

<script>
import CategoryForm from "./CategoryForm.vue";
import Modal from "../common/BaseModal.vue";
import logger from "../../configs/logger";

export default {
  name: "EditCategory",
  components: {
    appCategoryForm: CategoryForm,
    appModal: Modal,
  },
  props: {
    name: {
      type: String,
      default: "",
    },
    categoryUrl: {
      type: String,
      default: "",
    },
  },
  emits: ["submit"],
  data() {
    return {
      category: {
        name: this.name,
        oldName: this.name,
      },
      isModalVisible: false,
    };
  },
  methods: {
    submit(data) {
      // submit the form data to parent component
      logger.debug("edit category submitting data:", data);
      this.$emit("submit", data);
      this.dismissModal();
    },
    dismissModal() {
      this.isModalVisible = false;
    },
  },
};
</script>

<style scoped>
.modal-fade-enter,
.modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.5s ease;
}
</style>
