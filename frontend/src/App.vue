<template>
  <div id="app">

    <Navbar></Navbar>
    <router-view></router-view>
    <Footer></Footer>

  </div>
</template>

<script>
import { components } from 'aws-amplify-vue'
import Navbar from './components/Navbar'
import Footer from './components/Footer'
import { mapActions } from 'vuex'

export default {
  name: 'app',
  components: {
    Navbar,
    Footer,
    ...components
  },
  computed: {
    ...mapActions([
      'authenticateUser'
    ])
  },
  mounted () {
    // check if user is still authenticated, log them in, and save their user info into vuex
    this.$store.dispatch('authenticateUser')
  }
}
</script>

<style>
  /*body {*/
    /*!*this is bootstraps bg-dark color*!*/
    /*background-color: #343a40;*/
  /*}*/
</style>
