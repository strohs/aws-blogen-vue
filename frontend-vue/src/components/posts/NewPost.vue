// NewPost
// This component encapsulates the logic for creating a new Blogen Post.
// It provides a 'New Post' button that, when clicked, will launch a modal containing
// a 'PostForm' component.
//
// Emits:
// refreshPage - this is emitted once the post data is valid, and has been saved by the store. It indicates that
//               an ancestor component should refresh its view of posts
//
<template>
  <div>

    <b-button block variant="primary" data-bs-toggle="modal" data-bs-target="#bsModal">
      <font-awesome-icon class="mx-2" icon="plus" />
      New Post
    </b-button>

    <blogen-modal header-bg-color="bg-primary" header-text-color="text-white" :show-footer="false">
      <template #header-title>New Post</template>
      <template #body>
        <app-post-form v-bind="post" @cancel-post="dismissModal" @submit-post="submitPost"></app-post-form>
      </template>
    </blogen-modal>

  </div>

</template>

<script>
import PostForm from './PostForn.vue'
import BlogenModal from "../common/BlogenModal.vue";

export default {
  name: 'NewPostModal',
  components: {
    appPostForm: PostForm,
    BlogenModal
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
      }
    }
  },
  methods: {
    submitPost (postData) {
      this.$store.dispatch('createPost', { post: postData });
      this.$emit('refreshPage');
      this.dismissModal()
    },
    dismissModal () {
      const bsModal = new bootstrap.Modal(document.getElementById('bsModal'));
      bsModal.hide();
    },
    genRandomInt (max) {
      return Math.floor(Math.random() * max) + 1
    }
  }
}
</script>

<style scoped>

</style>
