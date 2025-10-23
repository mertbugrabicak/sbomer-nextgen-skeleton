# sbomer-nextgen-skeleton
An attempt to lay out the skeleton of the NextGen design for SBOMer as a multi-module Quarkus project in order to see it in action

curl -X POST http://localhost:8080/v1/generate \
-H "Content-Type: application/json" \
-d '{
"sourceType": "errata",
"payload": {
"advisoryId": 12345
}
}'