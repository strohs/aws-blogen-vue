<template>
  <b-input-group id="postSearchGroup">
    <b-form-input
      v-model="searchStr"
      type="text"
      placeholder="Search All Posts"
    ></b-form-input>
    <b-input-group-append>
      <b-button variant="primary" type="submit" @click="emitSearchStr"
        >Search</b-button
      >
      <b-button
        variant="secondary"
        :disabled="searchStr.length === 0"
        @click="emitClear"
        >Clear</b-button
      >
    </b-input-group-append>
  </b-input-group>
</template>

<script>
// PostSearch
// This is a vue custom component that contains an input field and button.
// The input field will capture the string to search for, and the button, when clicked, will submit the contents of
// the input field.
//
// Emits:
// search (search string) - will contain the string being searched for
// clear - clears the search string input by setting it to the empty string
//
import logger from "../../configs/logger";

export default {
  name: "PostSearch",
  props: {
    value: {
      type: String,
      default: "",
    },
  },
  emits: ["search", "clear"],
  data() {
    return {
      searchStr: "",
    };
  },
  methods: {
    emitSearchStr() {
      logger.debug("post search string:", this.searchStr);
      this.$emit("search", this.searchStr);
    },
    emitClear() {
      this.searchStr = "";
      this.$emit("clear");
    },
  },
};
</script>

<style scoped></style>
