<template>
    <div>
       <a href="#/login"><span v-if="!isAuthenticated()">Login</span></a>
       <a href="#/join"> <span v-if="!isAuthenticated()">Join</span> </a>
       <a><span @click="onClickLogout()" v-if="isAuthenticated()">Logout</span></a>
       <a href="#/myInfo"><span v-if="isAuthenticated()">MY Info</span></a>
       <a href="#/findPassword"><span v-if="!isAuthenticated()">Find Password</span></a>
    </div>
</template>
<style scoped>
 span{
     margin: 20px;
 }
</style>

<script>
var isEmpty = function (value) {
    if (value == "" || value == null || value == undefined || (value != null && typeof value == "object" && !Object.keys(value).length)) {
      return true
    } else {
      return false
    }
  };
export default {
  computed: {
  },
  methods: {
    onClickLogout() {
      localStorage.removeItem("token");
      localStorage.removeItem("refreshToken");
      window.location.href="/"
    },
    isAuthenticated() {
      return !isEmpty(localStorage.getItem('token'));
    }
  },
  created(){
    console.log(!isEmpty(localStorage.getItem('token')));
  }
}
</script>