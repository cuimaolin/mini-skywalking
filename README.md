# mini-skywalking

写这个仓库的主要目的是希望能够将skywalking的源码进行模块化，按照不同的功能分为不同的项目文件，保留最简洁的代码，以达到学习的目的。

目前包括：

- java-agent-init. java agent初始化的核心代码，如何做字节码侵入的核心实现
- java-agent-collect. java agent进行链路数据收集的核心代码，以spring mvc插件为例