/**
 * env.d 模块
 *
 * @author 阿德
 * @date 2026/05/09
 */
declare module '*.css'

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<object, object, unknown>
  export default component
}
