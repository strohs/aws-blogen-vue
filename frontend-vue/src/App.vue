<template>
  <div id="app">

    <Navbar></Navbar>
    <router-view></router-view>


  </div>
</template>

<script setup>
import Navbar from './components/Navbar.vue'
import { useStore } from 'vuex';
import {onMounted} from "vue";

const store = useStore();

onMounted( async () => {
  // if user us currently authenticated, try to prefetch catagories and avatar names
  const user = await store.dispatch('authenticateUser');
  if (user) {
    await store.dispatch('fetchAndStoreCategories', {});
    await store.dispatch('fetchAndStoreAvatarFileNames');
  }

});



</script>

<style scoped>
</style>
