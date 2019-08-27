单词卡片项目。
详细说明，详见：
https://blog.csdn.net/u014620028/article/details/100098090

项目缺点：

1、不支持卡片回撤。即：卡片滑出后，不支持撤回已划出的单词卡
2、一卡一词模式下，释义行数高度计算不完成

原因：

1、单词卡可以在“一卡多词”、“一卡一词”中任意切换，用户滑出后，可以，就现在的单词做切换，回撤时，要考虑东西很多

2、现在的算法，切换样式前后，是将当前单词作为卡片上的第一个展示，而不是5个视为一组

总之，实现卡片撤回，复杂，要改的东西太多。这里，暂时没有实现


以卡片形式展示单词，卡片可滑动，划出去后，展示下一张单词卡

核心算法：

如：当前有20个单词（0-19）

   一开多词情况下：划出1张，即，学了0-4，当前展示第二张，5-9。
 
  然后切换一卡一词，划出2张，即，学了5，6。当前展示的7（因为6划出去了）

  再切换回一卡多词，当前卡片上，展示的是：7，8，9，10，11 这5个单词。而不是 5，6，7，8，9


具备功能：

1、一卡多词（5个单词）、一卡一词切换

2、自动读音

3、数据库查询
