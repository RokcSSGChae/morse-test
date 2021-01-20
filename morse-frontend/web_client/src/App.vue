<template>
  <div id="app">
  <div>로고를 누르시면 홈으로 이동합니다</div>
    <Menu/>
    <a href="/"><img alt="Vue logo" width="300"  src="./assets/logo.png"></a>
    <router-view></router-view>
  </div>
</template>

<script>
import Menu from "./components/Menu.vue"
export default {
  name: 'App',
  components: {
    Menu
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
