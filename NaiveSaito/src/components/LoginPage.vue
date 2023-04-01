<template>
  <div id="loginDiv">
    <div style="margin: 0 auto; width: 400px">
      <img style="width: 150px" alt="Vue logo" src="../assets/logo.png" />
      MyLouise Backfront Alpha
    </div>
    <n-divider />
    <n-card v-bind:title="loginPageTitle" id="loginCard">
      <n-tabs @update:value="updateValue" default-value="signin">
        <n-tab-pane name="signin" tab="登录">
          <n-form :model="loginForm">
            <n-form-item>
              <n-input v-model:value="loginForm.username" placeholder="用户名" />
            </n-form-item>
            <n-form-item>
              <n-input
                v-on:keyup.enter="signUp()"
                type="password"
                show-password-on="mousedown"
                placeholder="密码"
                :maxlength="8"
                v-model:value="loginForm.password"
              />
            </n-form-item>
          </n-form>
          <n-button @click="quickLogin()">Login</n-button>
          <n-button style="margin-left: 60px" @click="signUp()" type="primary"
            >Confirm</n-button
          >
        </n-tab-pane>
        <n-tab-pane name="signup" tab="注册">
          <n-button style="margin: 20px 25px">Reset</n-button>
          <n-button @click="signUp()" type="primary" style="margin: 20px 25px"
            >Confirm</n-button
          >
        </n-tab-pane>
        <n-tab-pane name="image-retrieve" tab="图像检索">
          <ImageRetrieve></ImageRetrieve>
        </n-tab-pane>
      </n-tabs>
    </n-card>
  </div>
</template>

<style>
#loginCard {
  width: 300px;
  margin: 50px auto;
}
#loginDiv {
  margin: 100px auto;
}
</style>

<script>
import { defineComponent } from "vue";
import { useMessage } from "naive-ui";
import { router } from "../router";
import ImageRetrieve from "./ImageRetrieve.vue";

export default defineComponent({
  name: "LoginPage",
  setup() {
    const message = useMessage();
    return {
      message,
    };
  },
  data() {
    return {
      loginPageTitle: "TECH OTAKU SAVE THE WORLD",
      loginForm: {
        username: "",
        password: "",
      },
    };
  },
  components: {
    useMessage,
    ImageRetrieve,
  },
  methods: {
    updateValue(value) {
      if (value == "image-retrieve") router.push("/image-retrieve");
    },
    signUp() {
      this.message.loading("登录中");
      this.loginPageTitle = "Sign Up";
      this.$axios.post("login", this.loginForm).then((result) => {
        let data = result.data;
        if (data.code == 200) {
          this.message.destroyAll();
          this.message.success("登录成功");
          const timestamp = data.data.password;
          this.$store.commit("set_token", timestamp);
          router.push({
            path: "/home/index",
          });
        } else if (data.code == 403) {
          this.message.destroyAll();
          this.message.warning("用户已被禁用");
        } else {
          this.message.destroyAll();
          this.message.error("用户名或密码错误");
        }
      });
    },
    quickLogin() {
      this.message.destroyAll();
      this.message.success("登录成功");
      this.$store.commit("set_token", "123456");
      router.push({
        path: "/home/index",
      });
    },
  },
});
</script>
