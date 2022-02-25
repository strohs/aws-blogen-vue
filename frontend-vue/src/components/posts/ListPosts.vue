// Posts
// This component handles rendering a list of blogen Posts, as well as functionality for perform
// CRUD operations on Posts
<template>
  <!-- Main Posts Page -->
  <section>
    <!-- Post Page HEADER -->
    <header id="post-header" class="py-2 bg-primary">
      <div class="container-fluid">
        <div class="row">
          <div class="col-sm-6">
                <span>
                    <h1 class="text-white"><font-awesome-icon class="mx-2" icon="pencil-alt" scale="1.5"></font-awesome-icon>Posts (<small>{{selectedCategory.name}}</small>)</h1>
                </span>
          </div>
        </div>
      </div>
    </header>

    <!-- FILTER POSTS / SEARCH POSTS -->
    <section id="search-posts" class="py-4 mb-4 bg-light">
      <div class="container-fluid">
        <div class="row">
          <!-- Category Filter Dropdown Button -->
          <div class="col-md-4">
            <app-category-filter-button v-model="selectedCategory"></app-category-filter-button>
          </div>
          <!-- NEW Post Button and modal -->
          <div class="col-md-4">
            <app-new-post></app-new-post>
          </div>
          <!-- SEARCH Post input -->
          <div class="col-md-4">
            <app-post-search v-model="postSearchStr"></app-post-search>
          </div>
        </div>
      </div>
    </section>

    <!-- PAGINATION -->
    <div class="container">
      <div class="row">
        <div class="col">
          <b-pagination
                        v-model="currentNavPage"
                        :total-rows="pageInfo.totalElements"
                        :per-page="pageInfo.pageSize"
                        @input="fetchPage"
          >
          </b-pagination>
        </div>
      </div>
    </div>


    <!-- List of Posts and child posts -->
    <section id="posts" class="py-2">
      <div class="container">
        <div v-for="post in posts" :key="post.id">
          <div class="row my-4">
            <div class="col">
              <transition appear name="fade" mode="out-in">
                <app-post-media v-bind="post"></app-post-media>
              </transition>

              <div class="row my-2" v-for="child in post.children" :key="child.id">
                <div class="col-md-6 offset-md-2">
                  <transition appear name="fade" mode="out-in">
                    <app-post-media v-bind="child"></app-post-media>
                  </transition>
                </div>
              </div>

            </div>
          </div>

        </div>

      </div>
    </section>

  </section>
</template>

<script>
import axios from '../../axios-auth'
import { handleAxiosError } from '../../common/errorHandlers'
import CategoryFilterButton from './CategoryFilterButton.vue'
import NewPost from './NewPost.vue'
import PostSearch from './PostSearch.vue'
import PostMedia from './PostMedia.vue'
import { mapState } from 'vuex'

export default {
  name: 'ListPosts',
  components: {
    appCategoryFilterButton: CategoryFilterButton,
    appNewPost: NewPost,
    appPostSearch: PostSearch,
    appPostMedia: PostMedia
  },
  data () {
    return {
      selectedCategory: { name: 'All' },
      postSearchStr: '',
      pageLimit: 3,
      currentNavPage: 1
    }
  },
  computed: {
    ...mapState({
      posts: state => state.blogenRestApi.posts,
      pageInfo: state => state.blogenRestApi.pageInfo
    })
  },
  watch: {
    selectedCategory (newCategory) {
      // when selected category changes, re-fetch posts with the new category and start on the first page
      this.currentNavPage = 1
      this.fetchPosts(0, this.pageLimit, newCategory.name)
    },
    postSearchStr (newSearchStr) {
      if (newSearchStr.length > 0) {
        this.searchPosts(newSearchStr)
      } else {
        this.fetchPosts(this.pageInfo.pageNumber, this.pageLimit, this.selectedCategory.name)
      }
    }
  },
  created () {
    // pre-fetch a list of all categories and user avatars from the API and put them in the store
    this.$store.dispatch('fetchAndStoreCategories', { pageNum: 0, pageLimit: 10 })
    this.$store.dispatch('fetchAvatarFileNames')
    this.fetchPosts(0)
  },
  methods: {
    fetchPosts (pageNum, pageLimit = 3, categoryName = 'All') {
      this.$store.dispatch('fetchPosts', { pageNum, pageLimit, categoryName })
    },
    fetchPage (pageNum) {
      console.log('fetchPAGE:', pageNum)
      // API page numbers are 0-based, so subtract one from pageNum
      this.fetchPosts(pageNum - 1, this.pageLimit, this.selectedCategory.name)
    },
    searchPosts (searchStr, pageLimit = 3) {
      // TODO possibly add to store
      axios.get(`/api/v1/posts/search/${searchStr}`, {
        params: {
          limit: pageLimit
        }
      })
          .then(res => {
            console.log('searchPosts response:', res.data)
            this.$store.commit('SET_POSTS', res.data.posts)
            this.$store.commit('SET_PAGE_INFO', res.data.pageInfo)
          })
          .catch(error => {
            handleAxiosError(error)
          })
    },
    refreshPage () {
      this.fetchPage(this.currentNavPage)
    }
  }
}
</script>

<style scoped>
  .fade-enter {
    opacity: 0;
  }

  .fade-enter-active {
    transition: opacity 1s;
  }

  .fade-leave {

  }

  .fade-leave-active {
    transition: opacity 1s;
    opacity: 0;
  }

  .slide-enter-active {
    animation: slide-in 500ms ease-out forwards;
  }
  .slide-leave-active {
    animation: slide-out 500ms ease-out forwards;
  }

  @keyframes slide-in {
    from {
      transform: translateX(50px);
      opacity: 0;
    }
    to {
      transform: translateX(0);
      opacity: 1;
    }
  }
  @keyframes slide-out {
    from {
      transform: translateX(0px);
      opacity: 1;
    }
    to {
      transform: translateX(50px);
      opacity: 0;
    }
  }
</style>
