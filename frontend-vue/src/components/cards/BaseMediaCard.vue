<template>
  <b-card>
    <b-card :title="title" :sub-title="subTitle" :img-src="imgSrc" img-alt="user posted image" img-top>

      <b-card-text>
        <slot name="body-text">
          Default Text
        </slot>
      </b-card-text>

      <template #footer>
        <small class="text-muted">
          <slot name="footer-text"></slot>
        </small>
      </template>

    </b-card>
  </b-card>

</template>

<script setup>
  const props = defineProps({
    title: {
      type: String,
      default: '',
    },
    subTitle: {
      type: String,
      default: '',
    },
    imgSrc: {
      type: String,
      default: '',
    }
  })

</script>

<style scoped>

</style>