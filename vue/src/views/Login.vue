<template>
  <b-container fluid>
    <b-row>
      <b-col cols="0" sm="2" md="4"></b-col>
      <b-col cols="12" sm="8" md="4">
        <div>
          <b-navbar toggleable="lg" type="dark" variant="info" class="navbar-login">
            <b-navbar-brand href="#" class="mx-auto">Login</b-navbar-brand>
          </b-navbar>
        </div>
        <div class="container-login-form">
          <b-form @submit="onSubmit" @reset="onReset">
            <b-form-group id="input-group-1">
              <b-form-input id="email" v-model="form.email" type="email" required placeholder="Email"></b-form-input>
            </b-form-group>
            <b-form-group id="input-group-2">
              <b-form-input id="password" v-model="form.password" type="password" required placeholder="Password"></b-form-input>
            </b-form-group>
            <b-button type="submit" variant="primary">Submit</b-button>
          </b-form>
        </div>
      </b-col>
      <b-col cols="0" sm="2" md="4"></b-col>
    </b-row>
  </b-container>
</template>

<script>
export default {
  name: 'Login',
  data () {
    return {
      form: {
        email: '',
        password: ''
      }
    }
  },

  methods: {
    onSubmit (evt) {
      evt.preventDefault()
      alert(JSON.stringify(this.form))
    }
  }

}
</script>

<style scoped>
  .navbar-login{
    margin-top: 100px;
  }
  .container-login-form{
    padding: 40px 20px 40px 20px;
    border: 1px solid darkgrey;
  }
</style>
