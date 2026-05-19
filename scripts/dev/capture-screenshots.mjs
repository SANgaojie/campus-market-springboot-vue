/**
 * 项目截图生成脚本。
 *
 * @author 阿德
 * @date 2026/05/19
 */
import fs from 'node:fs/promises'
import { spawn } from 'node:child_process'

const CHROME = process.env.CHROME_BIN || '/opt/google/chrome/chrome'
const PORT = 18893
const OUTPUT_DIR = 'docs/screenshots'
const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms))

await fs.mkdir(OUTPUT_DIR, { recursive: true })

const chrome = spawn(CHROME, [
  '--headless=new',
  '--no-sandbox',
  '--disable-gpu',
  '--window-size=1440,1000',
  `--remote-debugging-port=${PORT}`,
  '--user-data-dir=/tmp/campus-market-screenshots',
  'about:blank',
], { stdio: 'ignore' })

async function getJson(url, opts) {
  const response = await fetch(url, opts)
  if (!response.ok) throw new Error(`${url} ${response.status}`)
  return response.json()
}

let ready = false
for (let i = 0; i < 80; i += 1) {
  try {
    await getJson(`http://127.0.0.1:${PORT}/json/version`)
    ready = true
    break
  } catch {
    await sleep(250)
  }
}
if (!ready) throw new Error('Chrome DevTools endpoint was not ready')

const target = await getJson(`http://127.0.0.1:${PORT}/json/new?about:blank`, { method: 'PUT' })
const ws = new WebSocket(target.webSocketDebuggerUrl)
await new Promise((resolve, reject) => {
  ws.addEventListener('open', resolve, { once: true })
  ws.addEventListener('error', reject, { once: true })
})

let id = 0
const pending = new Map()
ws.addEventListener('message', (event) => {
  const message = JSON.parse(event.data)
  if (message.id && pending.has(message.id)) {
    const item = pending.get(message.id)
    pending.delete(message.id)
    message.error ? item.reject(new Error(JSON.stringify(message.error))) : item.resolve(message.result)
  }
})

function send(method, params = {}) {
  const callId = ++id
  ws.send(JSON.stringify({ id: callId, method, params }))
  return new Promise((resolve, reject) => pending.set(callId, { resolve, reject }))
}

async function evalJs(expression) {
  const result = await send('Runtime.evaluate', { expression, awaitPromise: true, returnByValue: true })
  if (result.exceptionDetails) throw new Error(JSON.stringify(result.exceptionDetails))
  return result.result.value
}

async function waitFor(expression) {
  for (let i = 0; i < 50; i += 1) {
    try {
      if (await evalJs(expression)) return
    } catch {
      // wait and retry
    }
    await sleep(250)
  }
  throw new Error(`wait timeout: ${expression}`)
}

async function screenshot(path) {
  const shot = await send('Page.captureScreenshot', { format: 'png', captureBeyondViewport: true })
  await fs.writeFile(path, Buffer.from(shot.data, 'base64'))
}

await send('Page.enable')
await send('Runtime.enable')

await send('Page.navigate', { url: 'http://localhost:5173/' })
await waitFor(`document.body.innerText.includes('发现校园好物') && document.body.innerText.includes('共 ') && !document.body.innerText.includes('Request aborted')`)
await sleep(600)
await screenshot(`${OUTPUT_DIR}/web-home.png`)

await evalJs(`document.querySelector('input[placeholder="搜索商品标题或描述"]').value='MacBook'; document.querySelector('input[placeholder="搜索商品标题或描述"]').dispatchEvent(new Event('input',{bubbles:true})); document.querySelector('button.primary').click()`)
await waitFor(`document.body.innerText.includes('MacBook Air')`)
await screenshot(`${OUTPUT_DIR}/web-search.png`)

await send('Page.navigate', { url: 'http://localhost:5174/' })
await waitFor(`document.body.innerText.includes('管理员登录')`)
await evalJs(`document.querySelectorAll('input')[0].value='admin'; document.querySelectorAll('input')[0].dispatchEvent(new Event('input',{bubbles:true})); document.querySelectorAll('input')[1].value='admin123456'; document.querySelectorAll('input')[1].dispatchEvent(new Event('input',{bubbles:true})); document.querySelector('button.primary').click()`)
await waitFor(`document.body.innerText.includes('用户总数') && document.body.innerText.includes('商品状态分布') && document.body.innerText.includes('订单状态流转') && !document.body.innerText.includes('加载中')`)
await sleep(600)
await screenshot(`${OUTPUT_DIR}/admin-dashboard.png`)

console.log('screenshots written to docs/screenshots')
ws.close()
chrome.kill('SIGTERM')
