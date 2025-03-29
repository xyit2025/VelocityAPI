# 如何使用 API

### 请求头
```text
Content-Type: application/json
Authorization: token
```
token 在配置文件里

## 查询所有玩家数量
````text
GET /vc/query
````
示例返回
```json
{
  "player_number":"123"
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
  "message": "message"
}
```
返回
```200```