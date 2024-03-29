<template>
  <!-- Main Posts Page -->
  <section>
    <!-- Post Page HEADER -->
    <header id="post-header" class="container-fluid py-2 bg-primary">
      <div class="row">
        <div class="col-sm-6">
          <h2 class="text-white">
            <font-awesome-icon
              class="mx-2"
              icon="pencil-alt"
              scale="1.5"
            ></font-awesome-icon>
            Posts (<small>{{ selectedCategory.name }}</small
            >)
          </h2>
        </div>
      </div>
    </header>

    <!-- FILTER POSTS By Category, New Post Button, SEARCH POSTS -->
    <section id="search-posts" class="container-fluid bg-light">
      <div class="row py-2">
        <!-- Category Filter Dropdown Button -->
        <div class="col">
          <app-category-filter-button
            v-model="selectedCategory"
          ></app-category-filter-button>
        </div>
        <!-- New Post Button -->
        <div class="col">
          <b-button block variant="primary" @click="showModal">
            <font-awesome-icon class="mx-2" icon="plus" />
            New Post
          </b-button>
        </div>
        <!-- SEARCH Post input -->
        <div class="col">
          <app-post-search
            v-model="postSearchStr"
            @search="searchPosts"
            @clear="refreshPage"
          ></app-post-search>
        </div>
      </div>
    </section>

    <!-- PAGINATION -->
    <div class="container py-2">
      <div class="row">
        <div class="col">
          <b-pagination
            v-model="currentNavPage"
            :total-rows="pageInfo.totalElements"
            :per-page="pageInfo.pageSize"
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
              <Transition appear name="fade" mode="out-in">
                <app-post-media v-bind="post"></app-post-media>
              </Transition>

              <div
                v-for="child in post.children"
                :key="child.id"
                class="row my-2"
              >
                <div class="col-md-6 offset-md-2">
                  <Transition appear name="fade" mode="out-in">
                    <app-post-media v-bind="child"></app-post-media>
                  </Transition>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </section>

  <!-- New Post Modal -->
  <Teleport to="body">
    <transition name="modal-fade">
      <app-modal
        v-show="isModalVisible"
        header-bg-color="bg-blue-300"
        @cancel="dismissModal"
      >
        <template #header>
          <h4 class="modal-title">New Post</h4>
        </template>

        <template #body>
          <post-form
            v-bind="post"
            @cancel-post="dismissModal"
            @submit-post="submitPost"
          ></post-form>
        </template>

        <template #footer><br /></template>
      </app-modal>
    </transition>
  </Teleport>
</template>

<script>
// ListPosts
// This component handles rendering a list of all Posts made to blogen.
// It provides the ability to filter posts by category, to search post titles and test,
// as well as functionality for perform CRUD operations on Posts
//
import axios from "../../configs/axios-auth";
import { mapState } from "vuex";
import { handleAxiosError } from "../../common/errorHandlers";
import CategoryFilterButton from "./CategoryFilterButton.vue";
import PostSearch from "./PostSearch.vue";
import PostMedia from "./PostMedia.vue";
import Modal from "../common/BaseModal.vue";
import PostForm from "./PostForn.vue";
import logger from "../../configs/logger";

export default {
  name: "ListPosts",
  components: {
    appCategoryFilterButton: CategoryFilterButton,
    appPostSearch: PostSearch,
    appPostMedia: PostMedia,
    appModal: Modal,
    PostForm: PostForm,
  },
  data() {
    return {
      selectedCategory: { name: "All", categoryUrl: "" },
      postSearchStr: "",
      pageLimit: 3,
      currentNavPage: 1,
      isModalVisible: false,
      post: {
        title: "",
        text: "",
        // generates a random image URL
        imageUrl: import.meta.env.VITE_IMAGE_PROVIDER + this.genRandomInt(1000),
        categoryName: null,
      },
    };
  },
  computed: {
    ...mapState({
      posts: (state) => state.blogenRestApi.posts,
      pageInfo: (state) => state.blogenRestApi.pageInfo,
    }),
  },
  watch: {
    // when selected category changes, re-fetch posts with the new category and start on the first page
    selectedCategory(newCategory) {
      logger.debug("selected category changed to: ", this.selectedCategory.name);
      this.fetchPosts(0, this.pageLimit, newCategory.name);
      this.currentNavPage = 1;
    },
    // when navigation page changes, select a new page
    currentNavPage(newPageNum) {
      logger.debug("fetchPAGE:", newPageNum);
      this.fetchPosts(
        newPageNum - 1,
        this.pageLimit,
        this.selectedCategory.name
      );
    },
  },
  created() {
    this.fetchPosts(0);
  },
  methods: {
    fetchPosts(pageNum, pageLimit = 3, categoryName = "All") {
      this.$store.dispatch("fetchPosts", { pageNum, pageLimit, categoryName });
    },
    // fetchPage() {
    //   logger..debug('fetchPAGE:', this.currentNavPage);
    //   this.fetchPosts(this.currentNavPage - 1, this.pageLimit, this.selectedCategory.name)
    // },
    searchPosts(searchStr, pageLimit = 3) {
      axios
        .get(`/api/v1/posts/search/${searchStr}`, {
          params: {
            limit: pageLimit,
          },
        })
        .then((res) => {
          logger.debug("searchPosts response:", res.data);
          this.$store.commit("SET_POSTS", res.data.posts);
          this.$store.commit("SET_PAGE_INFO", res.data.pageInfo);
        })
        .catch((error) => {
          handleAxiosError(error);
        });
    },
    refreshPage() {
      this.fetchPosts(this.currentNavPage - 1);
    },
    submitPost(postData) {
      this.$store.dispatch("createPost", { post: postData });
      this.$emit("refreshPage");
      this.dismissModal();
    },
    showModal() {
      this.isModalVisible = true;
    },
    dismissModal() {
      this.isModalVisible = false;
    },
    genRandomInt(max) {
      return Math.floor(Math.random() * max) + 1;
    },
  },
};
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
