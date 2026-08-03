<script setup>
import { computed, onBeforeUnmount, ref } from 'vue'
import { Bell, CalendarDays, ChevronDown, CircleUserRound, Clock3, LayoutDashboard, LogOut, Menu, Music2, Search, ShieldCheck, ShoppingBag, Ticket, UsersRound, X } from 'lucide-vue-next'

const isSidebarOpen = ref(false)
const searchQuery = ref('')
const selectedDate = ref(0)
const selectedTicket = ref(1)
const quantity = ref(1)
const dialogOpen = ref(false)
const loginOpen = ref(false)
const account = ref({ username: '', password: '' })
const loggedIn = ref(Boolean(localStorage.getItem('seckill-token')))
const pending = ref(false)
const message = ref('')
let clearMessageTimer

const dates = [
  { day: '08.16', week: '周六' }, { day: '08.17', week: '周日' }, { day: '08.22', week: '周五' },
  { day: '08.23', week: '周六' }, { day: '08.24', week: '周日' }
]
const tickets = [
  { id: 1, name: '看台票', price: 480, stock: 128, note: '一层看台，视野开阔' },
  { id: 2, name: '内场 A 区', price: 880, stock: 36, note: '临近主舞台，专属入口' },
  { id: 3, name: '内场 VIP', price: 1280, stock: 12, note: '前排区域，含纪念礼包' }
]
const selected = computed(() => tickets.find((ticket) => ticket.id === selectedTicket.value))
const total = computed(() => selected.value.price * quantity.value)

function showMessage(text) {
  message.value = text
  clearTimeout(clearMessageTimer)
  clearMessageTimer = setTimeout(() => { message.value = '' }, 3400)
}

function openPurchase() {
  if (!loggedIn.value) {
    loginOpen.value = true
    return
  }
  dialogOpen.value = true
}

async function login() {
  if (!account.value.username || !account.value.password) {
    showMessage('请输入账号和密码')
    return
  }
  pending.value = true
  try {
    const response = await fetch('/api/auth/login', {
      method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(account.value)
    })
    const result = await response.json()
    if (!response.ok || !result.data?.token) throw new Error(result.message || '登录失败')
    localStorage.setItem('seckill-token', result.data.token)
    loggedIn.value = true
    loginOpen.value = false
    showMessage('登录成功，现在可以提交秒杀订单')
  } catch (error) {
    showMessage(error.message || '无法连接后端服务')
  } finally { pending.value = false }
}

async function submitOrder() {
  pending.value = true
  try {
    const tokenResult = await request('/api/seckill/1/token', { method: 'POST' })
    const submitToken = tokenResult.data?.submitToken
    if (!submitToken) throw new Error(tokenResult.message || '获取下单令牌失败')
    const result = await request('/api/seckill/1', {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ skuId: selected.value.id, quantity: quantity.value, submitToken })
    })
    if (!result.data?.requestId) throw new Error(result.message || '秒杀请求未被受理')
    dialogOpen.value = false
    showMessage(`秒杀请求已提交，排队编号 ${result.data.requestId.slice(0, 8)}`)
  } catch (error) {
    showMessage(error.message || '提交失败，请稍后再试')
  } finally { pending.value = false }
}

async function request(url, options) {
  const response = await fetch(url, {
    ...options,
    headers: { Authorization: `Bearer ${localStorage.getItem('seckill-token')}`, ...options.headers }
  })
  const result = await response.json()
  if (!response.ok || result.code >= 400) throw new Error(result.message || '请求失败')
  return result
}

function logout() {
  localStorage.removeItem('seckill-token')
  loggedIn.value = false
  showMessage('已退出登录')
}
onBeforeUnmount(() => clearTimeout(clearMessageTimer))
</script>

<template>
  <div class="app-shell">
    <aside class="sidebar" :class="{ open: isSidebarOpen }">
      <div class="brand"><span class="brand-mark"><Music2 :size="20" /></span><span>LiveNow</span></div>
      <nav>
        <a class="nav-item active" href="#discover"><LayoutDashboard :size="19" />发现演出</a>
        <a class="nav-item" href="#events"><Ticket :size="19" />演出活动</a>
        <a class="nav-item" href="#orders"><ShoppingBag :size="19" />我的订单</a>
        <a class="nav-item" href="#members"><UsersRound :size="19" />会员中心</a>
      </nav>
      <div class="sidebar-bottom"><a class="nav-item" href="#support"><ShieldCheck :size="19" />购票须知</a></div>
    </aside>
    <div class="main-wrap">
      <header class="topbar">
        <button class="icon-button mobile-menu" aria-label="打开菜单" @click="isSidebarOpen = !isSidebarOpen"><Menu :size="22" /></button>
        <div class="crumb"><span>演出活动</span><span class="slash">/</span><strong>限时秒杀</strong></div>
        <div class="top-actions"><button class="icon-button" aria-label="消息"><Bell :size="19" /></button><button v-if="loggedIn" class="profile-button" @click="logout"><CircleUserRound :size="20" />已登录 <LogOut :size="15" /></button><button v-else class="login-button" @click="loginOpen = true">登录</button></div>
      </header>
      <main>
        <section class="intro" id="discover"><div><p class="eyebrow">LIVE MUSIC · 2026 SUMMER</p><h1>把今晚，留给现场</h1><p class="intro-copy">热门演出限时开抢。选择场次、票档，准时奔赴这一场心跳。</p></div><div class="countdown"><span>距离本场开抢</span><strong>00 : 18 : 42</strong><small>8 月 16 日 12:00 开始</small></div></section>
        <section class="filters" id="events"><div class="search-field"><Search :size="19" /><input v-model="searchQuery" placeholder="搜索艺人、场馆或城市" /></div><button class="city-filter">上海 <ChevronDown :size="16" /></button><button class="date-filter"><CalendarDays :size="17" />全部日期</button></section>
        <section class="event-panel">
          <div class="event-visual"><div class="visual-noise"></div><div class="event-copy"><p>2026 夏日限定</p><h2>回响之夜<br />城市音乐节</h2><span>RESONANCE LIVE FESTIVAL</span></div><div class="orb orb-one"></div><div class="orb orb-two"></div></div>
          <div class="event-info"><div class="tags"><span>音乐节</span><span>热门</span></div><h2>回响之夜 · 城市音乐节</h2><p class="venue"><Music2 :size="18" />上海国际音乐公园 · 星野舞台</p><p class="time"><Clock3 :size="18" />2026.08.16 周六 19:30</p><div class="stat-row"><div><strong>12,683</strong><span>人想看</span></div><div><strong>3</strong><span>热门票档</span></div><div><strong>4.9</strong><span>观众评分</span></div></div></div>
        </section>
        <section class="selection-section"><div class="section-heading"><div><p class="section-kicker">STEP 01</p><h2>选择演出场次</h2></div><span>共 5 个可售场次</span></div><div class="date-tabs"><button v-for="(date, index) in dates" :key="date.day" :class="{ selected: selectedDate === index }" @click="selectedDate = index"><strong>{{ date.day }}</strong><span>{{ date.week }}</span><em v-if="index === 0">抢</em></button></div></section>
        <section class="ticket-section"><div class="section-heading"><div><p class="section-kicker">STEP 02</p><h2>选择票档</h2></div><span class="stock-hint">库存实时更新中</span></div><div class="ticket-grid"><button v-for="ticket in tickets" :key="ticket.id" class="ticket-card" :class="{ selected: selectedTicket === ticket.id }" @click="selectedTicket = ticket.id"><span class="ticket-name">{{ ticket.name }}</span><strong>¥{{ ticket.price }}</strong><span class="ticket-note">{{ ticket.note }}</span><span class="ticket-stock">仅剩 {{ ticket.stock }} 张</span><i v-if="selectedTicket === ticket.id">已选</i></button></div></section>
        <section class="purchase-bar"><div><p>已选择 <strong>{{ dates[selectedDate].day }} {{ dates[selectedDate].week }}</strong> · <strong>{{ selected.name }}</strong></p><span>含票务服务费，具体以订单页为准</span></div><div class="purchase-controls"><div class="stepper"><button @click="quantity = Math.max(1, quantity - 1)">−</button><span>{{ quantity }}</span><button @click="quantity = Math.min(2, quantity + 1)">+</button></div><button class="seckill-button" @click="openPurchase">立即秒杀 <span>¥{{ total }}</span></button></div></section>
      </main>
    </div>
    <Transition name="fade"><div v-if="message" class="toast">{{ message }}</div></Transition>
    <Transition name="fade"><div v-if="dialogOpen || loginOpen" class="overlay" @click.self="dialogOpen = loginOpen = false"><section class="modal"><button class="close-button" aria-label="关闭" @click="dialogOpen = loginOpen = false"><X :size="20" /></button><template v-if="loginOpen"><p class="section-kicker">ACCOUNT</p><h2>登录账户</h2><p class="modal-subtitle">登录后即可参与演出秒杀</p><input v-model="account.username" placeholder="用户名" /><input v-model="account.password" type="password" placeholder="密码" @keyup.enter="login" /><button class="modal-primary" :disabled="pending" @click="login">{{ pending ? '登录中...' : '登录并继续' }}</button></template><template v-else><p class="section-kicker">ORDER CONFIRMATION</p><h2>确认秒杀订单</h2><div class="confirm-line"><span>{{ selected.name }}</span><strong>¥{{ selected.price }} × {{ quantity }}</strong></div><div class="confirm-line muted"><span>{{ dates[selectedDate].day }} {{ dates[selectedDate].week }} 19:30</span><span>上海国际音乐公园</span></div><div class="order-total"><span>应付金额</span><strong>¥{{ total }}</strong></div><button class="modal-primary" :disabled="pending" @click="submitOrder">{{ pending ? '正在提交...' : '确认参与秒杀' }}</button></template></section></div></Transition>
  </div>
</template>
