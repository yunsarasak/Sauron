package kr.ac.kumoh.sauron_yunseok

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ebanx.swipebtn.SwipeButton
import kotlinx.android.synthetic.main.activity_lock.*

class LockActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)

        swipe.setOnStateChangeListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            Toast.makeText(this, "밀어서 잠금 해제", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onResume() {
        super.onResume()
        SwipeButton.START_OF
    }
}
