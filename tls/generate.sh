#!/usr/bin/env bash
# Author: mskacelik

set -euo pipefail

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PASSWORD="changeit"
DAYS=3650
CN="localhost"

openssl req -newkey rsa:4096 -nodes \
  -keyout "$DIR/ca-key.pem" \
  -x509 -days "$DAYS" \
  -out "$DIR/ca-cert.pem" \
  -subj "/CN=edgy-benchmark-ca"

openssl req -newkey rsa:4096 -nodes \
  -keyout "$DIR/server-key.pem" \
  -out "$DIR/server.csr" \
  -subj "/CN=$CN"

openssl x509 -req \
  -in "$DIR/server.csr" \
  -CA "$DIR/ca-cert.pem" \
  -CAkey "$DIR/ca-key.pem" \
  -CAcreateserial \
  -out "$DIR/server-cert.pem" \
  -days "$DAYS" \
  -extfile <(printf "subjectAltName=DNS:localhost,IP:127.0.0.1,DNS:heroes-service")

rm -f "$DIR/truststore.p12"
keytool -importcert \
  -noprompt \
  -alias ca \
  -file "$DIR/ca-cert.pem" \
  -storetype PKCS12 \
  -storepass "$PASSWORD" \
  -keystore "$DIR/truststore.p12"

rm -f "$DIR/server.csr" "$DIR/ca-cert.srl"