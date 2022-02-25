// EditPost
// A Component that provides an edit button that when clicked opens a modal containing a form that
// can be used to edit the details of a blogen post.
// When the form is submitted, this component will call the store to update the details of the post
//
// Props:
// postId - post id to be be edited, this will be used to retrieve the post details from the store.
//
<template>
  <div class="mx-1">

    <b-button size="sm" variant="outline-primary" data-bs-toggle="modal" data-bs-target="#bsModal">
      Edit
    </b-button>

    <blogen-modal header-bg-color="bg-primary" header-text-color="text-white" :show-footer="false">
      <template #header-title>Edit Post</template>
      <template #body>
        <app-post-form v-bind="post" @cancel-post="dismissModal" @submit-post="submitPost"></app-post-form>
      </template>
    </blogen-modal>

  </div>

</template>

<script>
import PostForm from './PostForn.vue'
import constants from '../../common/constants.js'
import BlogenModal from "../common/BlogenModal.vue";

export default {
  name: 'EditPost',
  components: {
    appPostForm: PostForm,
    BlogenModal
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
      }
    }
  },
  created () {
    // default this components post data to the post data in the store
    const post = this.$store.getters.getPostById(this.postId)
    this.post.title = post.title
    this.post.text = post.text
    this.post.imageUrl = post.imageUrl
    this.post.categoryName = post.category.name
  },
  methods: {
    submitPost (postData) {
      console.log(`edit post id:${this.postId} with data:`, postData)
      this.$store.dispatch('updatePost', { id: this.postId, post: postData })
      this.dismissModal()
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
