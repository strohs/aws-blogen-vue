// This Component is a select dropdown with associated image element that lets a user select an
//  avatar image. It expects the parent component to use v-model with this component to capture the selected
//  avatar image name
<template>

  <div class="row">
    <div class="col">
      <b-form-group id="avatarGroup" label="Select Your Avatar" label-for="avatarSelect">
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
      <b-img class="ms-5" thumbnail fluid width="100" :src="avatarUrl" alt="avatar image"></b-img>
    </div>

  </div>

</template>

<script>
export default {
  name: 'AvatarSelect',
  emits: ['update:modelValue'],
  props: {
    modelValue: {
      type: String,
      default: 'avatar0.jpg'
    }
  },
  data () {
    return {
      avatars: [],
      avatarImage: this.modelValue
    }
  },
  computed: {
    avatarUrl () {
      return this.$store.getters.getAvatarUrlByFileName(this.avatarImage)
    }
  },
  created () {
    // fetch a list of avatar image filenames
    this.$store.dispatch('fetchAndStoreAvatarFileNames')
      .then(filenames => this.avatars = filenames);
  },
  methods: {
  }
}
</script>

<style scoped>

</style>
