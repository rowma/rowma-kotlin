package com.rowma.rowma_kotlin

import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class Rowma (url: String) {
    val url : String = url;
    var socket: Socket;
    val uuid : String = UUID.randomUUID().toString()
    private var handlers : MutableMap<String, () -> Void> = mutableMapOf();

    object HttpClient {
        val instance = OkHttpClient()
    }

    init {
        val options = IO.Options()
        options.transports = arrayOf("websocket");
        val mSocket = IO.socket(url, options)
        socket = Socket(mSocket.io(),"/rowma", null)
    }

    fun connect () {
        socket.on(Socket.EVENT_CONNECT) {
            registerApplication()
        }.on(Socket.EVENT_DISCONNECT) {
            println("disconnected")
        }.on("topic_to_application") { parameters ->
            print("")
        }
        socket.connect()
    }

    fun close () {
        socket.close()
    }

    fun currentConnectionList (networkUuid : String = "default"): JSONArray {
        val baseUrl = "$url/list_connections?uuid=$networkUuid"
        val request = Request.Builder()
            .url(baseUrl)
            .build()

        val response = HttpClient.instance.newCall(request).execute()
        val body = response.body()?.string()
        return JSONArray(body)
    }

    fun getRobotStatus (uuid: String, networkUuid: String = "default", jwt: String = "") : JSONObject {
        val baseUrl = "$url/robots?uuid=$uuid&networkUuid=$networkUuid"
        val request = Request.Builder()
            .url(baseUrl)
            .addHeader("Authorization", jwt)
            .build()

        val response = HttpClient.instance.newCall(request).execute()
        val body = response.body()?.string()
        return JSONObject(body)
    }

    fun runLaunch (uuid: String, command: String) {
        val destination = JSONObject()
        destination.put("type", "robot")
        destination.put("uuid", uuid)

        val payload = JSONObject()
        payload.put("destination", destination)
        payload.put("command", command)

        socket.emit(
            "run_launch",
            payload,
            Ack { }
        )
    }

    fun runRosrun(uuid: String, command: String, args: String) {
        val destination = JSONObject()
        destination.put("type", "robot")
        destination.put("uuid", uuid)

        val payload = JSONObject()
        payload.put("destination", destination)
        payload.put("command", command)
        payload.put("args", args)

        socket.emit(
            "run_rosrun",
            payload,
            Ack { }
        )
    }

    fun killNodes(uuid: String, rosnodes: Array<String>) {
        val destination = JSONObject()
        destination.put("type", "robot")
        destination.put("uuid", uuid)

        val payload = JSONObject()
        payload.put("destination", destination)
        payload.put("rosnodes", rosnodes)

        socket.emit(
            "kill_rosnodes",
            payload,
            Ack { }
        )
    }

    fun publish (uuid: String, topic: String, msg: Any) {
        val topicMessage = JSONObject()
        topicMessage.put("op", "publish")
        topicMessage.put("topic", topic)
        topicMessage.put("msg", msg)
        val destination = JSONObject()
        destination.put("type", "robot")
        destination.put("uuid", uuid)
        val payload = JSONObject()
        payload.put("destination", destination)
        payload.put("msg", topicMessage)

        socket.emit(
            "topic_transfer",
            payload,
            Ack { }
        )
    }

    fun setTopicRoute(destUuid: String, topicDestType: String, topicDestUuid: String, topic: String, alias: String?) {
        val destination = JSONObject()
        destination.put("type", "robot")
        destination.put("uuid", destUuid)

        val topicDestination = JSONObject()
        topicDestination.put("type", topicDestType)
        topicDestination.put("uuid", topicDestUuid)

        val msg = JSONObject()
        msg.put("op", "subscribe")
        msg.put("topicDestination", topicDestination)
        msg.put("topic", topic)
        if (alias != null) msg.put("alias", alias)

        val payload = JSONObject()
        payload.put("destination", destination)
        payload.put("msg", msg)

        socket.emit(
            "topic_transfer",
            payload,
            Ack { }
        )
    }

    private fun registerApplication () {
        val payload = JSONObject()
        payload.put("applicationUuid", this.uuid)
        socket.emit(
            "register_application",
            payload,
            Ack { }
        )
    }

    private fun baseHandler(topic: String) {
        val handler = handlers[topic]
        if (handler != null) {
            handler()
        }
    }
}
