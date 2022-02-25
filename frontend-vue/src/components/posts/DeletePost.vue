// DeletePost
// This component consists of a "delete" button that will launch a modal to confirm if the
// user really wants to delete a post. If confirmed, the vuex store will be called to actually
// perform the deletion
//
// Props:
// postId - the post id to be deleted
<template>
  <div class="mx-1 text-dark">

    <b-button size="sm" variant="outline-danger" data-bs-toggle="modal" data-bs-target="#bsModal">
      Delete
    </b-button>

    <blogen-modal header-bg-color="bg-danger" header-text-color="text-white" @confirm="deletePost">
      <template #header-title>Delete Post</template>
      <template #body>
        <h4>Are you sure?</h4>
      </template>
    </blogen-modal>

  </div>

</template>

<script>
import constants from '../../common/constants.js';
import BlogenModal from "../common/BlogenModal.vue";

export default {
  name: 'DeletePost',
  components: {
    BlogenModal
  },
  props: {
    // the post id to delete
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
  methods: {
    deletePost () {
      console.log(`delete post with id:${this.postId}`)
      this.$store.dispatch('deletePost', this.postId)
    }
  }
}
</script>

<style scoped>

</style>
