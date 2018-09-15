#!/bin/bash
set -e
cd ../../alerts-server/ && sbt dist && cd -
cp ../../alerts-server/target/universal/crypto-coin-alerts-0.1-SNAPSHOT.zip app.zip
