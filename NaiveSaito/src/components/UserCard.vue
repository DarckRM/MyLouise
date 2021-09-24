<template>
    <n-card>
        <n-avatar style="float: left" :size=140 round :src="avatar" />
        <div v-if="credit_buff > 0">
        <n-h1 prefix="bar" style="margin: 10px 0 0 30px; float: left">CREDIT余额 {{credit}}</n-h1>
        <span style="margin: 50px 0 0 -250px; float: left">周期CREDIT回复 <span style="color: green">{{credit_buff}}</span></span>
        <n-h2 prefix="bar" style="float: left; margin: 95px 0 0 -276px;">总功能请求 {{invoke_count}}</n-h2>
        </div>
        <div v-else>
        <n-h1 type="error" prefix="bar" style="margin: 10px 0 0 30px; float: left">CREDIT余额 {{credit}}</n-h1>
        <span style="margin: 50px 0 0 -250px; float: left">周期CREDIT回复 <span style="color: red">{{credit_buff}}</span></span>
        <n-h2 prefix="bar" style="float: left; margin: 95px 0 0 -276px;">总功能请求 {{invoke_count}}</n-h2>
        </div>
        
        
    </n-card>
</template>

<script>
import { defineComponent } from 'vue'

export default defineComponent({
    setup() {
        
    },
    props: {
        avatar: String,
        credit: Number,
        credit_buff: Number,
        invoke_count: Number
    }
})
</script>
