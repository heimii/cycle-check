# cycle-check
用于检测 java 代码中的循环引用链（Service 层的代码）。


关于 CycleCheck 的使用

假设源代码目录为 d:/git/myProject, 目标目录为 f:/3, 先执行一段脚本，把文件进行一次预处理（在 git-bash 中执行即可）。

```bash
#/bin/bash
SRC=/d/git
DEST=/f/3

find $SRC -type f -name "*Service.java" | xargs -I {} cp {} $DEST
cd $DEST
sed -i '/private .*Service;/!d' $(find . -type f )
```
让代码中只保留 private XxxService xxxService;

再运行 CycleCheck 进行检测。如果存在循环引用，将打印以下内容：
```text
存在循环引用，引用链：WlService --> QualityService --> XxxService --> YyyService --> ZzzQueryService --> TttQueryService --> WlService
存在循环引用，引用链：ZjQueryService --> ZjService --> ZjQueryService
```

输出的内容保存到一个文件中，然后再进行一次处理：
```bash
sort resoult.txt | uniq > r2.txt
```


