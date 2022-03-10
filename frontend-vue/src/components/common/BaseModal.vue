<script>
export default {
  name: "Modal",
  props: {
    headerBgColor: {
      type: String,
      default: "bg-light",
    },
    headerTextColor: {
      type: String,
      default: "text-black",
    },
  },
  emits: ["cancel", "confirm"],
  methods: {
    cancel() {
      this.$emit("cancel");
    },
    confirm() {
      this.$emit("confirm");
    },
  },
};
</script>

<template>
  <div class="modal-backdrop">
    <div class="modal-dialog">
      <div
        class="modal-content"
        role="dialog"
        aria-labelledby="modalTitle"
        aria-describedby="modalDescription"
      >
        <header class="modal-header" :class="[headerBgColor, headerTextColor]">
          <slot name="header" class="modal-title"> Default Header </slot>
          <button
            type="button"
            class="btn-close"
            aria-label="Close Modal"
            @click="cancel"
          ></button>
        </header>

        <section id="modalDescription" class="modal-body">
          <slot name="body">
            <p>This is the default body!</p>
          </slot>
        </section>

        <footer class="modal-footer">
          <slot name="footer">
            <button type="button" class="btn btn-secondary" @click="cancel">
              Cancel
            </button>
            <button type="button" class="btn btn-primary" @click="confirm">
              Confirm
            </button>
          </slot>
        </footer>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-backdrop {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: rgba(0, 0, 0, 0.5);
}
</style>
