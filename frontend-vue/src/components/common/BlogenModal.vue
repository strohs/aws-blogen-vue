// BlogenModal
// This is a modal component using Bootstrap5 for styling
//
// Props:
// header-bg-color - set the background color of the header to one of the bootstrap5 background color values
//                   defaults to bg-light
// header-text-color - set the color of the text in the header to a bootstrap5 text color, defaults to 'text-body'
// show-footer - if set to false, the footer of this modal will be hidden, defaults to true
//
// Slots:
// header-title - set the Title text that appears in the modal header
// body - set the elements that will appear in the body of the modal
//
// Emits:
// confirm - if the confirm button of the modal was clicked
//
<script>
export default {
  props: {
    headerBgColor: {
      type: String,
      default: 'bg-light'
    },
    headerTextColor: {
      type: String,
      default: 'text-body'
    },
    showFooter: {
      type: Boolean,
      default: true,
    }
  },
  emits: ['confirm'],
  data() {
    return {
      openModal: false,
    }
  }
}
</script>

<template>

  <!-- Modal -->
  <Teleport to="body">
    <div id="bsModal" class="modal fade" tabindex="-1" aria-labelledby="bsModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">

          <div class="modal-header" :class="headerBgColor">
            <h5 id="bsModalLabel" class="modal-title">
              <slot name="header-title" :class="headerTextColor"></slot>
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>

          <div class="modal-body">
            <slot name="body"></slot>
          </div>

          <div v-show="showFooter" class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
            <button type="button" class="btn btn-primary" data-bs-dismiss="modal" @click="$emit('confirm')">
              <slot name="footer-btn-text">Confirm</slot>
            </button>
          </div>

        </div>
      </div>
    </div>
  </Teleport>

</template>

<style>
</style>