<template>
    <div id="loginDiv">
    <img style="width: 150px" alt="Vue logo" src="../assets/logo.png">
        MyLouise Backfront Alpha
        <n-divider />
        <n-card v-bind:title="loginPageTitle">
            <n-form :model="loginForm">    
                <n-form-item>
                    <n-input
                        v-model:value="loginForm.username"
                        placeholder="用户名"
                    />
                </n-form-item>
                <n-form-item>
                    <n-input
                        type="password"
                        show-password-on="mousedown"
                        placeholder="密码"
                        :maxlength="8"
                        v-model:value="loginForm.password"
                    />
                </n-form-item>
            </n-form>
            <n-button>Sign in</n-button>
            <n-button @click="signUp()" type="primary">Sign up</n-button>
        </n-card>
    </div>
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
#loginDiv {
    margin-top: 60px;
}
</style>

<script>
  import { defineComponent } from 'vue'
  import { useMessage } from 'naive-ui'
  import { router } from '../router'

  export default defineComponent({

    name: 'LoginPage',
    setup() {
        window.$message = useMessage()
    },
    data() {
        return {
            loginPageTitle: "Login",
            loginForm: {
                username: "",
                password: ""
            }
            
        }
    },
    components: {
      useMessage,
    },
    methods: {
        signUp() {
            window.$message.loading("登录中")
            this.loginPageTitle = "Sign Up"
            this.$axios.post('login', this.loginForm).then(result => {
                let data = result.data
                if(data.code == 200) {
                    window.$message.destroyAll()
                    window.$message.success('登录成功')
                    router.push({
                        path: '/home'
                    })
                } else if(data.code == 403) {
                    window.$message.destroyAll()
                    window.$message.warning('用户已被禁用')
                } else {
                    window.$message.destroyAll()
                    window.$message.error('用户名或密码错误')
                }
            })
        }
    }
    
  })


</script>