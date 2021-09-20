<template>
    <n-button>
        {{ msg }}
    </n-button>
</template>

<script>

import { defineComponent } from 'vue'

export default defineComponent({

    name: 'SaitoConfig',
    setup() {

    },
    data() {
        return {
            msg: 'Saito'
        }
    }

})

</script>

<style>

</style>