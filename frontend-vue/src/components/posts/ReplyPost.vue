// NewPost
// This component encapsulates the logic for replying to a Blogen Post.
// It provides a 'Reply to Post' button that, when clicked, will launch a modal containing
// a form that can be used to enter a new post that will be a reply to another post.
//
// Props:
// postId - the id of the post to reply to
//
<template>
  <div class="mx-1">

    <b-button size="sm" variant="outline-success" data-bs-toggle="modal" data-bs-target="#bsModal">
      Reply
    </b-button>

    <blogen-modal header-bg-color="bg-success" header-text-color="text-white" :show-footer="false">
      <template #header-title>Reply to Post</template>
      <template #body>
        <app-post-form v-bind="post" @cancel-post="dismissModal" @submit-post="submitPost"></app-post-form>
      </template>
    </blogen-modal>

  </div>

</template>

<script>
import PostForm from './PostForn.vue';
import constants from '../../common/constants.js';
import BlogenModal from "../common/BlogenModal.vue";

export default {
  name: 'ReplyPost',
  components: {
    appPostForm: PostForm,
    BlogenModal
  },
  props: {
    // the post id to reply to
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
    this.post.title = 'RE: ' + post.title
    this.post.text = post.text
    this.post.imageUrl = post.imageUrl
    this.post.categoryName = post.category.name
  },
  methods: {
    submitPost (postData) {
      console.log(`reply to post id:${this.postId} with data:${postData}`)
      this.$store.dispatch('createPost', { id: this.postId, post: postData })
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
