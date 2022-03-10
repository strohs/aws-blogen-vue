<template>
  <!-- Main Splash Page recent posts -->
  <b-card-group columns class="mt-4">
    <app-post-card
      v-for="post in posts"
      :key="post.id"
      v-bind="post"
    ></app-post-card>
  </b-card-group>
</template>

<script>
import PostCard from "./PostCard.vue";
import axios from "../../axios-auth";
import { handleAxiosError } from "../../common/errorHandlers";
import logger from "../../configs/logger";

export default {
  name: "PostCards",
  components: {
    appPostCard: PostCard,
  },
  data() {
    return {
      posts: [],
    };
  },
  created() {
    this.getLatestPosts();
  },
  methods: {
    getLatestPosts() {
      axios
        .get("/api/v1/auth/latestPosts")
        .then((res) => {
          logger.debug("api response:", res.data);
          this.posts = this.posts.concat(res.data.posts);
        })
        .catch((error) => {
          handleAxiosError(error);
        });
    },
  },
};
</script>

<style scoped></style>
