# Rowma Kotlin SDK
[ ![Download](https://api.bintray.com/packages/asmsuechan/rowma/rowma-kotlin/images/download.svg) ](https://bintray.com/asmsuechan/rowma/rowma-kotlin/_latestVersion)

We can build Android app of Rowma.

## Install
Just put this line to `dependencies` in `build.gradle`.

```
implementation 'com.rowma.rowma-kotlin:rowma-kotlin:0.0.3'
```

## Usage
The simple usage is below:

```kotlin
package com.rowma.rowmaandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rowma.rowma_kotlin.Rowma
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONArray

class MainActivity : AppCompatActivity() {
    val rowma = Rowma("https://rowma.moriokalab.com")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rowma.connect()
        rowma.publish("df0c5509-2464-4bbf-9064-2d0bc7f56102", "/chatter", "test message")
        getCurrentRobots()
    }

    fun getCurrentRobots() = GlobalScope.launch(Dispatchers.Main) {
        async(Dispatchers.Default) { rowma.currentConnectionList() }.await().let {
            val res = JSONArray(it.toString())
            println(res)
        }
    }
}
```

## Deployment
This package is deployed to bintray.com by using `gradle`.

```bash
gradle clean build bintrayUpload -PbintrayUser=BintrayUsername -PbintrayApiKey=BintrayApiKey -PdryRun=false
```
