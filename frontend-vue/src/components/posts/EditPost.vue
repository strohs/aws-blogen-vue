// EditPost
// A Component that provides an edit button that when clicked opens a modal containing a form that
// can be used to edit the details of a blogen post.
// When the form is submitted, this component will call the store to update the details of the post
//
// Props:
// postId - post id to be be edited, this will be used to retrieve the post details from the store.
//
<template>
  <div>

    <b-button size="sm" variant="outline-primary" @click="isModalVisible = !isModalVisible">
      Edit
    </b-button>

    <Teleport to="body">
      <transition name="modal-fade">
        <app-modal
            v-show="isModalVisible"
            header-bg-color="bg-primary"
            @cancel="dismissModal"
        >
          <template #header>
            <h4 class="modal-title">Edit Post</h4>
          </template>

          <template #body>
            <app-post-form v-bind="post" @cancel-post="dismissModal" @submit-post="submitPost"></app-post-form>
          </template>

          <template #footer><br/></template>

        </app-modal>
      </transition>
    </Teleport>

  </div>

</template>

<script>
import PostForm from './PostForn.vue'
import constants from '../../common/constants.js'
import Modal from "../common/Modal.vue";

export default {
  name: 'EditPost',
  components: {
    appPostForm: PostForm,
    appModal: Modal,
  },
  props: {
    // the post id to edit
    postId: {
      type: String,
      required: true,
    }
  },
  data () {
    return {
      post: {
        title: '',
        text: '',
        imageUrl: constants.API_SERVER_URL + constants.DEFAULT_IMAGE_PATH,
        categoryName: ''
      },
      isModalVisible: false,
    }
  },
  created () {
    // default this components post data to the post data in the store
    const post = this.$store.getters.getPostById(this.postId);
    this.post.title = post.title;
    this.post.text = post.text;
    this.post.imageUrl = post.imageUrl;
    this.post.categoryName = post.category.name;
  },
  methods: {
    submitPost (postData) {
      console.log(`edit post id:${this.postId} with data:`, postData);
      this.$store.dispatch('updatePost', { id: this.postId, post: postData });
      this.dismissModal();
    },
    dismissModal () {
      this.isModalVisible = false;
    }
  }
}
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
