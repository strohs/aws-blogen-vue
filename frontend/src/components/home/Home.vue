<template>
  <div id="homePage">
    <header id="welcome-head-section" class="bg-primary text-white">
      <div class="container">
        <div class="row">
          <div class="col text-center my-4">
            <div>
              <h1 class="display-4">Welcome To Blogen</h1>
            </div>
          </div>
        </div>
      </div>
    </header>

    <!-- MAIN PAGE HEADER -->
    <section id="main-header" class="bg-secondary text-white">
      <div class="container">
        <div class="row">
          <div class="col-md text-center my-2">
            <h3>A blogging community sharing pictures and thoughts on a variety of topics</h3>
          </div>
        </div>
      </div>
    </section>

    <section id="postCarousel">
      <div class="container">
        <div class="row">
          <div class="col-md-8 mx-auto mt-4">
            <appPostCarousel></appPostCarousel>
          </div>
        </div>
      </div>
    </section>

    <!-- HOME ICON SECTION -->
    <section id="homeIcons" class="py-5">
      <div class="container">
        <div class="row">
          <div class="col-md-4 mb-4 text-center">
            <icon class="mb-2" name="pencil-alt"></icon>
            <h3>Blogging:</h3>
            <p>Use Blogen...to blog! Connect with thousands of users!</p>
          </div>
          <div class="col-md-4 mb-4 text-center">
            <icon class="mb-2" name="camera"></icon>
            <h3>Photo Sharing:</h3>
            <p>Not just text, pictures! They are worth a thousand words.</p>
          </div>
          <div class="col-md-4 mb-4 text-center">
            <icon class="mb-2" name="cloud"></icon>
            <h3>All in the cloud:</h3>
            <p>Blogen is cloud based, you data is available from any device, anywhere in the world!</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Mission Statement SECTION HEADER-->
    <section id="about-header">
      <div class="container">
        <div class="row">
          <div class="col-md-6 m-auto text-center">
            <h1>Our Mission</h1>
            <p>We are a group of technology professionals, that believe in connecting the world, through the
              written word and images.
              <br>
              <b-button :to="{ name: 'clogin', params: { loginState: 'SIGN_UP' } }"
                        type="button"
                        variant="primary">
                Sign-Up
              </b-button>
            </p>
          </div>
        </div>
      </div>
    </section>

  </div>
</template>

<script>
import PostCarousel from './PostCarousel'

export default {
  name: 'home',
  data () {
    return {
      msg: ''
    }
  },
  components: {
    appPostCarousel: PostCarousel
  },
  beforeRouteEnter (to, from, next) {
    console.log('to path:', to.path)
    if (to.path === '/cognito-signin') {
      next(vm => {
        vm.$store.dispatch('authenticateUser')
          .then(isAuthenticated => {
            console.log('isAuthenticated:', isAuthenticated)
            vm.$router.push({ name: 'posts' })
          })
      })
    } else {
      next()
    }
  }
}
</script>

<style scoped>

  #about-header {
    height: 200px;
    background: url('/images/image1.jpg');
    background-position: 0 -360px;
    background-attachment: fixed;
    color: #fff;
    border-bottom: 1px #eee solid;
    padding-top: 50px;
  }

  /*.about-img {*/
    /*margin-top: -50px;*/
  /*}*/
</style>
