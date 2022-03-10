<template>
  <div class="card mb-3" style="max-width: 540px">
    <div class="row g-0">
      <div class="col-md-4">
        <img
          :src="imgSrc"
          class="img-fluid rounded-start"
          alt="Users Posted Image"
        />
      </div>
      <div class="col-md-8">
        <div class="card-body">
          <h5 class="card-title">{{ title }}</h5>
          <h6 class="card-subtitle mb-2 text-muted">{{ subTitle }}</h6>
          <p class="card-text">{{ text }}</p>
          <p class="card-text">
            <small class="text-muted">{{ footerText }}</small>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  title: {
    type: String,
    default: "",
  },
  subTitle: {
    type: String,
    default: "",
  },
  imgSrc: {
    type: String,
    default: "",
  },
  text: {
    type: String,
    default: "",
  },
  footerText: {
    type: String,
    default: "",
  },
});
</script>

<style scoped></style>
