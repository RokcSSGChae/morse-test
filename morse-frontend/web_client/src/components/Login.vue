<template>
    <div>
       <div> 
            <form>
                Id : <input type="text" v-model="id" name="id" id="id" placeholder="Id">
                <br>
                Password : <input type="password" v-model="password" name="password" id="password" placeholder="Password">
            </form>
            <br>
        <button @click="submit" value="submit">submit</button></div>
        <div v-if="this.message!=null">{{message}}</div>
    </div>
</template>
<style>
    form{
        display: inline;
    }
</style>

<script>
import axios from "axios"

export default {
  name: 'LoginPage',
  props: {
    msg: String
  },
  methods: {
      submit(){
          axios({
              method:"POST",
              url:"http://localhost:8001/login",
              data:{
                uid:this.id,
                password:this.password
              },
          })
          .then((res)=>{
              console.log(res);
              if(res.data.code==201){
                this.message=res.data.message;
                return;
              }else if(res.data.code==202){
                this.message=res.data.message;
                return;
              }
              else{
                localStorage.setItem("token",res.data.token);
                localStorage.setItem("refreshToken",res.data.refreshToken);
                window.location.href="/"
              }
          })
          .catch({
              function (error) {
                  console.log(error+"error");
              }
          })
      }
  },
  data() {
      return {
          message:null
      }
  },
}
</script>