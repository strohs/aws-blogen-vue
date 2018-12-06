<template>

  <!-- IMAGE CAROUSEL -->
  <div class="container">
    <b-carousel id="homePageCarousel"
                style="text-shadow: 1px 1px 2px #333;"
                controls
                indicators
                :interval="4000"
                img-width="300"
                img-height="200"
                v-model="slide"
                @sliding-start="onSlideStart"
                @sliding-end="onSlideEnd">

      <!-- Post slides with image -->
      <b-carousel-slide v-for="post in posts" :key="post.id"
                        :caption="post.text"
                        :text="postDetails(post)"
                        :img-src="post.imageUrl"
      >
      </b-carousel-slide>

    </b-carousel>

  </div>

</template>

<script>
import axios from '../../axios-auth'
import { handleAxiosError } from '../../common/errorHandlers'

export default {
  name: 'PostCarousel',
  data () {
    return {
      posts: [],
      slide: 0,
      sliding: null
    }
  },
  methods: {
    getLatestPosts () {
      axios.get('/api/v1/auth/latestPosts')
        .then(res => {
          console.log('api response', res.data.posts)
          this.posts = this.posts.concat(res.data.posts)
        })
        .catch(error => {
          handleAxiosError(error)
        })
    },
    onSlideStart (slide) {
      this.sliding = true
    },
    onSlideEnd (slide) {
      this.sliding = false
    },
    postDetails (post) {
      return 'Posted by ' + post.user.userName + ' in ' + post.category.name
    }
  },
  created () {
    this.getLatestPosts()
  }
}
</script>

<style scoped>

</style>
