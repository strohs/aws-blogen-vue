<template>
  <div id="app">

    <Navbar></Navbar>
    <router-view></router-view>


  </div>
</template>

<script setup>
import Navbar from './components/Navbar.vue'
import { useStore } from 'vuex';

const store = useStore();

// check if user is still authenticated, if so, store their credentials and user details
// within our store
store.dispatch('authenticateUser');

</script>

<style scoped>
</style>
