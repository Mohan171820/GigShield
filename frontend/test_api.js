

async function testApi() {
  const API_BASE = 'http://26.37.211.123:8080';

  // Test 1: Worker Login
  console.log('Testing Worker Login...');
  try {
    const res = await fetch(`${API_BASE}/api/v1/workers/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'Origin': 'http://localhost:3000' },
      body: JSON.stringify({ workerId: 1, id: 1, password: 'password123' })
    });
    console.log('Login Status:', res.status);
    console.log('Login Body:', await res.text());
  } catch (err) {
    console.error('Login Error:', err);
  }

  // Test 2: Policy Registration
  console.log('\nTesting Policy Registration...');
  try {
    const res = await fetch(`${API_BASE}/api/v1/policies/register-full`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'Origin': 'http://localhost:3000' },
      body: JSON.stringify({
        workerId: 1,
        policyType: 'INCOME_PROTECTION',
        coverageAmount: 3500,
        premiumPaid: 99,
        validityDays: 30
      })
    });
    console.log('Policy Status:', res.status);
    console.log('Policy Body:', await res.text());
  } catch (err) {
    console.error('Policy Error:', err);
  }
}

testApi();
