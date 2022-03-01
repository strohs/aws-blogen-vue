// NewPost
// This component encapsulates the logic for creating a new Blogen Post.
// It provides a 'New Post' button that, when clicked, will launch a modal containing
// the 'PostForm' component.
//
// Emits:
// refreshPage - this is emitted once the post data is valid, and has been saved by the store. It indicates that
//               an ancestor component should refresh its view of posts
//
<template>

  <div>

    <b-button block variant="primary" @click="showModal">
      <font-awesome-icon class="mx-2" icon="plus" />
      New Post
    </b-button>

    <Teleport to="body">
      <transition name="modal-fade">
        <app-modal v-show="isModalVisible" @cancel="dismissModal" @confirm="submitPost">

        </app-modal>
      </transition>
    </Teleport>

<!--    <blogen-modal header-bg-color="bg-primary" header-text-color="text-white" :show-footer="false">-->
<!--      <template #header-title>New Post</template>-->
<!--      <template #body>-->
<!--        <app-post-form v-bind="post" @cancel-post="dismissModal" @submit-post="submitPost"></app-post-form>-->
<!--      </template>-->
<!--    </blogen-modal>-->

  </div>

</template>

<script>
import PostForm from './PostForn.vue'
import BlogenModal from "../common/BlogenModal.vue";
import Modal from "../common/Modal.vue";

export default {
  name: 'NewPostModal',
  components: {
    appPostForm: PostForm,
    appModal: Modal
  },
  emits: ['refreshPage'],
  data () {
    return {
      post: {
        title: '',
        text: '',
        // generates a random image URL
        imageUrl: import.meta.env.VITE_IMAGE_PROVIDER + this.genRandomInt(1000),
        categoryName: null
      },
      isModalVisible: false,
    }
  },
  methods: {
    submitPost(postData) {
      this.$store.dispatch('createPost', { post: postData });
      this.$emit('refreshPage');
      this.dismissModal()
    },
    showModal() {
        this.isModalVisible = true;
    },
    dismissModal () {
      this.isModalVisible = false;
    },
    genRandomInt (max) {
      return Math.floor(Math.random() * max) + 1
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
