const fetch = (...args) => import('node-fetch').then(({default: fetch}) => fetch(...args));
async function test() {
  const res = await fetch('http://26.37.211.123:8080/api/v1/payouts');
  const data = await res.json();
  console.log('PAYOUT KEYS:', Object.keys(data[0] || {}));
  console.log('SAMPLE PAYOUT:', data[0]);
}
test();
