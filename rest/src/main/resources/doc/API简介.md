# TUNNEL RESTful API使用手册

本手册详细描述 Restful API的使用规范，并提供所有API的详细定义。

### 服务器地址

```$xslt
http://api.syscxp.com/tunnel/v1
```

### 术语表

本文档涉及的一些常用术语如下：

|术语|中文|说明|
|---|---|---|
|Tunnel|云专线||
|Interface|物理接口||
|Endpoint|连接点||


### HTTP方法

当前API只支持GET：

### 参数

仅Query String 传参方式。

**Query String传参**

```$xslt
http://api.syscxp.com/tunnel/v1?condition=state=Running
```

### HTTP Headers

当前API使用如下自定义HTTP Headers：


<a name="X-Job-UUID">**X-Job-UUID**</a>

对于[异步API](#async_api)，可以通过`X-Job-UUID` HTTP Header来指定该API Job的UUID，
例如：
```$xslt
X-Job-UUID: d825b1a26f4e474b8c59306081920ff2
```

如果未指定该HTTP Header，自动为API Job生成一个UUID。

>注意：X-Job-UUID必须为一个v4版本的UUID（即随机UUID）字符串去掉连接符`-`。非法的字符串返回一个400 Bad Request的错误。

<a name="X-Web-Hook">**X-Web-Hook**</a>

对于[异步API](#async_api)，可以通过`X-Web-Hook` HTTP Header指定一个回调URL用于接收API
返回。通过使用回调URL的方法，调用者可以避免使用轮询去查询一个异步API的执行结果。举例：

```$xslt
X-Web-Hook: http://localhost:5000/api-callback
```

**X-Job-Success**

当使用了`X-Web-Hook`回调的方式获取异步API结果时，ZStack推送给回调URL的HTTP Post请求中会
包含`X-Job-Success` HTTP Header指明该异步API的执行结果是成功还是失败。例如：

```$xslt
X-Job-Success: true
```

当值为*true*时执行成功，为*false*时执行失败。

### HTTP返回码 (HTTP Status Code)

#### 200

API执行成功。

#### 202

API请求已被Syscxp接受，用户需要通过轮询或Web Hook的方式获取API结果。该返回码只在调用异步API时出现。

#### 400

API请求未包含必要的参数或包含了非法的参数。

#### 404

URL不存在，通常是指定了错误的API URL。
如果访问的URL是异步API返回的轮询地址，表示该轮询地址已经过期。

#### 405

API调用使用了错误的HTTP方法。

#### 500

API服务遭遇了一个内部错误。

#### 503

API所执行的操作引发了一个错误。错误的具体信息可以从HTTP Response Body。



