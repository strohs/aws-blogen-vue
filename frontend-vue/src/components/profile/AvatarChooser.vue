<template>
  <div class="row">
    <div class="col">
      <b-form-group
        id="avatarGroup"
        label="Select Your Avatar"
        label-for="avatarSelect"
      >
        <b-form-select
          id="avatarSelect"
          v-model="avatarImage"
          :options="avatars"
          @input="$emit('update:modelValue', avatarImage)"
        >
        </b-form-select>
      </b-form-group>
    </div>

    <div class="col">
      <b-img
        class="ms-5"
        thumbnail
        fluid
        width="100"
        :src="avatarUrl"
        alt="avatar image"
      ></b-img>
    </div>
  </div>
</template>

<script>
// AvatarChooser
// A custom compoment consisting of a select dropdown that displays avatar image file names along
// with a preview image of the avatar.
// It lets a user select their preferred avatar image. Once a file name is selected, it emits
// the filename as a 'update:modelValue' event. For example: "avatar8.jpg".
export default {
  name: "AvatarSelect",
  props: {
    modelValue: {
      type: String,
      default: "avatar0.jpg",
    },
  },
  emits: ["update:modelValue"],
  data() {
    return {
      avatars: [],
      avatarImage: this.modelValue,
    };
  },
  computed: {
    avatarUrl() {
      return this.$store.getters.getAvatarUrlByFileName(this.avatarImage);
    },
  },
  created() {
    // fetch a list of avatar image filenames
    this.$store
      .dispatch("fetchAndStoreAvatarFileNames")
      .then((filenames) => (this.avatars = filenames));
  },
  methods: {},
};
</script>

<style scoped></style>
