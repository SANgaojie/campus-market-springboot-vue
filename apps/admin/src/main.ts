/**
 * 应用启动入口
 *
 * @author 阿德
 * @date 2026/05/07
 */
import { createApp } from 'vue'
import router from '@/router'
import App from './App.vue'
import './assets/main.css'

createApp(App).use(router).mount('#app')
