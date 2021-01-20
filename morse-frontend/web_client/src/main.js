import Vue from 'vue'
import App from './App.vue'
import VueRouter from 'vue-router'
import MainPage from "./components/MainPage.vue"
import Login from "./components/Login.vue"
import Join from "./components/Join.vue"
import common from './assets/common'
import StreamingPage from './components/StreamingPage.vue'
import ViewingPage from './components/ViewingPage.vue'
import BootstrapVue from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.min.css'
import TestRoomCreatePage from "@/components/TestRoomCreatePage";
//import 'ekko-lightbox/dist/ekko-lightbox.min.css'

Vue.config.productionTip = false
Vue.config.devtools = false
Vue.use(VueRouter);
Vue.use(common);
Vue.use(BootstrapVue);

global.jQuery = require('jquery');
var $ = global.jQuery;
window.$ = $;

const routes = [
    {
        path: "/",
        name: "main",
        component: MainPage,
    },
    {
        path: "/login",
        name: "login Page",
        component: Login,
    },
    {
        path: "/join",
        name: "join Page",
        component: Join,
    },
    {
        path: "/streaming-page",
        name: "stream",
        component: StreamingPage,
    },
    {
        path: "/viewing-page",
        name: "viewing",
        component: ViewingPage,
    },
    {
        path: "/test-room-create-page",
        name: "test room create page",
        component: TestRoomCreatePage,
    }
]

const router = new VueRouter({
    routes
});

new Vue({
    render: h => h(App),
    router
}).$mount('#app')
