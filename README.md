# Remake
Library to modify Java classes at runtime, without the use of java agents.<br>
Based on body's [Remake](https://github.com/Body-Alhoha/Remake)

## How does this work? 
This work by loading our own Java native library, then calling [retransformClasses](https://docs.oracle.com/javase/6/docs/api/java/lang/instrument/Instrumentation.html#retransformClasses(java.lang.Class...)) with a [ClassFileLoadHook](https://docs.oracle.com/javase/8/docs/platform/jvmti/jvmti.html#ClassFileLoadHook) to modify the class bytes and edit the class bytecode

## How do i use this?
To use this, please check out the pretty self explainatory example [here](https://github.com/StellarTweaks/Remake/blob/main/Remake/src/test/java/sh/body/test/Main.java). <br>
This first calls the `test` function, then edits its bytecode to change the strings and calls it again.
```java
test()
```
The expected result without Remake would be printing `Hello, World!` twice, yet this is the response: 
```
Hello, World!
[Transformer] Found class: sh/body/test/Main
Changed ldc: Hello, World!
Hooked by remake <3
```
As we can see here, Remake modified the string and as the test function was called for the 2nd time, it outputed something totally different. <br>
You can also use this library to modify other values or logic flow, basically recode the whole program while it's currently running, just like HotSwap.

## Contribute
There's 2 subprojects in this:
  - the native library, a [Visual Studio](https://visualstudio.microsoft.com/) project
  - the java libary, an [Intellij Idea](https://www.jetbrains.com/idea/) project

It would really help out if you contribute, thanks <3
