// =====================
// API Configuration
// =====================
const API_BASE = 'https://canteen-management-system.up.railway.app';

const API = {
  // Customers
  customers: {
    register: (data) => post('/customers/register', data),
    search: (q) => get(`/customers/search?query=${encodeURIComponent(q)}`),
    getByMobile: (m) => get(`/customers/mobile/${m}`),
    getById: (id) => get(`/customers/${id}`),
    getAll: () => get('/customers'),
    outstanding: () => get('/customers/outstanding'),
  },
  // Menu
  menu: {
    getAll: () => get('/menu'),
    getAllAdmin: () => get('/menu/all'),
    search: (q) => get(`/menu/search?query=${encodeURIComponent(q)}`),
    add: (data) => post('/menu', data),
    update: (id, data) => put(`/menu/${id}`, data),
    toggle: (id) => patch(`/menu/${id}/toggle`),
    delete: (id) => del(`/menu/${id}`),
  },
  // Orders
  orders: {
    create: (data) => post('/orders/create', data),
    addItem: (orderId, data) => post(`/orders/${orderId}/items`, data),
    removeItem: (orderId, itemId) => del(`/orders/${orderId}/items/${itemId}`),
    updateItemStatus: (itemId, status) => patch(`/orders/items/${itemId}/status`, { status }),
    getActive: () => get('/orders/active'),
    getById: (id) => get(`/orders/${id}`),
    getByCustomer: (cid) => get(`/orders/customer/${cid}`),
    cancel: (id) => patch(`/orders/${id}/cancel`),
  },
  // Payments
  payments: {
    process: (data) => post('/payments/process', data),
    billing: (cid) => get(`/payments/billing/${cid}`),
    byCustomer: (cid) => get(`/payments/customer/${cid}`),
    ledger: (cid) => get(`/payments/ledger/${cid}`),
  },
  // Reports
  reports: {
    daily: () => get('/reports/daily'),
    dashboard: () => get('/reports/dashboard'),
  },
  // Users
  users: {
    login: (data) => post('/users/login', data),
    getAll: () => get('/users'),
    create: (data) => post('/users', data),
    update: (id, data) => put(`/users/${id}`, data),
    delete: (id) => del(`/users/${id}`),
  },
};

async function get(path) {
  const r = await fetch(API_BASE + path);
  if (!r.ok) throw await r.json();
  return r.json();
}
async function post(path, data) {
  const r = await fetch(API_BASE + path, {
    method: 'POST', headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  if (!r.ok) throw await r.json();
  return r.json();
}
async function put(path, data) {
  const r = await fetch(API_BASE + path, {
    method: 'PUT', headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  if (!r.ok) throw await r.json();
  return r.json();
}
async function patch(path, data = {}) {
  const r = await fetch(API_BASE + path, {
    method: 'PATCH', headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  if (!r.ok) throw await r.json();
  return r.json();
}
async function del(path) {
  const r = await fetch(API_BASE + path, { method: 'DELETE' });
  if (r.status === 204) return {};
  if (!r.ok) throw await r.json();
  return r.json();
}
