# Remake
Library to modify Java classes at runtime, without the use of java agents.<br>
Based on body's [Remake](https://github.com/Body-Alhoha/Remake)

## How does this work? 
This work by loading our own Java native library, then calling [retransformClasses](https://docs.oracle.com/javase/6/docs/api/java/lang/instrument/Instrumentation.html#retransformClasses(java.lang.Class...)) with a [ClassFileLoadHook](https://docs.oracle.com/javase/8/docs/platform/jvmti/jvmti.html#ClassFileLoadHook) to modify the class bytes and edit the class bytecode

## Contribute
There's 2 subprojects in this:
  - the native library, a [Visual Studio](https://visualstudio.microsoft.com/) project
  - the java libary, an [Intellij Idea](https://www.jetbrains.com/idea/) project

It would really help out if you contribute, thanks <3
