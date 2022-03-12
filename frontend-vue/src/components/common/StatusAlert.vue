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

<script setup>
import {computed} from "vue";

const props = defineProps({
  message: {
    type: String,
    default: "",
  },
  code: {
    type: Number,
    required: true,
  }
});

const emit = defineEmits(['dismissed']);


const computeVariant = computed(() => {
  if (props.code >= 200 && props.code < 300) {
    return "success";
  } else if (props.code >= 400 && props.code <= 500) {
    return "danger";
  } else {
    return "info";
  }
});

</script>

<style scoped></style>
