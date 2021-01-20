<template>
  <div>
    <h2>Test Room Create</h2>
      <div>
        <span>Room Index</span><input type="text" v-model="roomIdx" id="roomIdx">
        <br>
        <span>Title</span><input type="text" v-model="title" id="title">
        <br>
        <span>Streamer Id</span><input type="text" v-model="streamerId" id="streamerId">
      </div>
    <a id="create" @click="create()" class="btn btn-primary"><span
        class="glyphicon glyphicon-user"></span> Create </a>
    <br>
    <div v-if = "isCreated">
        <streaming-page></streaming-page>
    </div>
  </div>
</template>

<script>
import axios from "axios"
import StreamingPage from './StreamingPage.vue'

export default {
  components: { StreamingPage },
  created(){

  },
  data(){
    return{
      roomIdx:null,
      title:null,
      streamerId:null,
      isCreated:false
    }
  },
  methods:{
    create(){
      this.sendToServer();
    },
    sendToServer(){
      console.log(this.roomIdx, this.title, this.streamerId)
      axios({
          method:"POST",
          url:"https://localhost:8443/room/create",
          data:{
              roomIdx:this.roomIdx,
              title:this.title,
              presenterId:this.presenterId
          },
      })
      .then((res)=>{
          console.log(res);
          if(res.status==200){
            this.isCreated=true;
          }
      })
      .catch({
          function (error) {
              console.log(error+"error");
          }
      });
    },
  }
}
</script>

<style scoped>

</style>