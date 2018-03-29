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