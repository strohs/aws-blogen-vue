// Handles the edit password button and edit password modal window
<template>
  <div class="mx-1">

    <b-btn variant="danger" @click="showModal">
      Change Password
    </b-btn>

    <b-modal ref="editPasswordModalRef"
             title="Change Password"
             :hide-footer="true"
             header-bg-variant="info"
             header-text-variant="white">

      <appChangePasswordForm @validated="validated" @cancel="hideModal"></appChangePasswordForm>
    </b-modal>
  </div>

</template>

<script>
import ChangePasswordForm from '@/components/profile/ChangePasswordForm'

export default {
  name: 'EditPassword',
  components: {
    appChangePasswordForm: ChangePasswordForm
  },
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
      this.hideModal()
    },
    showModal () {
      this.$refs.editPasswordModalRef.show()
    },
    hideModal () {
      this.$refs.editPasswordModalRef.hide()
    }
  }
}
</script>

<style scoped>

</style>
