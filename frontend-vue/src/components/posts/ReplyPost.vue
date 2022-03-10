<template>
  <div>
    <b-button
      size="sm"
      variant="outline-success"
      @click="isModalVisible = !isModalVisible"
    >
      Reply
    </b-button>

    <Teleport to="body">
      <transition name="modal-fade">
        <app-modal
          v-show="isModalVisible"
          header-bg-color="bg-success"
          @cancel="dismissModal"
        >
          <template #header>
            <h4 class="modal-title">Reply to Post</h4>
          </template>

          <template #body>
            <app-post-form
              v-bind="post"
              @cancel-post="dismissModal"
              @submit-post="submitPost"
            ></app-post-form>
          </template>

          <template #footer><br /></template>
        </app-modal>
      </transition>
    </Teleport>
  </div>
</template>

<script>
// ReplyPost
// This component encapsulates the logic for replying to a Blogen Post.
// It provides a 'Reply to Post' button that, when clicked, will launch a modal containing
// a form that can be used to reply to another post.
//
// Props:
// postId - the id of the post to reply to
//
import PostForm from "./PostForn.vue";
import constants from "../../common/constants.js";
import Modal from "../common/BaseModal.vue";
import logger from "../../configs/logger";

export default {
  name: "ReplyPost",
  components: {
    appPostForm: PostForm,
    appModal: Modal,
  },
  props: {
    // the post id to reply to
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
  created() {
    // default this components post data to the post data in the store
    const post = this.$store.getters.getPostById(this.postId);
    this.post.title = "RE: " + post.title;
    this.post.text = post.text;
    this.post.imageUrl = post.imageUrl;
    this.post.categoryName = post.category.name;
  },
  methods: {
    submitPost(postData) {
      logger.debug(`reply to post id:${this.postId} with data:${postData}`);
      this.$store.dispatch("createPost", { id: this.postId, post: postData });
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
