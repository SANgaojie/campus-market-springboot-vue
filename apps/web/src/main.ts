/**
 * 应用启动入口
 *
 * @author 阿德
 * @date 2026/05/09
 */
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from '@/router'
import App from './App.vue'
import './assets/main.css'

createApp(App)
  .use(createPinia())
  .use(router)
  .mount('#app')
