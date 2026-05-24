import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/login',
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/login/LoginView.vue'),
    },
    {
      path: '/oauth/callback',
      name: 'OAuthCallback',
      component: () => import('../views/login/OAuthCallback.vue'),
    },
    {
      path: '/user',
      component: () => import('../components/layout/UserLayout.vue'),
      meta: { role: 'USER' },
      children: [
        { path: 'home', name: 'UserHome', component: () => import('../views/user/UserHome.vue') },
        { path: 'orders', name: 'UserOrderList', component: () => import('../views/user/UserOrderList.vue') },
        { path: 'order/:id', name: 'UserOrderEdit', component: () => import('../views/user/UserOrderEdit.vue'), props: true },
      ],
    },
    {
      path: '/admin',
      component: () => import('../components/layout/AdminLayout.vue'),
      meta: { role: 'ADMIN' },
      children: [
        { path: 'home', name: 'AdminHome', component: () => import('../views/admin/AdminHome.vue') },
        { path: 'orders', name: 'AdminOrderList', component: () => import('../views/admin/AdminOrderList.vue') },
        { path: 'orders/:id', name: 'AdminOrderDetail', component: () => import('../views/admin/AdminOrderDetail.vue'), props: true },
        { path: 'manage', name: 'AdminManagement', component: () => import('../views/admin/AdminManagement.vue') },
      ],
    },
    {
      path: '/super',
      component: () => import('../components/layout/SuperAdminLayout.vue'),
      meta: { role: 'SUPER_ADMIN' },
      children: [
        { path: 'manage', name: 'SuperAdminManage', component: () => import('../views/super/SuperAdminManage.vue') },
      ],
    },
  ],
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  if (to.path === '/login' || to.path.startsWith('/oauth/')) {
    if (token) {
      if (role === 'SUPER_ADMIN') next('/super/manage')
      else if (role === 'ADMIN') next('/admin/home')
      else next('/user/home')
    } else {
      next()
    }
    return
  }

  if (!token) {
    next('/login')
    return
  }

  const requiredRole = to.matched[to.matched.length - 1]?.meta?.role as string | undefined
  if (requiredRole && requiredRole !== role) {
    if (role === 'SUPER_ADMIN') next('/super/manage')
    else if (role === 'ADMIN') next('/admin/home')
    else next('/user/home')
    return
  }

  next()
})

export default router
