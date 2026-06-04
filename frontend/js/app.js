// =====================
// App State
// =====================
let currentUser = null;
let currentOrder = null;
let currentCustomer = null;
let pollingInterval = null;

// =====================
// Utility
// =====================
function toast(msg, type = 'success') {
  const t = document.getElementById('toast');
  t.textContent = msg;
  t.className = type;
  t.style.display = 'block';
  setTimeout(() => t.style.display = 'none', 3000);
}

function fmt(n) { return '₹' + Number(n || 0).toLocaleString('en-IN'); }

function badgeHtml(status) {
  const map = {
    PENDING: 'badge-pending', IN_PROGRESS: 'badge-progress',
    COMPLETED: 'badge-completed', CLOSED: 'badge-closed', CANCELLED: 'badge-cancelled'
  };
  return `<span class="badge ${map[status] || ''}">${status}</span>`;
}

function itemBadge(status) {
  const map = { PENDING: 'badge-pending', IN_PROGRESS: 'badge-progress', COMPLETED: 'badge-completed' };
  return `<span class="badge ${map[status] || ''}">${status}</span>`;
}

// =====================
// Navigation
// =====================
function navigate(page) {
  document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
  document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
  const p = document.getElementById('page-' + page);
  if (p) p.classList.add('active');
  const n = document.querySelector(`.nav-item[data-page="${page}"]`);
  if (n) n.classList.add('active');

  if (page === 'kitchen') loadKitchen();
  if (page === 'orders') loadActiveOrders();
  if (page === 'reports') loadReports();
  if (page === 'menu-admin') loadAdminMenu();
  if (page === 'users') loadUsers();
}

// =====================
// LOGIN
// =====================
document.getElementById('login-btn').addEventListener('click', async () => {
  const u = document.getElementById('login-username').value;
  const p = document.getElementById('login-password').value;
  const err = document.getElementById('login-error');
  try {
    const user = await API.users.login({ username: u, password: p });
    currentUser = user;
    document.getElementById('login-page').style.display = 'none';
    document.getElementById('app-shell').style.display = 'flex';
    document.getElementById('sidebar-name').textContent = user.fullName;
    document.getElementById('sidebar-role').textContent = user.role;
    document.getElementById('sidebar-avatar').textContent = user.fullName[0].toUpperCase();
    setupNav(user.role);
    navigate('dashboard');
    loadDashboard();
  } catch (e) {
    err.textContent = 'Invalid username or password.';
    err.style.display = 'block';
  }
});

function setupNav(role) {
  document.querySelectorAll('.nav-item[data-roles]').forEach(item => {
    const roles = item.dataset.roles.split(',');
    item.style.display = roles.includes(role) ? '' : 'none';
  });
}

document.getElementById('logout-btn').addEventListener('click', () => {
  currentUser = null;
  document.getElementById('login-page').style.display = 'flex';
  document.getElementById('app-shell').style.display = 'none';
  clearInterval(pollingInterval);
});

// =====================
// DASHBOARD
// =====================
async function loadDashboard() {
  try {
    const d = await API.reports.dashboard();
    document.getElementById('dash-revenue').textContent = fmt(d.todayRevenue);
    document.getElementById('dash-orders').textContent = d.todayOrders;
    document.getElementById('dash-cash').textContent = fmt(d.cashCollection);
    document.getElementById('dash-upi').textContent = fmt(d.upiCollection);
    document.getElementById('dash-pending').textContent = fmt(d.pendingCollection);
    document.getElementById('dash-newcust').textContent = d.newCustomers;
  } catch (e) { console.error(e); }

  clearInterval(pollingInterval);
  pollingInterval = setInterval(loadDashboard, 30000);
}

// =====================
// CUSTOMER SEARCH
// =====================
let customerSearchTimeout = null;

document.getElementById('cust-search-input').addEventListener('input', function () {
  clearTimeout(customerSearchTimeout);
  const q = this.value.trim();
  if (q.length < 2) {
    document.getElementById('cust-dropdown').classList.remove('open');
    return;
  }
  customerSearchTimeout = setTimeout(async () => {
    try {
      const results = await API.customers.search(q);
      const dd = document.getElementById('cust-dropdown');
      dd.innerHTML = '';
      if (!results.length) {
        dd.innerHTML = '<div class="dropdown-item" style="color:var(--text3)">No customers found</div>';
      } else {
        results.forEach(c => {
          const item = document.createElement('div');
          item.className = 'dropdown-item';
          item.innerHTML = `<strong>${c.name}</strong> <span class="id">${c.customerId} · ${c.mobileNumber}</span>`;
          item.onclick = () => selectCustomer(c);
          dd.appendChild(item);
        });
      }
      dd.classList.add('open');
    } catch (e) {}
  }, 300);
});

function selectCustomer(c) {
  currentCustomer = c;
  document.getElementById('cust-search-input').value = c.name;
  document.getElementById('cust-dropdown').classList.remove('open');
  document.getElementById('selected-customer-info').innerHTML = `
    <div class="card" style="margin-top:14px">
      <div style="display:flex;gap:16px;align-items:center">
        <div class="user-avatar" style="width:44px;height:44px;font-size:18px">${c.name[0]}</div>
        <div>
          <div style="font-weight:700;font-size:15px">${c.name}</div>
          <div style="color:var(--text3);font-size:12px">${c.customerId} · ${c.mobileNumber}</div>
          ${c.outstandingBalance > 0 ? `<div style="color:var(--red);font-size:12px;margin-top:2px">Outstanding: ${fmt(c.outstandingBalance)}</div>` : ''}
        </div>
        <button class="btn btn-primary" style="margin-left:auto" onclick="createOrderForCustomer()">Create Order</button>
      </div>
    </div>`;
}

async function createOrderForCustomer() {
  if (!currentCustomer) return;
  try {
    const order = await API.orders.create({ customerId: currentCustomer.customerId, waiterName: currentUser.fullName });
    currentOrder = order;
    toast('Order ' + order.orderId + ' created!');
    navigate('order-entry');
    renderOrderPanel();
    loadMenuGrid();
  } catch (e) { toast(e.error || 'Error creating order', 'error'); }
}

// =====================
// ADD CUSTOMER
// =====================
document.getElementById('add-customer-btn').addEventListener('click', async () => {
  const name = document.getElementById('new-cust-name').value.trim();
  const mobile = document.getElementById('new-cust-mobile').value.trim();
  if (!name || !mobile) return toast('Name and mobile required', 'error');
  try {
    const c = await API.customers.register({ name, mobileNumber: mobile });
    toast('Customer ' + c.customerId + ' registered!');
    document.getElementById('new-cust-name').value = '';
    document.getElementById('new-cust-mobile').value = '';
    selectCustomer(c);
    navigate('customer-search');
  } catch (e) { toast(e.error || 'Registration failed', 'error'); }
});

// =====================
// ORDER ENTRY - MENU
// =====================
let allMenuItems = [];
let menuSearchTimeout = null;

async function loadMenuGrid(query = '') {
  try {
    const items = query ? await API.menu.search(query) : await API.menu.getAll();
    allMenuItems = items;
    renderMenuGrid(items);
  } catch (e) {}
}

function renderMenuGrid(items) {
  const g = document.getElementById('menu-grid');
  g.innerHTML = '';
  items.forEach(item => {
    const c = document.createElement('div');
    c.className = 'menu-card';
    c.innerHTML = `
      <div class="mname">${item.name}</div>
      <div class="mprice">${fmt(item.price)}</div>
      <div class="mcat">${item.category || ''}</div>`;
    c.onclick = () => openQtyModal(item);
    g.appendChild(c);
  });
}

document.getElementById('menu-search-input').addEventListener('input', function () {
  clearTimeout(menuSearchTimeout);
  const q = this.value.trim();
  menuSearchTimeout = setTimeout(() => loadMenuGrid(q), 250);
});

function openQtyModal(item) {
  document.getElementById('qty-modal-name').textContent = item.name;
  document.getElementById('qty-modal-price').textContent = fmt(item.price);
  document.getElementById('qty-input').value = 1;
  document.getElementById('qty-modal').classList.add('open');
  document.getElementById('qty-confirm-btn').onclick = () => addItemToCurrentOrder(item);
}

document.getElementById('qty-cancel-btn').addEventListener('click', () => {
  document.getElementById('qty-modal').classList.remove('open');
});

async function addItemToCurrentOrder(item) {
  if (!currentOrder) return toast('No active order', 'error');
  const qty = parseInt(document.getElementById('qty-input').value);
  if (qty < 1) return;
  try {
    const order = await API.orders.addItem(currentOrder.orderId, { menuItemId: item.id, quantity: qty });
    currentOrder = order;
    renderOrderPanel();
    document.getElementById('qty-modal').classList.remove('open');
    toast(`${item.name} x${qty} added`);
  } catch (e) { toast(e.error || 'Error adding item', 'error'); }
}

function renderOrderPanel() {
  if (!currentOrder) return;
  document.getElementById('order-panel-id').textContent = currentOrder.orderId;
  document.getElementById('order-panel-customer').textContent = currentOrder.customer?.name || '';
  const list = document.getElementById('order-items-list');
  list.innerHTML = '';
  const items = currentOrder.items || [];
  items.forEach(item => {
    const row = document.createElement('div');
    row.className = 'order-item-row';
    row.innerHTML = `
      <span class="iname">${item.menuItem?.name}</span>
      <input class="iqty" type="number" min="1" value="${item.quantity}" onchange="updateQty('${currentOrder.orderId}',${item.id},this.value,${item.menuItem?.id})">
      <span class="isubtotal">${fmt(item.subtotal)}</span>
      <span class="idel" onclick="removeOrderItem('${currentOrder.orderId}',${item.id})">✕</span>`;
    list.appendChild(row);
  });
  document.getElementById('order-total').textContent = fmt(currentOrder.totalAmount);
}

async function removeOrderItem(orderId, itemId) {
  try {
    const order = await API.orders.removeItem(orderId, itemId);
    currentOrder = order;
    renderOrderPanel();
  } catch (e) { toast(e.error || 'Error', 'error'); }
}

document.getElementById('proceed-billing-btn').addEventListener('click', () => {
  if (!currentOrder) return;
  navigate('billing');
  loadBillingInfo();
});

// =====================
// BILLING & PAYMENT
// =====================
async function loadBillingInfo() {
  if (!currentOrder || !currentCustomer) return;
  try {
    const info = await API.payments.billing(currentCustomer.customerId);
    const prev = info.outstandingBalance || 0;
    const curr = currentOrder.totalAmount || 0;
    const total = curr + prev;

    document.getElementById('bill-customer-name').textContent = info.customerName;
    document.getElementById('bill-order-id').textContent = currentOrder.orderId;
    document.getElementById('bill-current').textContent = fmt(curr);
    document.getElementById('bill-previous').textContent = fmt(prev);
    document.getElementById('bill-total').textContent = fmt(total);
    document.getElementById('payment-amount').value = total;

    const itemsHtml = (currentOrder.items || []).map(i =>
      `<tr><td>${i.menuItem?.name}</td><td>x${i.quantity}</td><td style="text-align:right">${fmt(i.subtotal)}</td></tr>`
    ).join('');
    document.getElementById('bill-items-table').innerHTML = itemsHtml;
  } catch (e) { console.error(e); }
}

document.getElementById('pay-btn').addEventListener('click', async () => {
  const amount = parseFloat(document.getElementById('payment-amount').value);
  const method = document.getElementById('payment-method').value;
  if (!amount || !currentOrder) return toast('Invalid payment', 'error');
  try {
    const payment = await API.payments.process({
      orderId: currentOrder.orderId,
      amountPaid: amount,
      paymentMethod: method,
    });
    const pending = payment.pendingAmount;
    toast(`Payment successful! ${pending > 0 ? 'Pending: ' + fmt(pending) : 'Fully paid'}`, 'success');
    currentOrder = null;
    currentCustomer = null;
    navigate('customer-search');
    document.getElementById('cust-search-input').value = '';
    document.getElementById('selected-customer-info').innerHTML = '';
  } catch (e) { toast(e.error || 'Payment failed', 'error'); }
});

// =====================
// KITCHEN DASHBOARD
// =====================
async function loadKitchen() {
  try {
    const orders = await API.orders.getActive();
    const grid = document.getElementById('kitchen-grid');
    grid.innerHTML = '';
    if (!orders.length) {
      grid.innerHTML = '<div style="color:var(--text3);padding:20px">No active orders</div>';
      return;
    }
    orders.forEach(order => {
      const card = document.createElement('div');
      card.className = 'kitchen-card';
      const itemsHtml = (order.items || []).map(item => `
        <div class="kitchen-item">
          <span class="kitem-name">${item.menuItem?.name}</span>
          <span class="kqty">x${item.quantity}</span>
          ${itemBadge(item.status)}
          ${item.status !== 'COMPLETED' ? `<button class="btn btn-sm btn-success" onclick="markItemDone(${item.id})">Done</button>` : ''}
        </div>`).join('');
      card.innerHTML = `
        <div class="kheader">
          <div>
            <div class="kname">${order.customer?.name}</div>
            <div class="korderid">${order.orderId}</div>
          </div>
          ${badgeHtml(order.status)}
        </div>
        ${itemsHtml}`;
      grid.appendChild(card);
    });
  } catch (e) { console.error(e); }
}

async function markItemDone(itemId) {
  try {
    await API.orders.updateItemStatus(itemId, 'COMPLETED');
    toast('Item marked completed');
    loadKitchen();
  } catch (e) { toast(e.error || 'Error', 'error'); }
}

// =====================
// ACTIVE ORDERS LIST
// =====================
async function loadActiveOrders() {
  try {
    const orders = await API.orders.getActive();
    const tbody = document.getElementById('active-orders-tbody');
    tbody.innerHTML = '';
    orders.forEach(order => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${order.orderId}</td>
        <td>${order.customer?.name}<br><span style="font-size:11px;color:var(--text3)">${order.customer?.mobileNumber}</span></td>
        <td>${(order.items || []).map(i => `${i.menuItem?.name} x${i.quantity}`).join(', ')}</td>
        <td>${fmt(order.totalAmount)}</td>
        <td>${badgeHtml(order.status)}</td>
        <td><span style="font-size:12px;color:var(--text3)">${new Date(order.createdAt).toLocaleTimeString()}</span></td>`;
      tbody.appendChild(tr);
    });
  } catch (e) { console.error(e); }
}

// =====================
// REPORTS
// =====================
async function loadReports() {
  try {
    const r = await API.reports.daily();
    document.getElementById('rep-total').textContent = fmt(r.revenue?.totalSales);
    document.getElementById('rep-cash').textContent = fmt(r.revenue?.cashCollection);
    document.getElementById('rep-upi').textContent = fmt(r.revenue?.gpayCollection);
    document.getElementById('rep-pending').textContent = fmt(r.revenue?.pendingCollection);
    document.getElementById('rep-orders').textContent = r.orders?.totalOrders || 0;
    document.getElementById('rep-completed').textContent = r.orders?.completedOrders || 0;
    document.getElementById('rep-newcust').textContent = r.customers?.newCustomersToday || 0;
    document.getElementById('rep-outstanding').textContent = r.customers?.customersWithOutstanding || 0;

    const tbody = document.getElementById('dish-report-tbody');
    tbody.innerHTML = '';
    (r.dishWiseSales || []).forEach(d => {
      const tr = document.createElement('tr');
      tr.innerHTML = `<td>${d.dish}</td><td>${d.quantity}</td>`;
      tbody.appendChild(tr);
    });
  } catch (e) { console.error(e); }
}

// =====================
// ADMIN - MENU
// =====================
async function loadAdminMenu() {
  try {
    const items = await API.menu.getAllAdmin();
    const tbody = document.getElementById('admin-menu-tbody');
    tbody.innerHTML = '';
    items.forEach(item => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${item.id}</td>
        <td>${item.name}</td>
        <td>${item.category || '-'}</td>
        <td>${fmt(item.price)}</td>
        <td><span class="badge ${item.available ? 'badge-completed' : 'badge-cancelled'}">${item.available ? 'Active' : 'Hidden'}</span></td>
        <td>
          <button class="btn btn-sm btn-secondary" onclick="openEditMenu(${JSON.stringify(item).replace(/"/g,"'")})">Edit</button>
          <button class="btn btn-sm btn-secondary" onclick="toggleMenu(${item.id})">${item.available ? 'Hide' : 'Show'}</button>
          <button class="btn btn-sm btn-danger" onclick="deleteMenu(${item.id})">Delete</button>
        </td>`;
      tbody.appendChild(tr);
    });
  } catch (e) { console.error(e); }
}

document.getElementById('add-menu-btn').addEventListener('click', () => {
  document.getElementById('menu-modal-title').textContent = 'Add Menu Item';
  document.getElementById('menu-form-id').value = '';
  document.getElementById('menu-form-name').value = '';
  document.getElementById('menu-form-price').value = '';
  document.getElementById('menu-form-category').value = '';
  document.getElementById('menu-modal').classList.add('open');
});

function openEditMenu(item) {
  document.getElementById('menu-modal-title').textContent = 'Edit Menu Item';
  document.getElementById('menu-form-id').value = item.id;
  document.getElementById('menu-form-name').value = item.name;
  document.getElementById('menu-form-price').value = item.price;
  document.getElementById('menu-form-category').value = item.category || '';
  document.getElementById('menu-modal').classList.add('open');
}

document.getElementById('menu-form-save').addEventListener('click', async () => {
  const id = document.getElementById('menu-form-id').value;
  const data = {
    name: document.getElementById('menu-form-name').value,
    price: parseFloat(document.getElementById('menu-form-price').value),
    category: document.getElementById('menu-form-category').value,
    available: true,
  };
  try {
    if (id) await API.menu.update(id, data);
    else await API.menu.add(data);
    document.getElementById('menu-modal').classList.remove('open');
    toast('Menu item saved');
    loadAdminMenu();
  } catch (e) { toast(e.error || 'Error', 'error'); }
});

document.getElementById('menu-form-cancel').addEventListener('click', () => {
  document.getElementById('menu-modal').classList.remove('open');
});

async function toggleMenu(id) {
  try { await API.menu.toggle(id); loadAdminMenu(); } catch (e) { toast('Error', 'error'); }
}
async function deleteMenu(id) {
  if (!confirm('Delete this item?')) return;
  try { await API.menu.delete(id); loadAdminMenu(); toast('Deleted'); } catch (e) { toast('Error', 'error'); }
}

// =====================
// ADMIN - USERS
// =====================
async function loadUsers() {
  try {
    const users = await API.users.getAll();
    const tbody = document.getElementById('users-tbody');
    tbody.innerHTML = '';
    users.forEach(u => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${u.id}</td>
        <td>${u.username}</td>
        <td>${u.fullName}</td>
        <td>${u.role}</td>
        <td><span class="badge ${u.active ? 'badge-completed' : 'badge-cancelled'}">${u.active ? 'Active' : 'Inactive'}</span></td>
        <td><button class="btn btn-sm btn-danger" onclick="deleteUser(${u.id})">Delete</button></td>`;
      tbody.appendChild(tr);
    });
  } catch (e) {}
}

document.getElementById('add-user-btn').addEventListener('click', () => {
  document.getElementById('user-modal').classList.add('open');
});

document.getElementById('user-form-save').addEventListener('click', async () => {
  const data = {
    username: document.getElementById('user-form-username').value,
    password: document.getElementById('user-form-password').value,
    fullName: document.getElementById('user-form-fullname').value,
    role: document.getElementById('user-form-role').value,
    active: true,
  };
  try {
    await API.users.create(data);
    document.getElementById('user-modal').classList.remove('open');
    toast('User created');
    loadUsers();
  } catch (e) { toast(e.error || 'Error', 'error'); }
});

document.getElementById('user-form-cancel').addEventListener('click', () => {
  document.getElementById('user-modal').classList.remove('open');
});

async function deleteUser(id) {
  if (!confirm('Delete this user?')) return;
  try { await API.users.delete(id); loadUsers(); toast('Deleted'); } catch (e) { toast('Error', 'error'); }
}

// =====================
// NAV CLICKS
// =====================
document.querySelectorAll('.nav-item[data-page]').forEach(item => {
  item.addEventListener('click', () => navigate(item.dataset.page));
});

// Refresh kitchen every 10s
setInterval(() => {
  const kitchen = document.getElementById('page-kitchen');
  if (kitchen && kitchen.classList.contains('active')) loadKitchen();
  const orders = document.getElementById('page-orders');
  if (orders && orders.classList.contains('active')) loadActiveOrders();
}, 10000);
