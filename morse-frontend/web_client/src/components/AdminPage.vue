<template>
    <div>
        <H2>User List</H2>
        <div>
            <center>
                <table border="1">
                    <thead>
                        <td>id</td>
                        <td>uid </td>
                        <td>authorization</td>
                    </thead>
                    <tr v-for="(item, index) in userData" :key="index">
                        <td>{{item.id}} </td>
                        <td>{{item.uid}}</td>
                        <td>{{item.status}}</td>
                        <td>
                            <button @click="deleteUser(item.id)">delete</button>
                        </td>
                    </tr>
                </table>
            </center>
        </div>
    </div>
</template>

<script>
import axios from "axios"
export default {
    created(){
        axios({
            method:"GET",
            url:"http://localhost:8001/getUsetList",
            headers:{
                "token":localStorage.getItem('token')
            },
        })
        .then((res)=>{
            console.log(res);
            if(res.data.code==401){
                this.newToken();
                return;
            }else if(res.data.code==400){
                localStorage.removeItem('token');
                localStorage.removeItem('refreshToken');
                alert("재로그인 해주세요");
            }
            console.log(res.data);
            if(res.data.code==201){
                window.location.href="/#/NoAuth";
            }else if(res.data.code==202){
                this.message="유저가 없습니다.";
            }else{
                this.userData=res.data.userList;
            }
        })
        .catch({
            function (error) {
                console.log(error+"error");
            }
        })
    },
    methods:{
        deleteUser(id){
            var result=confirm("정말로 삭제하시겠습니까?")
            if(result){
                console.log(id);
                axios({
                    method:"POST",
                    url:"http://localhost:8001/deleteUser",
                    headers:{
                        "token":localStorage.getItem('token')
                    },
                    data:{
                        userId:id,
                    }
                })
                .then((res)=>{
                    this.message=res.data.message;
                    alert("삭제완료 되었습니다.");
                    window.location.reload()
                })
                .catch({
                    function (error) {
                        console.log(error+"error");
                    }
                });
            }else{
                return;
            }
        },
        newToken(){
            axios({
                method:"POST",
                url:"http://localhost:8001/getNewToken",
                data:{
                    "token":localStorage.getItem('token'),
                    "refreshToken":localStorage.getItem('refreshToken')
                }
            })
            .then((res)=>{
                console.log(res);
                if(res.data.code==215){
                    localStorage.removeItem('token');
                    localStorage.removeItem('refreshToken');
                    alert("재로그인 해주세요");
                    window.location.href="/#/"
                    
                }else if(res.data.code==220){
                    localStorage.removeItem('token');
                    localStorage.removeItem('refreshToken');
                    alert("재로그인 해주세요");
                    window.location.href="/#/"
                }else{
                    localStorage.setItem('token',res.data.token);
                    return;
                }
            })
            .catch({
                function (error) {
                    console.log(error+"error");
                }
            });
        }
    },
    data(){
        return{
            message:null,
            userData:[]
        }
    }
}
</script>