import { createStore } from 'vuex';
const state = {
  status: window.sessionStorage.getItem('status'),
  token: window.sessionStorage.getItem('token')
}

const mutations = {
  set_token(state, token) {
    state.status = true
    state.token = token
    window.sessionStorage.setItem('token', token)
    window.sessionStorage.setItem('status', true)
  },
  del_token(state) {
    state.status = false
    state.token = ''
    window.sessionStorage.removeItem('token')
  }
}

const actions = { }

export default createStore({
  state,
  mutations,
  actions
})
