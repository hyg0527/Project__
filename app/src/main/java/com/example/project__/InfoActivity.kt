package com.example.project__

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.info_activity)

        val author = findViewById<TextView>(R.id.authorInfo)
        val button = findViewById<Button>(R.id.enterMessaging)
        val currentUser = FirebaseAuth.getInstance().currentUser?.displayName.toString()

        val receivedData = intent.getStringExtra("Author") ?: "" // 상대방의 이름을 intent로 받아와서 textview에 출력
        if (receivedData != "") {
            author.text = receivedData
        }
        button.setOnClickListener {
            if (receivedData == currentUser) {
                Toast.makeText(this, "자신의 글에 채팅할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
            else {
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("author", receivedData)
                startActivity(intent)
            }
        }
    }
}