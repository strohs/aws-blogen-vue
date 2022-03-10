// NewCategory // This component encapsulates the logic for creating a new
Blogen category. // It provided the 'New Category' button that, when clicked,
will launch a modal containing // the 'CategoryForm' component. // // Emits: //
submit({ name, oldName }) - this event will contain an object containing the new
category name, as well as // the old category name
<template>
  <div>
    <b-button block variant="success" @click="isModalVisible = !isModalVisible">
      <font-awesome-icon class="mx-2" icon="plus" />
      New Category
    </b-button>

    <Teleport to="body">
      <transition name="modal-fade">
        <app-modal
          v-show="isModalVisible"
          header-bg-color="bg-success"
          @cancel="dismissModal"
        >
          <template #header>
            <h4 class="modal-title">New Category</h4>
          </template>

          <template #body>
            <app-category-form
              v-bind="category"
              @cancel="dismissModal"
              @submit="submitCategory"
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

export default {
  name: "NewCategory",
  components: {
    appCategoryForm: CategoryForm,
    appModal: Modal,
  },
  emits: ["submit"],
  data() {
    return {
      category: {
        name: "",
      },
      // result of creating the category which is emitted to calling component
      result: {
        code: 0,
        message: "",
        category: {},
      },
      isModalVisible: false,
    };
  },
  methods: {
    submitCategory(newCategory) {
      this.dismissModal();
      this.$emit("submit", newCategory);
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
