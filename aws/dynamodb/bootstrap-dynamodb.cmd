:: bootstraps the Blogen dynamodb table with Categories and the avatar file names used by Blogen
aws dynamodb batch-write-item --request-items file://blogen-data.json