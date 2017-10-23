# 正则表达式引擎

## 工作原理

1. 解析正则表达式，构建Token序列；
2. 将Token序列转换为中缀表达式；
3. 将中缀表达式转换为后缀表达式；
4. 使用Thompson构造法将后缀表达式转换为NFA；
5. 通过子集构造法将NFA转换为DFA；
6. 使用Hopcroft算法最小化DFA；
7. 通过DFA进行匹配。

## 实现功能

1. 实现简单闭包`*`，`?`，`+`；
2. 支持集合`[]`；
3. 支持`|`运算;
4. 支持`{}`范围；
5. 支持规定的预定义字符及字符集；
6. 仅支持ASCII；
7. 支持`Escape Character`；
8. 支持`^`，`$`。

## 主要Class的介绍

* Lexer：负责将正则表达式转换为后缀表达式序列。
* NFAConstructor：负责将后缀表达式转换为NFA，并以`NFAPair`的形式输出。
* NFAPair：只有一个开始节点和一个结束节点，保存了从开始节点到结束节点的所有节点及边的标签。
* DFAConstructor：通过子集构造法对每个NFA节点集合生成表项`DFAEntry`，再根据子集表构建DFA，输出DFA的开始节点。
* RegexEngine：对于每一个正则表达式都生成一个`Pattern`。
* Pattern：保存了DFA，可以根据内容进行匹配。

## 主要功能

> 所有的匹配功能都由`Pattern`提供，`RegexEngine`类只生成`Pattern`对象。

`Pattern`提供以下方法：

```java
boolean match(String content);

```
该方法用来匹配`content`，只产生匹配结果，支持`^`与`$`。

```java
List<String> matchAll(String content);
```

该方法将`content`中所有的可匹配的子串提取出来，并以`List`的形式返回。不支持`^`与`$`。

```java
void printTrace(String content);

```
该方法跟踪匹配`content`时的路径，打印路过的所有节点。

```java
void printDFA();
```

该方法打印DFA中所有的节点与边。

## 使用示例


```java
String regex = "\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}";
Pattern pattern = RegexEngine.createPattern(regex);
boolean result = pattern.match("xxxxx@gamil.com");

```
