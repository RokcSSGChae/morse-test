<template>
    <div>
        <div>My id is : {{uid}}</div>
        <br>
        <div>My histroys</div>
        <ul>
            <li v-for="(item, index) in myHistory" :key="index">{{index}} {{item.originalUrl}} localhost:8001/{{item.shortUrl}}</li>

        </ul>
    </div>
</template>

<script>
import axios from "axios"
export default {
    created(){
        axios({
            method:"GET",
            url:"http://localhost:8001/myInfo",
            headers:{
                "token":localStorage.getItem('token')
            },
        })
        .then((res)=>{
            console.log(res.data);
            this.uid=res.data.uid;
            this.myHistory=res.data.histroys;
        })
        .catch({
            function (error) {
                console.log(error+"error");
            }
        })
    },
    data(){
        return{
            uid:null,
            myHistory:[]
        }
    }
}
</script>