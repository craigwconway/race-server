#!/usr/bin/env python

import hashlib
import hmac
import requests
import sys

if len(sys.argv) < 4:
  print("Usage:\n\t{:s} server secret userid".format(sys.argv[0]))
  sys.exit()

server = sys.argv[1]
secret = sys.argv[2]
userid = int(sys.argv[3])

print("faking for server {:s} with secret {:s}".format(server, secret))
print("faking userid {:d}".format(userid))

msg = '{"delta":{"users":['+str(userid)+']}}'
dig = hmac.new(secret, msg=msg, digestmod=hashlib.sha256).digest()
signature = "".join("{:02x}".format(ord(c)) for c in dig)

r = requests.post("https://{:s}/bibs-server/dropbox/webhook".format(server), headers={'X-Dropbox-Signature': signature}, data=msg)
print(r.status_code)
print(r.text)
