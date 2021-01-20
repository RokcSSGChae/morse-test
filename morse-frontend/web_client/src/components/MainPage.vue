<template>
    <div>
        <h1>{{ msg }}</h1>
        <h2>로그인 눌러주세요!</h2>
    </div>
</template>
<style scoped>
    div#returnurl{
        display: inline;
    }
</style>
<script>
export default {
  name: 'MainPage',
  props: {
    msg: String
  },
  methods: {
      
  },
  data() {
      return {
        
      }
  },
}
</script>  