// PostCarousel // This component displays the latest posts (along with their
image) made to blogen as a // sliding image carousel
<template>
  <div id="latestPostCarousel" class="carousel slide" data-bs-ride="carousel">
    <!--    <div class="carousel-indicators">-->
    <!--      <button type="button" data-bs-target="#latestPostCarousel" data-bs-slide-to="0" class="active" aria-current="true" aria-label="Slide 1"></button>-->
    <!--      <button type="button" data-bs-target="#latestPostCarousel" data-bs-slide-to="1" aria-label="Slide 2"></button>-->
    <!--      <button type="button" data-bs-target="#latestPostCarousel" data-bs-slide-to="2" aria-label="Slide 3"></button>-->
    <!--    </div>-->

    <div class="carousel-inner">
      <div
        v-for="(post, index) in posts"
        :key="post.id"
        class="carousel-item"
        :class="[index === 0 ? 'active' : '']"
      >
        <img :src="post.imageUrl" class="d-block w-100" alt="post image" />

        <div class="carousel-caption d-none d-md-block">
          <h5>{{ post.text }}</h5>
          <p>{{ postDetails(post) }}</p>
        </div>
      </div>
    </div>
    <button
      class="carousel-control-prev"
      type="button"
      data-bs-target="#latestPostCarousel"
      data-bs-slide="prev"
    >
      <span class="carousel-control-prev-icon" aria-hidden="true"></span>
      <span class="visually-hidden">Previous</span>
    </button>
    <button
      class="carousel-control-next"
      type="button"
      data-bs-target="#latestPostCarousel"
      data-bs-slide="next"
    >
      <span class="carousel-control-next-icon" aria-hidden="true"></span>
      <span class="visually-hidden">Next</span>
    </button>
  </div>
</template>

<script>
import axios from "../../configs/axios-auth";
import { handleAxiosError } from "../../common/errorHandlers";
import logger from "../../configs/logger";

export default {
  name: "PostCarousel",
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
          logger.debug("api response", res.data.posts);
          this.posts = this.posts.concat(res.data.posts);
        })
        .catch((error) => {
          handleAxiosError(error);
        });
    },
    postDetails(post) {
      return "Posted by " + post.user.userName + " in " + post.category.name;
    },
  },
};
</script>

<style scoped></style>
