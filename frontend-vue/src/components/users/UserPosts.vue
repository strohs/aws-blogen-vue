<template>
  <div>

    <!-- HEADER -->
    <header id="user-posts-header" class="py-2 bg-secondary text-white">
      <div class="container">
        <div class="row">
          <h2>
            <b-img thumbnail fluid width="50" alt="avatar image" :src="avatarUrl"></b-img>
            Latest Posts from
            <small>{{ userName }}</small>
          </h2>
        </div>
      </div>
    </header>

    <!-- Status Alert -->
    <div class="container">
      <div class="row my-4 justify-content-center">
        <app-status-alert v-bind="status"></app-status-alert>
      </div>
    </div>

    <div class="container">
      <div class="row">
        <b-card-group columns class="mt-4">
          <b-card overlay v-for="post in posts" :key="post.id"
                  :img-src="post.imageUrl" img-alt="Card Image" text-variant="white"
                  :title="post.title"
          >
            <p class="card-text">{{post.text}}</p>
            <div slot="footer">
              <small class="text-muted">Posted in {{ post.category.name }} on {{ formatAsShortDate(post.created) }} </small>
            </div>
          </b-card>
        </b-card-group>
      </div>
    </div>
  </div>
</template>

<script>
import { dateShortFormat } from '../../filters/dateFormatFilter'
import constants from '../../common/constants'
import StatusAlert from '../common/StatusAlert.vue'
import { mapState } from 'vuex'

export default {
  name: 'UserPosts',
  components: {
    appStatusAlert: StatusAlert
  },
  props: {
    id: {
      type: String,
      required: true,
    },
    userName: {
      type: String,
      required: true,
    },
    avatarImage: {
      type: String,
      required: true,
    }
  },
  data () {
    return {
      status: {
        code: 200,
        message: '',
        show: false
      }
    }
  },
  computed: {
    ...mapState({
      posts: state => state.blogenRestApi.posts,
      pageInfo: state => state.blogenRestApi.pageInfo
    }),
    avatarUrl () {
      if (this.avatarImage.indexOf('/') >= 0) {
        const imageFile = this.avatarImage.slice(this.avatarImage.lastIndexOf('/'))
        return constants.DEFAULT_AVATAR_URL + imageFile
      } else {
        return constants.DEFAULT_AVATAR_URL + '/' + this.avatarImage
      }
    }
  },
  mounted () {
    this.fetchPostsByUser(this.id, 0, 9, 'ALL')
    // this.fetchUserInfo(this.userId)
  },
  methods: {
    fetchPostsByUser (id, pageNum, pageLimit, categoryName) {
      this.$store.dispatch('fetchPostsByUser', { id, pageNum, pageLimit, categoryName })
        .then(data => {
          if (data.posts === undefined || data.posts.length === 0) {
            console.log('no posts made for user id:', id)
            this.status.code = 301
            this.status.message = 'This user has not made any recent posts'
            this.displayStatusAlert()
          }
        })
        .catch(error => {
          this.status.code = error.response.status
          this.status.message = error.response.data.globalError[0].message
          this.displayStatusAlert()
        })
    },
    displayStatusAlert () {
      // display the status code from CRUD request
      this.status.show = true
    },
    dismissStatusAlert () {
      this.status.show = false
    },
    formatAsShortDate (dateStr) {
      return dateShortFormat(dateStr)
    }
  }
}
</script>

<style scoped>

</style>
