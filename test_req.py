import urllib.request, json, urllib.error
try:
    req = urllib.request.Request('http://localhost:8080/api/v1/policies/register-full', data=json.dumps({'workerId': 7, 'zone': 'North', 'tier': 'PRO'}).encode('utf-8'), headers={'Content-Type': 'application/json'})
    print(json.loads(urllib.request.urlopen(req).read().decode('utf-8')))
except urllib.error.HTTPError as e:
    print(json.dumps(json.loads(e.read().decode('utf-8')), indent=2))
