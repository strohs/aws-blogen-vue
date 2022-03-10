// DeletePost // This component consists of a "delete" button that will launch a
modal to confirm the deletion. // If confirmed, the vuex store will be called to
actually perform the deletion // // Props: // postId - the post id to be deleted
<template>
  <div>
    <b-button
      size="sm"
      variant="outline-danger"
      @click="isModalVisible = !isModalVisible"
    >
      Delete
    </b-button>

    <Teleport to="body">
      <transition name="modal-fade">
        <app-modal
          v-show="isModalVisible"
          header-bg-color="bg-danger"
          header-text-color="text-white"
          @cancel="dismissModal"
          @confirm="deletePost"
        >
          <template #header>
            <h4 class="modal-title">Delete Post</h4>
          </template>

          <template #body> Are You Sure? </template>
        </app-modal>
      </transition>
    </Teleport>
  </div>
</template>

<script>
import constants from "../../common/constants.js";
import Modal from "../common/BaseModal.vue";
import logger from "../../configs/logger";

export default {
  name: "DeletePost",
  components: {
    appModal: Modal,
  },
  props: {
    // the post id to delete
    postId: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      post: {
        title: "",
        text: "",
        imageUrl: constants.API_SERVER_URL + constants.DEFAULT_IMAGE_PATH,
        categoryName: "",
      },
      isModalVisible: false,
    };
  },
  methods: {
    deletePost() {
      logger.debug(`delete post with id:${this.postId}`);
      this.$store.dispatch("deletePost", this.postId);
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
