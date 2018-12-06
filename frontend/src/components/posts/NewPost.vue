// Handles the new post button and new post modal dialog
<template>
  <div>

    <b-button block variant="primary" v-b-modal.newPostModal>
      <icon class="mx-2" name="plus"></icon>
      New Post
    </b-button>

    <b-modal id="newPostModal" ref="newPostModalRef"
             title="New Post"
             :hide-footer="true"
             header-bg-variant="primary"
             header-text-variant="white">

      <app-post-form v-bind="post" @cancelPost="hideModal" @submitPost="submitPost"></app-post-form>

    </b-modal>
  </div>

</template>

<script>
import PostForm from './PostForn'

export default {
  name: 'NewPostModal',
  components: {
    appPostForm: PostForm
  },
  props: {
    postId: String // the post id to perform CRUD ops on
  },
  data () {
    return {
      post: {
        title: '',
        text: '',
        // generates a random image URL
        imageUrl: process.env.VUE_APP_IMAGE_PROVIDER + this.genRandomInt(1000),
        categoryName: null
      }
    }
  },
  methods: {
    submitPost (postData) {
      this.$store.dispatch('createPost', { post: postData })
      this.$emit('refreshPage')
      this.hideModal()
    },
    showModal () {
      this.$refs.newPostModalRef.show()
    },
    hideModal () {
      // console.log('hide modal')
      this.$refs.newPostModalRef.hide()
    },
    genRandomInt (max) {
      return Math.floor(Math.random() * max) + 1
    }
  }
}
</script>

<style scoped>

</style>
