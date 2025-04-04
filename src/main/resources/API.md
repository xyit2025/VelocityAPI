# 如何使用 API

### 请求头
```text
Content-Type: application/json
Authorization: token
```
token 在配置文件里

## 查询所有玩家数量
````text
POST /vc/query
````
请求参数
```json
{
  "page": "[2:3]"
}
```
示例返回
```json
{
  "player_number":"123",
  "players": [
    "player1",
    "player2",
    "player3"
  ]
}
```

## 查询指定玩家
````text
POST /vc/find_player
````
请求参数
```json
{
  "name": "player_name"
}
```
返回
```json
{
  "isOnline": true,
  "server": "server_name"
}
```

## 全服喊话
````text
POST /vc/hh
````
请求参数
```json
{
  "qID": "qq ID",
  "message": "message"
}
```
返回
```200```

## blind
````text
POST /vc/blind
````

请求参数
```json
{
  "qID": "qq ID",
  "code": "code"
}
```
返回
```200```
```403```