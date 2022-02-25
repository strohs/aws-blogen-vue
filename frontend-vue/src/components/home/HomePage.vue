<template>
  <div id="homePage" class="container-fluid">
    <header id="welcome-head-section" class="row bg-blue-300 text-white">
      <div class="col text-center my-4">
        <div>
          <h1 class="display-4">Welcome To Blogen</h1>
        </div>
      </div>
    </header>

    <!-- MAIN PAGE HEADER -->
    <section id="main-header" class="row bg-gray-700 text-white">
      <div class="col-md text-center my-2">
        <h3>A blogging community sharing pictures and thoughts on a variety of topics</h3>
      </div>
    </section>

    <div id="postCarouselSection" class="row mt-4">
      <div class="col-md-4 mx-auto">
        <post-carousel></post-carousel>
      </div>
    </div>


    <!-- HOME ICON SECTION -->
    <section id="homeIcons" class="row mt-4 p-4 bg-gray-100 justify-content-around">
      <div class="col-md-3 text-center">
        <font-awesome-icon class="mb-2" icon="pencil-alt"></font-awesome-icon>
        <h3>Blogging:</h3>
        <p>Use Blogen...to blog! Connect with thousands of users!</p>
      </div>
      <div class="col-md-3 text-center">
        <font-awesome-icon class="mb-2" icon="camera"></font-awesome-icon>
        <h3>Photo Sharing:</h3>
        <p>Not just text, pictures! They are worth a thousand words.</p>
      </div>
      <div class="col-md-3 text-center">
        <font-awesome-icon class="mb-2" icon="cloud"></font-awesome-icon>
        <h3>All in the cloud:</h3>
        <p>Blogen is cloud based, you data is available from any device, anywhere in the world!</p>
      </div>
    </section>

    <!-- Mission Statement SECTION HEADER-->
    <section id="about-header" class="row">
      <div class="col-md-6 m-auto text-center">
        <h1>Our Mission</h1>
        <p>We are a group of technology professionals that believe in connecting the world through the
          power of words and images.
        </p>
        <br>
        <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Odit, perspiciatis?</p>
        <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Quasi, saepe!</p>
        <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Commodi, fuga!</p>
        <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Adipisci, nihil.</p>
        <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Eligendi, molestiae.</p>


        <b-button class="my-4" :to="{ name: 'clogin' }"
                  type="button"
                  variant="primary">
          Sign-Up
        </b-button>

      </div>
    </section>

    <BlogenFooter class="mt-4"></BlogenFooter>

  </div>
</template>

<script>
import PostCarousel from './PostCarousel.vue';
import BlogenFooter from "../BlogenFooter.vue";

export default {
  name: 'HomePage',
  components: {
    PostCarousel,
    BlogenFooter
  },
  data() {
    return {
      msg: ''
    }
  }
}
</script>

<style scoped>
/*@import "src/scss/blogen-styles.scss";*/


#about-header {
  background: url('/images/lights.jpg');
  background-position: center;
  background-attachment: fixed;
  background-size: cover;
  color: #fff;
}

/*.about-img {*/
/*margin-top: -50px;*/
/*}*/
</style>
