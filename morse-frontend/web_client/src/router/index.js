import Vue from "Vue"
import Router from "vue-router"
import ErrorPage from "../components/ErrorPage.vue"
import MainPage from "../components/MainPage.vue"

Vue.use(Router)

export default new Router({
    mode: "history",
    routes: [
        {
            path: "/",
            name: "main",
            component: MainPage,
        },
        {
            path: "/NoUrl",
            name: "ErrorPage",
            component: ErrorPage,
        }
    ]
})