// EditPassword
// A Component that provides a change password button that when clicked opens a modal containing a form that
// can be used to edit a users password
//
// Emits:
// submit({ curPassword, newPassword }) - the submit event contains an object consisting of a two properties
//                                        'curPassword' is the users current password (to be changed)
//                                        'newPassword' is the users new password
//
<template>
  <div class="mx-1">

    <b-button variant="danger" @click="isModalVisible = !isModalVisible">
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
            <appChangePasswordForm @validated="validated" @cancel="dismissModal"></appChangePasswordForm>
          </template>

          <template #footer><br/></template>

        </app-modal>
      </transition>
    </Teleport>

  </div>

</template>

<script>
import ChangePasswordForm from '../../components/profile/ChangePasswordForm.vue'
import Modal from "../common/Modal.vue";

export default {
  name: 'EditPassword',
  components: {
    appChangePasswordForm: ChangePasswordForm,
    appModal: Modal
  },
  emits: ['submit'],
  data () {
    return {
      curPassword: '',
      newPassword: '',
      isModalVisible: false,
    }
  },
  methods: {
    validated (evt) {
      // submit the form data to parent component
      // evt data will contain { curPassword, newPassword }
      this.$emit('submit', evt);
      this.dismissModal()
    },
    dismissModal () {
      this.isModalVisible = false;
    }
  }
}
</script>

<style scoped>

</style>
