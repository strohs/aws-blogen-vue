#!/usr/bin/env bash
# invokes a lambda function synchronously (because of --invocation-type RequestResponse)
aws lambda invoke \
--invocation-type RequestResponse \
--function-name SimpleStringExample \
--region us-east-1 \
--payload request.txt \
outputfile.txt