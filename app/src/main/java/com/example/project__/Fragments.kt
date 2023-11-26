package com.example.project__

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(R.layout.fragment_home) {
//    override fun onContextItemSelected(item: MenuItem): Boolean {
//        when(item.itemId){
//            R.id.top_filter ->
//        }
//        return super.onContextItemSelected(item)
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        // RecyclerView 초기화
        val recyclerView = view.findViewById<RecyclerView>(R.id.showItemrecyclerView)
        val itemList = ArrayList<Items>()
        val boardAdapter = MainAdapter(itemList)
        // LinearLayoutManager 설정
        val layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(requireActivity(), layoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)

        val documentRef = Firebase.firestore.collection("pocket_post").orderBy("date", Query.Direction.DESCENDING)
        documentRef.addSnapshotListener { snapshot, e ->
            if (e != null) { // 오류 처리
                Log.w(ContentValues.TAG, "Listen failed.", e)
            }
            snapshot?.documentChanges?.forEach { change ->
                when (change.type) {
                    DocumentChange.Type.ADDED -> { // document 추가될 때
                        itemList.clear()
                        for (document in snapshot) {
                            val item = Items(
                                        document["id"] as String?,
                                        document["author"] as String?,
                                        document["body"] as String?,
                                        document["condition"] as String?,
                                        document["date"] as String?,
                                        document["price"] as Long?,
                                        document["soldout"] as Boolean?,
                                        document["title"] as String?,)
                            itemList.add(item)
                            boardAdapter.notifyDataSetChanged()
                        }
                    }
                    else -> {}
                }
            }
        }

        recyclerView.adapter = boardAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        // author, body, condition, date, price, soldout, title
        view.findViewById<FloatingActionButton>(R.id.enrollList).setOnClickListener {
            val intent = Intent(requireActivity(), PostWriting::class.java)
            startActivity(intent)
//            requireActivity().finish() // 현재 activity 종료
        }
        return view
    }
}
class ChatFragment : Fragment(R.layout.chat_fragment) {
    private var isViewCreated = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.chat_fragment, container, false)
        initRecyclerView(view)
        isViewCreated = false

        return view
    }
    // 뷰가 새로 갱신될 때마다 addactionListener가 적용 되도록 구현
    override fun onResume() {
        super.onResume()
        if (isViewCreated) initRecyclerView(requireView())
        isViewCreated = true
    }

    private fun initRecyclerView(view: View) {  // 메시지 리스트 갱신하는 함수
        val recyclerView = view.findViewById<RecyclerView>(R.id.chattingList)

        val itemList = ArrayList<ChatView>()
        val boardAdapter = MyListViewAdapter(itemList)
        val currentUser = FirebaseAuth.getInstance().currentUser?.displayName.toString()
        val path = "ChatList: " + currentUser
        val documentRef = Firebase.firestore.collection(path)

        recyclerView.adapter = boardAdapter
        val layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(requireActivity(), layoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)

        // 컬렉션 내의 문서 이름을 가져 오는 쿼리
        documentRef.addSnapshotListener { snapshot, e ->
            if (e != null) { // 오류 처리
                Log.w(ContentValues.TAG, "Listen failed.", e)
            }
            snapshot?.documentChanges?.forEach { change ->
                when (change.type) {
                    DocumentChange.Type.ADDED -> { // document 추가될 때
                        itemList.clear()
                        for (document in snapshot.documents) {
                            val documentName = document.id
                            itemList.add(
                                ChatView(
                                    documentName,
                                    document["text"] as String?,
                                    document["time"] as String?
                                )
                            )
                        }
                        boardAdapter.notifyDataSetChanged()
                    }
                    // 다른 DocumentChange.Type 처리
                    else -> {}
                }
            }
        }
    }
}
class UserFragment : Fragment(R.layout.user_fragment) {
    private val PREFS_KEY = "com.example.project__.PREFS_KEY"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fb = FirebaseAuth.getInstance()
        val topNickname = view.findViewById<TextView>(R.id.topNickname)
        val topEmail = view.findViewById<TextView>(R.id.topEmail)
        topNickname.text = fb.currentUser?.displayName.toString()
        topEmail.text = fb.currentUser?.email.toString()

        val userCollection = Firebase.firestore.collection("Users")
        val query = userCollection.whereEqualTo("nickname", fb.currentUser?.displayName)

        query.get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // 쿼리 결과가 비어 있지 않다면 해당 문서의 데이터를 가져와서 사용
                    val userDocument = documents.documents[0]  // 여러 문서 중 첫 번째 문서 사용
                    val userNickname = userDocument.getString("nickname")
                    val userName = userDocument.getString("name")
                    val userEmail = userDocument.getString("email")
                    val userBirthdate = userDocument.getString("birthdate")
                    view.findViewById<TextView>(R.id.bottom_nickname).text = userNickname
                    view.findViewById<TextView>(R.id.bottom_name).text = userName
                    view.findViewById<TextView>(R.id.bottom_email).text = userEmail
                    view.findViewById<TextView>(R.id.bottom_birthdate).text = userBirthdate
                } else // 여러 문서 중 첫 번째 문서 사용// 쿼리 결과가 비어 있지 않다면 해당 문서의 데이터를 가져와서 사용
                {
                    Toast.makeText(requireContext(), "사용자 정보 가져오기 실패", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                // 쿼리 수행 중 에러가 발생한 경우 처리
                Toast.makeText(requireContext(), "사용자 정보 가져오기 실패", Toast.LENGTH_SHORT).show()
            }
        view.findViewById<Button>(R.id.signout)?.setOnClickListener {
            startLoginActivityAndFinish()
        }
    }
    private fun startLoginActivityAndFinish() { // 로그아웃 함수
        // Clear shared preferences
        val sharedPreferences = requireActivity().getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        editor.commit()

        // Sign out from Firebase
        Firebase.auth.signOut()
        // Start LoginActivity and finish current activity
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

}