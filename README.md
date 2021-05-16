# Learn Reactive Spring

## Test
```sh
# GET All Items
crul http://localhost:8081/client/retrieve
curl http://localhost:8081/client/exchange

# GET a Single Item
curl http://localhost:8081/client/retrieve/singleItem
curl http://localhost:8081/client/exchange/singleItem

# POST
curl -d '{"id":null, "description": "Google Nest", "price": 199.99}' -H "Content-Type:application/json" -X POST http://localhost:8081/client/createItem

# PUT
curl -d '{"id":null, "description": "Beats Headphones", "price": 14.99}' -H "Content-Type:application/json" -X PUT http://localhost:8081/client/updateItem/ABC 

# DELETE
curl -X DELETE http://localhost:8081/client/deleteItem/ABC
```