// ============================================================
// InSureGig — Shared JS Utilities & API Layer (app.js)
// ============================================================

const API_BASE = 'https://gigshield-1-34yi.onrender.com';

// ── API helpers ──────────────────────────────────────────────
async function apiGet(path) {
  const res = await fetch(`${API_BASE}${path}`);
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    const e = new Error(err.message || `HTTP ${res.status}`);
    e.status = res.status;
    throw e;
  }
  return res.json();
}

async function apiPost(path, body) {
  const res = await fetch(`${API_BASE}${path}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    console.group(`❌ API Error: ${res.status} [${path}]`);
    console.error('Request Body:', body);
    console.error('💬 Error Details:', JSON.stringify(err, null, 2));
    console.groupEnd();
    const e = new Error(err.message || `HTTP ${res.status}`);
    e.status = res.status;
    throw e;
  }
  return res.json();
}

async function apiPostRaw(path) {
  const res = await fetch(`${API_BASE}${path}`, { method: 'POST' });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    console.group(`❌ API Error: ${res.status} [${path}]`);
    console.error('💬 Error Details:', JSON.stringify(err, null, 2));
    console.groupEnd();
    const e = new Error(err.message || `HTTP ${res.status}`);
    e.status = res.status;
    throw e;
  }
  return res.json();
}

// ── URL helpers ──────────────────────────────────────────────
function getParam(name) {
  return new URLSearchParams(location.search).get(name);
}

// ── Date formatters ──────────────────────────────────────────
function fmtDate(iso) {
  if (!iso) return '—';
  return new Date(iso).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
}
function fmtTime(iso) {
  if (!iso) return '—';
  return new Date(iso).toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit' });
}

// ── Status badge ─────────────────────────────────────────────
function statusBadge(status) {
  if (!status) return '';
  const s = status.toUpperCase();
  const MAP = {
    APPROVED:  ['green',  'Approved'],
    ACCEPTED:  ['green',  'Accepted'],
    ACTIVE:    ['green',  'Active'],
    PAID:      ['green',  'Paid'],
    REJECTED:  ['red',    'Rejected'],
    BLOCKED:   ['red',    'Blocked'],
    INITIATED:     ['amber',  'Processing'],
    PENDING:       ['amber',  'Pending'],
    ML_PROCESSING: ['amber',  '🤖 ML Deciding...'],
    VERIFIED:      ['green',  'ML Verified'],
    HIGH:          ['red',    'High'],
    MEDIUM:    ['amber',  'Medium'],
    LOW:       ['green',  'Low'],
  };
  const [c, l] = MAP[s] || ['gray', status];
  return `<span class="badge badge-${c}">${l}</span>`;
}

// ── Feature label map ────────────────────────────────────────
const FEATURE_LABELS = {
  rain_mm:               'Rainfall Level (mm)',
  aqi_value:             'Air Quality Index',
  tenure_weeks:          'Weeks Delivering',
  weekly_active_hours:   'Hours / Week',
  prev_claims_30d:       'Recent Claims (30d)',
  offline_pattern_score: 'Activity Score',
  temperature:           'Temperature (°C)',
  wind_speed:            'Wind Speed (km/h)',
  humidity:              'Humidity (%)',
  delivery_count:        'Deliveries Done',
};
function featureLabel(key) {
  return FEATURE_LABELS[key] || key.replace(/_/g, ' ').replace(/\b\w/g, c => c.toUpperCase());
}

// ── Skeleton HTML ────────────────────────────────────────────
function skeletonLines(n = 3, widths) {
  return Array.from({ length: n }, (_, i) => {
    const w = widths ? widths[i] : (i % 2 === 0 ? '90%' : '65%');
    return `<div class="skeleton sk-line" style="width:${w}"></div>`;
  }).join('');
}

// ── Searchable City Helper ──────────────────────────────────
const INDIAN_CITIES = [
  "Mumbai", "Delhi", "Bengaluru", "Ahmedabad", "Hyderabad", "Chennai", "Kolkata", "Pune", "Jaipur", "Lucknow", "Kanpur", "Nagpur", "Indore", "Thane", "Bhopal", "Visakhapatnam", "Pimpri-Chinchwad", "Patna", "Vadodara", "Ghaziabad", "Ludhiana", "Coimbatore", "Agra", "Madurai", "Nashik", "Vijayawada", "Faridabad", "Meerut", "Rajkot", "Kalyan-Dombivli", "Vasai-Virar", "Varanasi", "Srinagar", "Aurangabad", "Dhanbad", "Amritsar", "Navi Mumbai", "Prayagraj", "Ranchi", "Haora", "Jabalpur", "Gwalior", "Raipur", "Jodhpur", "Bareilly", "Kota", "Chandigarh", "Guwahati", "Solapur", "Hubballi-Dharwad", "Tiruchirappalli", "Tiruppur", "Moradabad", "Mysuru", "Thiruvananthapuram", "Bhiwandi", "Saharanpur", "Guntur", "Amravati", "Bikaner", "Noida", "Jamshedpur", "Bhilai", "Cuttack", "Firozabad", "Kochi", "Nellore", "Bhavnagar", "Dehradun", "Durgapur", "Asansol", "Rourkela", "Nanded", "Kolhapur", "Ajmer", "Akola", "Gulbarga", "Jamnagar", "Ujjain", "Loni", "Siliguri", "Jhansi", "Ulhasnagar", "Jammu", "Sangli-Miraj & Kupwad", "Mangaluru", "Erode", "Belgaum", "Ambattur", "Tirunelveli", "Malegaon", "Gaya", "Jalgaon", "Udaipur", "Maheshtala", "Davanagere", "Kozhikode", "Akurnool", "Rajpur Sonarpur", "Rajahmundry", "Bokaro", "South Dumdum", "Bellary", "Patiala", "Gopalpur", "Agartala", "Bhagalpur", "Muzaffarnagar", "Bhatpara", "Panihati", "Latur", "Dhule", "Tirupati", "Rohtak", "Korba", "Bhilwara", "Berhampur", "Muzaffarpur", "Ahmednagar", "Mathura", "Kollam", "Avadi", "Kadapa", "Kamarhati", "Sambalpur", "Bilaspur", "Shahjahanpur", "Satara", "Bijapur", "Rampur", "Shivamogga", "Chandrapur", "Junagadh", "Thrissur", "Alwar", "Siwan", "Khowai", "Bongaigaon", "Jaunpur", "Ayodhya"
].sort();

// ── Searchable Zone Helpers ──────────────────────────────────
const CITY_ZONES = {
  BANGALORE: ["HSR Layout", "Koramangala", "Indiranagar", "Whitefield", "Jayanagar", "Electronic City", "Marathahalli", "BTM Layout", "Banaswadi", "Hennur", "Malleshwaram", "Rajajinagar", "Hebbal", "Yelahanka", "Kammanahalli"],
  CHENNAI:   ["Anna Nagar", "T. Nagar", "Adyar", "Velachery", "Mylapore", "Nungambakkam", "Sholinganallur", "Porur", "Chromepet", "Tambaram", "Guindy", "Royapettah", "Besant Nagar", "Kilpauk", "Ambattur"],
  DELHI:     ["Connaught Place", "Hauz Khas", "Saket", "Dwarka", "Rohini", "Karol Bagh", "Vasant Kunj", "Lajpat Nagar", "Janakpuri", "Model Town", "Civil Lines", "Punjabi Bagh", "Mayur Vihar", "Pitampura", "Okhla"],
  HYDERABAD: ["Jubilee Hills", "Banjara Hills", "Gachibowli", "Madhapur", "Kondapur", "Kukatapally", "Secunderabad", "Himayatnagar", "Banjara Hills", "Dilsukhnagar", "Miyapur", "Begumpet", "Somajiguda"],
  MUMBAI:    ["Andheri", "Bandra", "Colaba", "Worli", "Borivali", "Powai", "Juhu", "Malad", "Kandivali", "Goregaon", "Wadala", "Chembur", "Versova", "Dadar", "Lower Parel"],
};

/**
 * Initializes a searchable city dropdown.
 * @param {string} containerId - ID of the container element.
 * @param {string} inputId - ID for the generated hidden input.
 * @param {string} defaultValue - Optional initial value.
 * @param {Function} onChange - Optional callback when city changes.
 */
function initCitySearch(containerId, inputId, defaultValue = '', onChange = null) {
  const container = document.getElementById(containerId);
  if (!container) return;

  container.innerHTML = `
    <div class="city-search-box">
      <input type="text" class="city-search-input" placeholder="Search city..." autocomplete="off">
      <input type="hidden" id="${inputId}" value="${defaultValue}">
      <div class="city-results" style="display:none"></div>
    </div>
  `;

  const searchInput = container.querySelector('.city-search-input');
  const hiddenInput = container.querySelector(`#${inputId}`);
  const resultsBox = container.querySelector('.city-results');

  if (defaultValue) {
    searchInput.value = defaultValue.charAt(0) + defaultValue.slice(1).toLowerCase();
  }

  function filter(query) {
    const q = query.toLowerCase().trim();
    if (!q) return INDIAN_CITIES;
    return INDIAN_CITIES.filter(c => c.toLowerCase().includes(q));
  }

  function renderResults(list) {
    if (list.length === 0) {
      resultsBox.innerHTML = '<div class="city-item disabled">No results found</div>';
    } else {
      resultsBox.innerHTML = list.slice(0, 50).map(c => `
        <div class="city-item" data-value="${c.toUpperCase()}">${c}</div>
      `).join('');
    }
    resultsBox.style.display = 'block';
  }

  searchInput.addEventListener('focus', () => {
    renderResults(filter(searchInput.value));
  });

  searchInput.addEventListener('input', (e) => {
    renderResults(filter(e.target.value));
  });

  document.addEventListener('click', (e) => {
    if (!container.contains(e.target)) {
      resultsBox.style.display = 'none';
    }
  });

  resultsBox.addEventListener('click', (e) => {
    const item = e.target.closest('.city-item');
    if (!item || item.classList.contains('disabled')) return;

    const val = item.getAttribute('data-value');
    const label = item.textContent;

    hiddenInput.value = val;
    searchInput.value = label;
    resultsBox.style.display = 'none';

    if (onChange) onChange(val);
  });
}

/**
 * Initializes a searchable zone dropdown.
 */
function initZoneSearch(containerId, inputId, city = 'BANGALORE', defaultValue = '', onChange = null) {
  const container = document.getElementById(containerId);
  if (!container) return;

  const zones = CITY_ZONES[city.toUpperCase()] || ["Area 1", "Area 2", "Area 3"]; // Fallback

  container.innerHTML = `
    <div class="city-search-box">
      <input type="text" class="city-search-input" placeholder="Search zone in ${city}..." autocomplete="off">
      <input type="hidden" id="${inputId}" value="${defaultValue}">
      <div class="city-results" style="display:none"></div>
    </div>
  `;

  const searchInput = container.querySelector('.city-search-input');
  const hiddenInput = container.querySelector(`#${inputId}`);
  const resultsBox = container.querySelector('.city-results');

  if (defaultValue) {
    searchInput.value = defaultValue;
  }

  function filter(query) {
    const q = query.toLowerCase().trim();
    if (!q) return zones;
    return zones.filter(z => z.toLowerCase().includes(q));
  }

  function renderResults(list) {
    if (list.length === 0) {
      resultsBox.innerHTML = '<div class="city-item disabled">No results found</div>';
    } else {
      resultsBox.innerHTML = list.map(z => `
        <div class="city-item" data-value="${z}">${z}</div>
      `).join('');
    }
    resultsBox.style.display = 'block';
  }

  searchInput.addEventListener('focus', () => renderResults(filter(searchInput.value)));
  searchInput.addEventListener('input', (e) => renderResults(filter(e.target.value)));

  document.addEventListener('click', (e) => {
    if (!container.contains(e.target)) resultsBox.style.display = 'none';
  });

  resultsBox.addEventListener('click', (e) => {
    const item = e.target.closest('.city-item');
    if (!item || item.classList.contains('disabled')) return;

    const val = item.getAttribute('data-value');
    hiddenInput.value = val;
    searchInput.value = val;
    resultsBox.style.display = 'none';

    if (onChange) onChange(val);
  });
}
