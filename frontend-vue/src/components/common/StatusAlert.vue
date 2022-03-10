// Component for displaying application wide status/info/failure messages in an
alert box
<template>
  <b-alert
    :variant="computeVariant"
    dismissible
    @dismissed="$emit('dismissed')"
  >
    {{ message }}
  </b-alert>
</template>

<script>
// TODO possibly make this an app wide component and move its state into VUEX
export default {
  name: "StatusAlert",
  props: {
    message: {
      type: String,
      default: "",
    },
    code: {
      type: Number,
      required: true,
    },
  },
  emits: ["dismissed"],
  data() {
    return {};
  },
  computed: {
    // computes the alert box variant based on the status code passed into the component
    computeVariant() {
      if (this.code >= 200 && this.code < 300) {
        return "success";
      } else if (this.code >= 400 && this.code <= 500) {
        return "danger";
      } else {
        return "info";
      }
    },
  },
};
</script>

<style scoped></style>
