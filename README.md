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
  "server": "server name",
  "page": "0"
}
```
示例返回
```json
{
  "total_page": "int page",
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

````text
POST /vc/find_player_by_qid
````
请求参数
```json
{
  "name": "qid"
}
```
返回
```json
{
  "isOnline": true,
  "server": "server_name"
}
```
```404```: 没有指定qid对应的玩家

## 全服喊话
````text
POST /vc/hh
````
请求参数
```json
{
  "qqID": "qq ID",
  "message": "message"
}
```
返回  
```200``` : 成功  
返回余额  

```403``` : 玩家不存在  

```429``` : 请求过快  
返回剩余毫秒数  

```409``` : 余额不足


## bind
````text
POST /vc/blind
````

请求参数
```json
{
  "qqID": "qq ID",
  "code": "code"
}
```
返回
```200```
```403```