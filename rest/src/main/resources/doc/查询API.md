# 查询API

## QueryxxxAction

QueryxxxAction：对一个资源进行查询，并且可以像MySQL一样指定多个查询条件、排序方式、选择字段、以及进行跨表查询等等。
例如：

```
    ?Action=QueryxxxAction
    &q=name=apt-test
    &start=0
    &limit=10
```
所有资源的查询API都支持下列参数：

|名字|类型|位置|描述|可选值|
|---|---|---|---|---|
|q (可选)|List|query|见[查询条件](#query-conditions)。省略该该字段将返回所有记录，返回记录数的上限受限于`limit`字段||
|limit (可选)|Integer|query|最多返回的记录数，类似MySQL的limit，默认值1000||
|start (可选)|Integer|query|起始查询记录位置，类似MySQL的offset。跟`limit`配合使用可以实现分页||
|count (可选)|Boolean|query|计数查询，相当于MySQL中的count()函数。当设置成`true`时，API只返回的是满足查询条件的记录数||
|groupBy (可选)|String|query|以字段分组，相当于MySQL中的group by关键字。例如groupBy=type||
|replyWithCount (可选)|Boolean|query|见上面[分页查询](#query-pagination)||
|sort (可选)|String|query|以字段排序，等同于MySQL中的sort by关键字，例如sort=+ip。必须跟+或者-配合使用，+表示升序，-表示降序,后面跟排序字段名|<ul><li>+`字段名`</li><li>-`字段名`</li></ul>||
|sortDirection (可选)|String|query|字段排序方向，必须跟`sortBy`配合使用|<ul><li>asc</li><li>desc</li></ul>|
|fields (可选)|List|query|指定返回的字段，等同于MySQL中的select字段功能。例如fields=name,uuid，则只返回满足条件记录的`name`和`uuid`字段||

#### <a name="query-conditions">查询条件</a>

查询条件类似于MySQL数据库，例如：

```
uuid=bfa67f956afb430890aa49db14b85153
totalCapacity>2000
vmInstanceUuid not null
```

> **字段名、查询操作符、匹配值三者之间不能有任何空格**。例如`uuid = 25506342d1384c07b7342373a57475b9`就是一个错误的查询条件，必须写为
`uuid=25506342d1384c07b7342373a57475b9`。

多个查询条件之间是**与**关系。总共支持10个查询操作符：

- `=`: 等于，例如：

    ```
    vmInstanceUuid=c4981689088b40f98d2ade2548c323da
    ```
    
- `!=`: 不等于，例如：

    ```
    vmInstanceUuid!=c4981689088b40f98d2ade2548c323da
    ```

- `>`: 大于
- `<`: 小于
- `>=`: 大于等于
- `<=`: 小于等于
- `?=`: `in`操作符，测试字段值是否在一个集合。集合中的值以`,`分隔。例如测试*uuid*是否属于某个集合：
    
    ```
    uuid?=25506342d1384c07b7342373a57475b9,bc58d68090ac42358c0cb0fe72e3287f
    ```
    
- `!?=`: `not int`操作符，测试字段值是否**不属于**一个集合。集合中的值以`,`分隔，例如测试*name*是否不等于VM1和VM2:

    ```
    name!?=VM1,VM2
    ```
    
- `~=`: 字符串模糊匹配，相当于MySQL中的`like`操作。使用`%`匹配一个或多个字符，使用`_`匹配一个字符。
    例如查询一个名字是以*IntelCore*开头的：
    
    ```
    name~=IntelCore%
    ```
    
    或者查询一个名字是以*IntelCore*开头，以*7*结尾，中间模糊匹配一个字符：
    
    ```
    name~=IntelCore_7
    ```  
      
    这样名字是*IntelCoreI7*，*IntelCoreM7*的记录都会匹配上。
    
- `!~=`: 模糊匹配非操作。查询一个字段不能模糊匹配到某个字符串，匹配条件与`~=`相同

- `is null`: 字段为null：

   ```
   name is null
   ```
   
- `not null`: 字段不为null：

   ```
   name not null
   ```

#### <a name="query-pagination">分页查询</a>

`start`、`limit`、`replyWithCount`三个字段可以配合使用实现分页查询。其中`start`指定其实查询位置，`limit`指定查询返回的最大记录数，而
`replyWithCount`被设置成true后，查询返回中会包含满足查询条件的记录总数，跟`start`值比较就可以得知还需几次分页。

例如总共有1000记录满足查询条件，使用如下组合:

```
start=0 limit=100 replyWithCount=true
```

则API返回将包含头100条记录，以及`total`字段等于1000，表示总共满足条件的记录为1000。