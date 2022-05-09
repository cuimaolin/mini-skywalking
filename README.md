# mini-skywalking

写这个仓库的主要目的是希望能够将skywalking的源码进行模块化，按照不同的功能分为不同的项目文件，保留最简洁的代码，以达到学习的目的。

![](https://camo.githubusercontent.com/143779cb51ec9557528e9059d1386d6cbc905fb46c8c20603f2f4dc0fb2b8ab1/68747470733a2f2f736b7977616c6b696e672e6170616368652e6f72672f696d616765732f536b7957616c6b696e675f4172636869746563747572655f32303231303432342e706e673f743d3230323130343234)

skywalking的主要流程为如下：

1. agent收集trace数据
2. agent发送trace数据给collector
3. collector接受trace数据
4. collector存储trace数据到数据库

围绕以上流程，我们将skywalking的代码分解为如下：

- java-agent-init. java agent初始化，即如何字节码侵入来实现监控
- java-agent-collect. 以spring mvc插件为例，介绍agent进行链路数据收集的流程
- java-agent-data. java agent trace的数据模型，遵守opentracing标准