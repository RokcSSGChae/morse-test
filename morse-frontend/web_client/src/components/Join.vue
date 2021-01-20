<template>
    <div>
       <div> 
            <form>
                Id : <input v-model="email" type="email" id="uid" name="uid" placeholder="Id">
                <button @click="checkEmail()">Check Email</button>
                <br>
                Password : <input v-model="password" type="password" id="password" name="password" placeholder="Password">
                <br>
                confirm Password : <input v-model="cpassword" type="password" name="cpassword" placeholder="Password">
            </form>
            <br>
        <button @click="submit" value="submit">submit</button></div>
        <div v-if="message!=null">{{message}}</div>
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
    methods:{
        checkEmail(){
            axios({
                method:"POST",
                url:"http://localhost:8001/checkEmail",
                data:{
                    email:this.email
                },
            })
            .then((res)=>{
                if(res.data){
                    alert("가입 가능합니다");
                    this.checkEmailValidate=true;
                    return;
                }else{
                    alert("이미 가입된 메일입니다.");
                    return;
                }
            })
        },
        submit(){
            if(!this.isEmail(this.email)){
                this.message="이메일 형식으로 맞춰주세요"
                return;
            }
            if(this.password!=this.cpassword){
                this.message="재확인 비밀번호와 비밀번호가 다릅니다."
                return;
            }
            this.message=null;
            
            axios({
                method:"POST",
                url:"http://localhost:8001/join",
                data:{
                    uid:this.email,
                    password:this.password
                },
            })
            .then((res)=>{
                console.log(res.data);
                if(res.data.code==201){
                this.message=res.data.message;
                }else{
                window.location.href="/"
                }
            })
            .catch({
                function (error) {
                    console.log(error+"error");
                }
            })
        },
        isEmail(asValue) {
            var regExp = /^[0-9a-zA-Z]([-_]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;
            return regExp.test(asValue); // 형식에 맞는 경우 true 리턴	
        }
    },
    data(){
        return{
            message:null,
            checkEmailValidate:null
        }
    }
}
</script>