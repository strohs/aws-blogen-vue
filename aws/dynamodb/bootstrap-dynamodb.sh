#!/usr/bin/env bash
# bootstraps the Blogen dynamodb table with default Categories and the avatar file names used by Blogen
aws dynamodb batch-write-item --request-items file://blogen-data.json