package kr.ac.kumoh.sauron_yunseok

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kr.ac.kumoh.sauron_yunseok.helpers.MqttHelper
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage


class MainActivity : AppCompatActivity() {
    var mqttHelper: MqttHelper? = null
    var data: String? = null
    var dataReceived: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startMqtt()

        lock.setOnClickListener {
            //val i = Intent(this, LockActivity::class.java)
            //startActivity(i)
            try {
                data = "open"
                mqttHelper!!.mqttAndroidClient.publish("sauron/yun", data!!.toByteArray(), 0, false)
            } catch (e: MqttException) {
                e.printStackTrace()
            }
        }

        help.setOnClickListener {
            showHelpPopup()
        }

        newface.setOnClickListener{
            val intent = Intent(this, AddUserActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showHelpPopup(){
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.helppopup, null)

        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Help Sauron")
            .setPositiveButton("닫기", null)
            .create()


        alertDialog.setView(view)
        alertDialog.show()
    }

    private fun startMqtt() {
        mqttHelper = MqttHelper(applicationContext)
        mqttHelper!!.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(b: Boolean, s: String) {}
            override fun connectionLost(throwable: Throwable) {}
            @Throws(Exception::class)
            override fun messageArrived(topic: String, mqttMessage: MqttMessage) {
                //Log.w("Debug", mqttMessage.toString())
                //dataReceived!!.text = mqttMessage.toString()
            }

            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {}
        })
    }
}
