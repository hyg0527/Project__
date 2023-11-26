package com.example.project__

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PostView : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var cancelDialog: Dialog
    private lateinit var changeDialog: Dialog
    var imageString = ""
    var postcount = 1
    var nowon = false

    val db: FirebaseFirestore = Firebase.firestore
    val documentRef = db.collection("pocket_post").document("post2")

    fun updateSoldOutFalse(documentId: String, newFieldValue: Boolean) {
        val updates = hashMapOf<String, Any>(
            "soldout" to true
        )
        val updatesFalse = hashMapOf<String, Any>(
            "soldout" to false
        )

        if (nowon == false)
            documentRef.update(updatesFalse)
        else
            documentRef.update(updates)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_nav_menu, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_view)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            intent.putExtra("image", imageString)
            startActivity(intent)
        }


        val title = findViewById<TextView>(R.id.TitleText)
        val price = findViewById<TextView>(R.id.PriceText)
        val text = findViewById<TextView>(R.id.TextText)
        val imageView = findViewById<ImageView>(R.id.imageView)
        val sellerName = findViewById<TextView>(R.id.sellerName)
        val currentUser = FirebaseAuth.getInstance().currentUser?.displayName.toString()

        val intent = intent
        title.text = intent.getStringExtra("title")
        price.text = intent.getLongExtra("price", 10000).toString() + " 원"
        print(price)
        text.text = intent.getStringExtra("text")
        sellerName.text = intent.getStringExtra("Author")

        val image = intent.getStringExtra("image")
        val id = intent.getStringExtra("id")
        val resourceName = image // 동적으로 변경되는 리소스 이름
        val resourceId = resources.getIdentifier(resourceName, "drawable", packageName)
        imageView.setImageResource(resourceId)

        bottomNavigationView = findViewById(R.id.navigationView)

        if (sellerName.text == currentUser)
            bottomNavigationView.visibility = View.VISIBLE
        else
            bottomNavigationView.visibility = View.GONE
        bottomNavigationView.setOnItemSelectedListener{ item ->
            val db = Firebase.firestore
            val itemsCollectionRef = db.collection("post_post")

            when (item.itemId){
                R.id.menu_edit -> {
                    val intent = Intent(this, PostWriting::class.java)
                    intent.putExtra("edit", id)
                    intent.putExtra("editMode", true)
                    startActivity(intent)
                    true
                }
                R.id.menu_change -> {
                    changeDialog = Dialog(this)
                    changeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // 타이틀 제거
                    changeDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    changeDialog.setContentView(R.layout.change_state)

                    ChangeDialog()
                    true
                }
                R.id.menu_delete -> {
                    cancelDialog = Dialog(this)
                    cancelDialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // 타이틀 제거
                    cancelDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    cancelDialog.setContentView(R.layout.dialog)

                    CancelDialog()
                    true
                }
                else -> false
            }
            return@setOnItemSelectedListener true
        }

        val sendButton = findViewById<Button>(R.id.chatButton)
        sendButton.setOnClickListener {
            if (sellerName.text == currentUser)
                Toast.makeText(this, "자신의 글에 메시지를 보낼 수 없습니다.", Toast.LENGTH_SHORT).show()
            else {
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("author", sellerName.text)
                startActivity(intent)
            }
        }
    }

    private fun CancelDialog() {
        cancelDialog.show() // 다이얼로그 띄우기

        val NoButton = cancelDialog.findViewById<Button>(R.id.NoButton)
        NoButton.setOnClickListener {
            cancelDialog.dismiss()
        }

        cancelDialog.findViewById<Button>(R.id.YesButton).setOnClickListener {
            finish()
        }
    }

    private fun ChangeDialog() {
        changeDialog.show() // 다이얼로그 띄우기

        val SellButton = changeDialog.findViewById<Button>(R.id.NowSell)
        val NotSellButton = changeDialog.findViewById<Button>(R.id.NotSell)
        val CancelButton = changeDialog.findViewById<Button>(R.id.Cancel)
        val imageView = findViewById<ImageView>(R.id.imageView)

        imageString = intent.getStringExtra("image")!!

        CancelButton.setOnClickListener {
            changeDialog.dismiss()
        }

        SellButton.setOnClickListener {
            val NowOn = findViewById<TextView>(R.id.NowOn)
            if (NowOn.text != "판매중") {
                NowOn.text = "판매중"
                Toast.makeText(applicationContext, "상태가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                changeDialog.dismiss()
                nowon = false
                val resourceName = imageString // 동적으로 변경되는 리소스 이름
                val resourceId = resources.getIdentifier(resourceName, "drawable", packageName)
                imageView.setImageResource(resourceId)
                updateSoldOutFalse("post2", false)
            }
            changeDialog.dismiss()
        }

        NotSellButton.setOnClickListener {
            val NowOn = findViewById<TextView>(R.id.NowOn)
            if (NowOn.text != "판매완료") {
                NowOn.text = "판매완료"
                Toast.makeText(applicationContext, "상태가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                changeDialog.dismiss()
                nowon = true
                imageView.setImageResource(R.drawable.soldout)
                updateSoldOutFalse("post2", true)

            }
            changeDialog.dismiss()
        }
    }
}