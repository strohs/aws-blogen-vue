<template>
  <div class="mx-1">
    <b-button variant="warning" @click="isModalVisible = !isModalVisible">
      Change Password
    </b-button>

    <Teleport to="body">
      <transition name="modal-fade">
        <app-modal
          v-show="isModalVisible"
          header-bg-color="bg-warning"
          @cancel="dismissModal"
        >
          <template #header>
            <h4 class="modal-title">Change Password</h4>
          </template>

          <template #body>
            <appChangePasswordForm
              @validated="validated"
              @cancel="dismissModal"
            ></appChangePasswordForm>
          </template>

          <template #footer><br /></template>
        </app-modal>
      </transition>
    </Teleport>
  </div>
</template>

<script>
// EditPassword
// A Component that provides a change password button that whenclicked opens a modal containing a form that
// can be used to edit a userspassword
//
// Emits:
// submit({ curPassword, newPassword }) - the submit eventcontains an object consisting of a two properties
// 'curPassword' is the userscurrent password (to be changed)
// 'newPassword' is the users new password
//
import ChangePasswordForm from "../../components/profile/ChangePasswordForm.vue";
import Modal from "../common/BaseModal.vue";

export default {
  name: "EditPassword",
  components: {
    appChangePasswordForm: ChangePasswordForm,
    appModal: Modal,
  },
  emits: ["submit"],
  data() {
    return {
      curPassword: "",
      newPassword: "",
      isModalVisible: false,
    };
  },
  methods: {
    validated(evt) {
      // submit the form data to parent component
      // evt data will contain { curPassword, newPassword }
      this.$emit("submit", evt);
      this.dismissModal();
    },
    dismissModal() {
      this.isModalVisible = false;
    },
  },
};
</script>

<style scoped></style>
