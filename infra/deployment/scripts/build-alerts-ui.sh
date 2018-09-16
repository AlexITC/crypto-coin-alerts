#!/bin/bash
set -e
cd ../../alerts-ui/ && ng build --prod && zip -r web.zip dist/* && cd -
mv ../../alerts-ui/web.zip web.zip
