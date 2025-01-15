**Single call API**

```
curl --location 'http://localhost:8080/api/call' \
--header 'Content-Type: application/json' \
--data '{
    "mobile" : "<phone1>",
    "countryCode" : "+91",
    "message" : "my name is aryak",
    "filename" : "english-4"
}'
```

**Single call Hindi API**

```
curl --location 'http://localhost:8080/api/call' \
--header 'Content-Type: application/json' \
--data '{
    "mobile" : "<phone1>",
    "countryCode" : "+91",
    "message" : "नमस्ते, मेरा नाम आर्यक है।",
    "filename" : "hindi-4",
    "hindi" : true
}'
```

**Bulk call API**

```
curl --location 'http://localhost:8080/api/bulk' \
--header 'Content-Type: application/json' \
--data '{
    "mobile" : "<phone2>,<phone2>",
    "countryCode" : "+91",
    "message" : "my name is aryak",
    "filename" : "english-4"
}'
```
**Swagger**
<img width="1499" alt="Screenshot 2025-01-15 at 16 28 02" src="https://github.com/user-attachments/assets/152ac85a-d417-4b6d-89c7-362e5d5cf134" />
