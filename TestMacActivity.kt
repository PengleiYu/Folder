package com.example.mactest

import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.NetworkInterface

/**
 * 测试是否可获取本机wifi mac和蓝牙mac
 */
class TestMacActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        networkInterface()
    }

    /**
     * 可行
     */
    private fun networkInterface() {
        val list = NetworkInterface.getNetworkInterfaces()
        for (interFace in list) {
            if (interFace.name != "wlan0") continue
            val address = interFace.hardwareAddress.joinToString(":") {
                if (it < 0) (it + 256).toString(16) else it.toString(16)
            }
            println("mac => $address")
        }
    }

    /**
     * 可行
     */
    private fun contentProviders() {
        val mac = Settings.Secure.getString(contentResolver, "bluetooth_address")
        println("mac => $mac")
    }

    /**
     * 无效，需要[android.permission.ACCESS_CONTENT_PROVIDERS_EXTERNALLY];
     * 但添加后依然无效
     */
    private fun settings() {
        execCommand("settings get secure bluetooth_address")
    }

    /**
     * 无效，权限拒绝
     */
    private fun readFile() {
        val wlan0File = "/sys/class/net/wlan0/address"
        val eth0File = "/sys/class/net/eth0/address"
        execCommand("cat $wlan0File")
    }

    private fun execCommand(command: String) {
        val process = Runtime.getRuntime().exec(command)
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val errorReader = BufferedReader(InputStreamReader(process.errorStream))
        reader.lines().forEach { println("reader => $it") }
        errorReader.lines().forEach { println("error => $it") }
    }
}
