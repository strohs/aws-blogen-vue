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

    <b-button variant="danger" @click="showModal">
      Change Password
    </b-button>

    <blogen-modal header-bg-color="bg-info" header-text-color="text-white" :show-footer="false">
      <template #header-title>Change Password</template>
      <template #body>
        <appChangePasswordForm @validated="validated" @cancel="hideModal"></appChangePasswordForm>
      </template>
    </blogen-modal>

  </div>

</template>

<script>
import ChangePasswordForm from '../../components/profile/ChangePasswordForm.vue'
import BlogenModal from "../common/BlogenModal.vue";

export default {
  name: 'EditPassword',
  components: {
    appChangePasswordForm: ChangePasswordForm,
    BlogenModal
  },
  emits: ['submit'],
  data () {
    return {
      curPassword: '',
      newPassword: ''
    }
  },
  methods: {
    validated (evt) {
      // submit the form data to parent component
      // evt data will contain { curPassword, newPassword }
      this.$emit('submit', evt)
      this.dismissModal()
    },
    dismissModal () {
      // hide the bootstrap modal
      const bsModal = new bootstrap.Modal(document.getElementById('bsModal'));
      bsModal.hide();
    }
  }
}
</script>

<style scoped>

</style>
