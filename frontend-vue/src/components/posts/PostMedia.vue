<template>
  <div class="d-flex">
    <div class="flex-shrink-0">
      <b-img
        slot="aside"
        thumbnail
        fluid
        width="100"
        alt="avatar image"
        :src="image"
        @mouseover="image = imageUrl"
        @mouseout="image = avatarUrl"
      >
      </b-img>
    </div>

    <div class="flex-grow-1 ms-3">
      <h5 class="fw-bold">{{ title }}</h5>
      <h6>
        Posted By:
        <small>
          <router-link
            :to="{
              name: 'users',
              params: {
                id: user.id,
                userName: user.userName,
                avatarImage: user.avatarUrl,
              },
            }"
            >{{ user.userName }}</router-link
          >
        </small>
        in
        <small class="font-italic">{{ category.name }}</small>
        on
        <small class="font-italic">{{ formatCreatedAsLongDate }}</small>
      </h6>

      <p>{{ text }}</p>

      <!-- CRUD buttons for a post -->
      <div class="d-flex">
        <app-reply-post
          v-if="isParentPost"
          class="mx-2"
          :post-id="id"
        ></app-reply-post>

        <app-edit-post
          v-if="canEditOrDeletePost"
          class="mx-2"
          :post-id="id"
        ></app-edit-post>

        <app-delete-post
          v-if="canEditOrDeletePost"
          class="mx-2"
          :post-id="id"
        ></app-delete-post>
      </div>
    </div>
  </div>
</template>

<script>
// PostMedia
// Uses bootstrap 5 css to render a post as a "traditional bootstrap media object".
// A media object renders the user's avatar image to the left, along with the title, category, and text of the post to the right.
//
import { dateLongFormat } from "../../filters/dateFormatFilter";
import { mapGetters } from "vuex";
import ReplyPost from "./ReplyPost.vue";
import EditPost from "./EditPost.vue";
import DeletePost from "./DeletePost.vue";

export default {
  name: "PostMedia",
  components: {
    appReplyPost: ReplyPost,
    appEditPost: EditPost,
    appDeletePost: DeletePost,
  },
  props: {
    id: {
      type: String,
      default: "",
    },
    title: {
      type: String,
      default: "",
    },
    text: {
      type: String,
      default: "",
    },
    created: {
      type: String,
      default: "",
    },
    imageUrl: {
      type: String,
      default: "",
    },
    category: {
      type: Object,
      default: "",
    },
    user: {
      type: Object,
      default: "",
    },
    threadId: {
      type: String,
      default: "",
    },
    postId: {
      type: String,
      default: "",
    },
    children: {
      type: Array,
      default: [],
    },
  },
  data() {
    return {
      // image will hold an url to either the users avatar or the image the user posted
      image: this.user.avatarUrl,
    };
  },
  computed: {
    ...mapGetters(["isAdmin", "getAuthUserId", "getAuthUser"]),
    avatarUrl() {
      return this.user.avatarUrl;
    },
    canEditOrDeletePost() {
      // the logged in user can delete a post of they created the post
      // ADMINS can edit or delete all posts
      return this.getAuthUserId === this.user.id || this.isAdmin;
    },
    isParentPost() {
      // a post is a parent post if its threadId === postId
      return this.threadId === this.postId;
    },
    formatCreatedAsLongDate() {
      return dateLongFormat(this.created);
    },
  },
  methods: {},
};
</script>

<style scoped></style>
