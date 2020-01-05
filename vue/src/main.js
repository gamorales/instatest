// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import BootstrapVue from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import axios from 'axios'
import VueAxios from 'vue-axios'
import Vuex from 'vuex'

Vue.config.productionTip = false
Vue.use(BootstrapVue)
Vue.use(Vuex)
Vue.use(VueAxios, axios)

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})

const store = new Vuex.Store({ // eslint-disable-line no-unused-vars

  state: {
    jwt: localStorage.getItem('token'),
    endpoints: {
      obtainJWT: 'http://0.0.0.0:10000/auth/obtain_token',
      refreshJWT: 'http://0.0.0.0:10000/auth/refresh_token'
    }
  },

  mutations: {
    updateToken (state, newToken) {
      localStorage.setItem('token', newToken)
      state.jwt = newToken
    },
    removeToken (state) {
      localStorage.removeItem('token')
      state.jwt = null
    }
  },

  actions: {

    obtainToken (email, password) {
      const payload = {
        email: email,
        password: password
      }

      axios.post(this.state.endpoints.obtainJWT, payload)
        .then((response) => {
          this.commit('updateToken', response.data.token)
        })
        .catch((error) => {
          console.log(error)
        })
    },

    refreshToken () {
      const payload = {
        token: this.state.jwt
      }

      axios.post(this.state.endpoints.refreshJWT, payload)
        .then((response) => {
          this.commit('updateToken', response.data.token)
        })
        .catch((error) => {
          console.log(error)
        })
    },

    inspectToken () {
    }
  }
})
