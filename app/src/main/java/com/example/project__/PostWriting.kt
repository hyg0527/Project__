package com.example.project__

import android.content.ContentValues
import android.content.ContentValues.TAG
import java.text.SimpleDateFormat
import java.util.Date
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PostWriting : AppCompatActivity() {
    private lateinit var edittitle: EditText
    private lateinit var editprice: EditText
    private lateinit var edittext: EditText
    private lateinit var imagespinner: String
    private lateinit var imagewriting: String
    private var isEditMode = false
    var price = 0
    var postcount = 2
    //private lateinit var sellername: EditText

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_writing_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        edittitle = findViewById(R.id.EditTitle)
        editprice = findViewById(R.id.EditPrice)
        edittext = findViewById(R.id.EditText)

        val spinner = findViewById<Spinner>(R.id.categorySpinner)
        val spinnerItems: Array<String> = resources.getStringArray(R.array.categories)

        val checktitle_empty = edittitle.text.toString().trim()
        val checkprice_empty = editprice.text.toString().trim()
        val checktext_empty = edittext.text.toString().trim()

        val db: FirebaseFirestore = Firebase.firestore
        val itemsCollectionRef = db.collection("pocket_post")
        val postCountDocRef = db.collection("pocket_count").document("count")
        val postcountField = "postcount"

        fun getCurrentDateTime(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
            val currentDateAndTime: String = sdf.format(Date())
            return currentDateAndTime
        }

        fun registerPost(/*newPostCount: Long*/) {
            val itemsCollectionRef = db.collection("pocket_post")

            val currentUser = FirebaseAuth.getInstance().currentUser?.displayName.toString()
            val listInfo = Items()

            // 게시물 정보 설정
            val itemMap = hashMapOf(
                "price" to checkprice_empty,
                "body" to checktext_empty,
                "title" to checktitle_empty,
                "date" to getCurrentDateTime(),
                "condition" to imagespinner,
                "soldout" to false
            )

            listInfo.date = itemMap["date"] as String?
            listInfo.soldout = itemMap["soldout"] as Boolean?
            listInfo.condition = itemMap["condition"] as String?
            listInfo.author = currentUser
            listInfo.body = itemMap["body"] as String?
            listInfo.price = itemMap["price"].toString().toLong()
            listInfo.title = itemMap["title"] as String?

            // Firestore에 데이터 추가
            val documentName = "post" + System.currentTimeMillis()
            itemsCollectionRef.document(documentName).set(listInfo)
//            itemsCollectionRef.document("post$newPostCount")
//                .set(itemMap)
//                .addOnSuccessListener {
//                    // 성공적으로 추가되었을 때의 로직
//                }
//                .addOnFailureListener { e ->
//                    // 실패했을 때의 로직
//                }
        }

        when (item.itemId) {
            R.id.action_register -> {
                registerPost()

//                fun updatePost(postID: String) {
//                    // postId에 해당하는 문서 가져오기
//                    val postDocRef = itemsCollectionRef.document(postID)
//
//                    // 업데이트할 데이터
//                    val updateData = hashMapOf<String, Any>(
//                        "price" to editprice.text.toString(),
//                        "body" to edittext.text.toString(),
//                        "title" to edittitle.text.toString(),
//                        "date" to getCurrentDateTime(),
//                        "condition" to imagespinner,
//                        "soldout" to false
//                    )
//
//                    postDocRef.update(updateData).addOnSuccessListener {
//                            // 업데이트 성공 시 필요한 작업 수행
//                        }
//                        .addOnFailureListener { e ->
//                            // 업데이트 실패 시 에러 처리
//                            Log.e(TAG, "Error updating document", e)
//                        }
//                }


//                postCountDocRef.get().addOnSuccessListener { documentSnapshot ->
//                    if (documentSnapshot.exists()) {
//                        val postID = intent.getStringExtra("edit")
//                        if (isEditMode && postID != null) {
//
//                            updatePost(postID)
//                            isEditMode = false
//                        }
//                        else {
//                            val currentCount = documentSnapshot.getLong("postcount") ?: 0
//                            val newCount = currentCount + 1
//                            // 현재 값을 업데이트
//                            postCountDocRef.update("postcount", newCount)
//                            // 게시물 작성
//                            registerPost(newCount)
//                        }
//
//                    } else {
//                        // 문서가 없다면 새로 생성
//                        postCountDocRef.set(mapOf("postcount" to 1))
//
//                        // 게시물 작성
//                        registerPost(1)
//                    }
//                }
                val myInstance2 = MyClass()
                val propertyValue = myInstance2.getMyProperty()

                val currentUser = FirebaseAuth.getInstance().currentUser?.displayName.toString()

                val intent = Intent(this, PostView::class.java)
                intent.putExtra("title", edittitle.text.toString())
                intent.putExtra("price", editprice.text.toString())
                intent.putExtra("text", edittext.text.toString())
                intent.putExtra("value", propertyValue)
                intent.putExtra("condition", imagespinner)
                intent.putExtra("image", imagewriting)
                intent.putExtra("Author", currentUser)

                fun isInteger(value: String): Boolean {
                    return try {
                        value.toInt()
                        true
                    } catch (e: NumberFormatException) {
                        false
                    }
                }

                if(!isInteger(checkprice_empty)) {
                    Toast.makeText(applicationContext, "가격란에는 정수를 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return true
                }

                if (checktitle_empty.isEmpty()) {
                    Toast.makeText(applicationContext, "제목란이 비어있습니다.", Toast.LENGTH_SHORT).show()
                    return true
                } else if (checkprice_empty.isEmpty()) {
                    Toast.makeText(applicationContext, "가격란이 비어있습니다.", Toast.LENGTH_SHORT).show()
                    return true
                } else if (checktext_empty.isEmpty()) {
                    Toast.makeText(applicationContext, "내용란이 비어있습니다.", Toast.LENGTH_SHORT).show()
                    return true
                }

                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_writing)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val db = Firebase.firestore
        val itemsCollectionRef = db.collection("pocket_post")
        setSupportActionBar(toolbar)

        edittitle = findViewById(R.id.EditTitle)
        editprice = findViewById(R.id.EditPrice)
        edittext = findViewById(R.id.EditText)

        val spinner = findViewById<Spinner>(R.id.categorySpinner)
        val imageView = findViewById<ImageView>(R.id.imageView)

        val postID = intent.getStringExtra("edit")

        val title = intent.getStringExtra("title")
        val price = intent.getStringExtra("price")
        val body = intent.getStringExtra("text")
        val image = intent.getStringExtra("image").toString()
        println(image)


        //수정 모드
//        fun loadPostData(postID: String) {
//            itemsCollectionRef.document(postID)
//                .get()
//                .addOnSuccessListener { documentSnapshot ->
//                    if (documentSnapshot.exists()) {
//                        val title = documentSnapshot.getString("title")
//                        val price = documentSnapshot.getString("price")
//                        val text = documentSnapshot.getString("body")
//                        val resourceName = documentSnapshot.getString("condition")
//
//                        if(resourceName == "새 상품") {
//                            imagewriting = "unwrapped"
//                            val resourceId = resources.getIdentifier("imagewriting", "drawable", packageName)
//                            editcondition.setImageResource(resourceId)
//                            spinner.setSelection(0)
//                        }
//                        else if(resourceName == "상태 좋음") {
//                            imagewriting = "good"
//                            val resourceId = resources.getIdentifier("imagewriting", "drawable", packageName)
//                            editcondition.setImageResource(resourceId)
//                            spinner.setSelection(1)
//                        }
//                        else if(resourceName == "상태 보통") {
//                            imagewriting = "normal"
//                            val resourceId = resources.getIdentifier("imagewriting", "drawable", packageName)
//                            editcondition.setImageResource(resourceId)
//                            spinner.setSelection(2)
//                        }
//                        else {
//                            imagewriting = "bad"
//                            val resourceId = resources.getIdentifier("imagewriting", "drawable", packageName)
//                            editcondition.setImageResource(resourceId)
//                            spinner.setSelection(3)
//                        }
//
//                        edittitle.setText(title)
//                        editprice.setText(price)
//                        edittext.setText(text)
//
//                        // 기타 필요한 작업 수행
//                    } else {
//                        // 문서가 존재하지 않는 경우, 예외 처리 등을 수행
//                    }
//                }
//                .addOnFailureListener { exception ->
//                    // 데이터를 가져오는 중 에러가 발생한 경우, 예외 처리 등을 수행
//                    Log.w(ContentValues.TAG, "Error getting document", exception)
//                }
//        }
//
//        isEditMode = intent.getBooleanExtra("editMode", false)
//        if (isEditMode) {
//            if (postID != null) {
//                loadPostData(postID)
//            }
//        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 뒤로가기 버튼
        toolbar.setNavigationOnClickListener {

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val spinnerItems: Array<String> = resources.getStringArray(R.array.categories)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerItems)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val myInstance = MyClass()
                val selectedItem = spinnerItems[position]

                when (selectedItem) {
                    spinnerItems[0] -> {
                        imageView.setImageResource(R.drawable.unwrapped)
                        imagewriting = "unwrapped"
                        imagespinner = "새 상품"
                    }
                    spinnerItems[1] -> {
                        imageView.setImageResource(R.drawable.good)
                        imagewriting = "good"
                        imagespinner = "상태 좋음"
                    }
                    spinnerItems[2] -> {
                        imageView.setImageResource(R.drawable.normal)
                        imagewriting = "normal"
                        imagespinner = "상태 보통"
                    }
                    spinnerItems[3] -> {
                        imageView.setImageResource(R.drawable.bad)
                        imagewriting = "bad"
                        imagespinner = "상태 안 좋음"
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                imageView.setImageResource(R.drawable.unwrapped)
                imagewriting = "unwrapped"
                imagespinner = "새 상품"
            }
        }


        edittitle.setText(title)
        editprice.setText(price)
        edittext.setText(body)

        if (image == "unwrapped") {
            imageView.setImageResource(R.drawable.unwrapped)
            spinner.setSelection(0)
        }
        else if (image == "good") {
            imageView.setImageResource(R.drawable.good)
            spinner.setSelection(1)
        }
        else if (image.equals("normal")) {
            println("done")
            imageView.setImageResource(R.drawable.normal)
            spinner.setSelection(2)
        }
        else if (image == "bad") {
            imageView.setImageResource(R.drawable.bad)
            spinner.setSelection(3)
        }
    }

    class MyClass {
        private var myProperty: String = ""

        fun getMyProperty(): String {
            return myProperty
        }

        fun setMyProperty(newValue: String) {
            myProperty = newValue
        }
    }
}