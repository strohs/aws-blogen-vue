<template>
  <img
    :src="blogenLogoUrl"
    :width="width"
    :height="height"
    class="d-inline-block align-top"
    alt="Blogen Logo"
  />
</template>

<script setup>
import blogenLogoUrl from "../../assets/images/blogen-logo.png";

defineProps({
  height: {
    type: String,
    required: true,
  },
  width: {
    type: String,
    required: true,
  },
});
</script>

<style scoped></style>
