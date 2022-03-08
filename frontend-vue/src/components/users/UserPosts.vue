<template>
  <div>
    <!-- Page HEADER -->
    <header id="user-posts-header" class="py-2 bg-gray-500 text-white">
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

    <div class="container">

      <!-- Status Alert -->
      <div class="row my-4 justify-content-center">
        <app-status-alert v-bind="status"></app-status-alert>
      </div>

      <!-- Horizontal Card containing post details -->
      <div class="row mt-2">
        <media-card v-for="post in posts" :key="post.id"
                    class="m-1"
                    :title="post.title"
                    :sub-title="post.category.name"
                    :img-src="post.imageUrl"
                    :text="post.text"
                    :footer-text="'Posted On: ' + formatAsShortDate(post.created)"
        >
        </media-card>



<!--        <b-card-group deck>-->
<!--          <media-card v-for="post in posts" :key="post.id" :title="post.title" :sub-title="post.category.name" :img-src="post.imageUrl">-->

<!--            <template #body-text>-->
<!--              {{post.text}}-->
<!--            </template>-->

<!--            <template #footer-text>-->
<!--              Posted on: {{ formatAsShortDate(post.created) }}-->
<!--            </template>-->
<!--          </media-card>-->
<!--        </b-card-group>-->


        <!--        <b-card-group columns class="mt-2">-->
        <!--          <b-card overlay v-for="post in posts" :key="post.id"-->
        <!--                  :img-src="post.imageUrl" img-alt="Card Image" text-variant="light"-->
        <!--                  :title="post.title"-->
        <!--          >-->
        <!--            <p class="card-text">{{post.text}}</p>-->
        <!--            <div slot="footer">-->
        <!--              <small class="text-muted">Posted in {{ post.category.name }} on {{ formatAsShortDate(post.created) }} </small>-->
        <!--            </div>-->
        <!--          </b-card>-->
        <!--        </b-card-group>-->

      </div>
    </div>

  </div>
</template>

<script>
import { dateShortFormat } from '../../filters/dateFormatFilter'
import constants from '../../common/constants'
import StatusAlert from '../common/StatusAlert.vue'
import { mapState } from 'vuex'
import BaseMediaCard from "../cards/BaseMediaCard.vue"
import BaseHorizCard from "../cards/BaseHorizCard.vue"

export default {
  name: 'UserPosts',
  components: {
    appStatusAlert: StatusAlert,
    mediaCard: BaseHorizCard,
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
            console.log('no posts made for user id:', id);
            this.status.code = 301;
            this.status.message = 'This user has not made any recent posts';
            this.displayStatusAlert();
          }
        })
        .catch(error => {
          this.status.code = error.response.status;
          this.status.message = error.response.data.globalError[0].message;
          this.displayStatusAlert();
        })
    },
    displayStatusAlert () {
      // display the status code from CRUD request
      this.status.show = true
    },
    dismissStatusAlert () {
      this.status.show = false
    },
    formatPostDetails(post) {
      return `Posted in ${post.category.name} on ${ this.formatAsShortDate(post.created) }`
    },
    formatAsShortDate (dateStr) {
      return dateShortFormat(dateStr)
    }
  }
}
</script>

<style scoped>

</style>
