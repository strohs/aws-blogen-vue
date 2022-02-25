<template>
    <!-- a Bootstrap card containing a single user post -->
  <b-card no-body>
    <b-card-header><h3>{{ category.name }}</h3></b-card-header>
    <b-card-img :src="imageUrl" alt="Image" top></b-card-img>
    <b-card-body :title="title">
      <p class="card-text">{{ text }}</p>
      <br>
      <br>
      <small>
        Posted by
        <cite title="Source Title">{{ user.userName }}</cite>
        on <cite title="Source Title">{{ new Date(created).toLocaleString() }}</cite>
      </small>
    </b-card-body>
  </b-card>

</template>

<script>
export default {
  name: 'PostCard',
  props: {
    id: {
      type: String,
      default: '',
    },
    title: {
      type: String,
      default: '',
    },
    user: {
      type: String,
      default: '',
    },
    imageUrl: {
      type: String,
      default: '',
    },
    category: {
      type: String,
      default: '',
    },
    text: {
      type: String,
      default: '',
    },
    created: {
      type: String,
      default: '',
    }
  },
}
</script>

<style scoped>

</style>
