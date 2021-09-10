<template>
    <n-card v-bind:title="loginPageTitle">
        <n-input
            v-model:value="value"
            placeholder="用户名"
        />
        <n-input
            type="password"
            show-password-on="mousedown"
            placeholder="密码"
            :maxlength="8"
        />
        <n-button>Sign in</n-button>
        <n-button @click="signUp()" type="primary">Sign up</n-button>
    </n-card>
</template>

<style>
.n-card {
  max-width: 300px;
  margin: 50px auto;
}
.n-input {
    margin-bottom: 10px;
}
.n-button {
    margin: 20px 25px;
}
</style>

<script>
  import { defineComponent } from 'vue'
  import { NCard, NButton, NInput } from 'naive-ui'
  import { axios } from 'axios'

  export default defineComponent({
    data() {
        return {
            loginPageTitle: "Login"
        }
    },
    components: {
      NCard,
      NButton,
      NInput
    },
    methods: {
        signUp() {
            this.loginPageTitle = "Sign Up"
            axios.post('test').then(result => {
                console.log(result)
            })
        }
    }
    
  })


</script>