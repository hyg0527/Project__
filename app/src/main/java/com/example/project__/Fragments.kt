package com.example.project__

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
                            val item = Items(document["author"] as String?,
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fb = FirebaseAuth.getInstance()
        val email = view.findViewById<TextView>(R.id.textEmailInfo)
        val email2 = view.findViewById<TextView>(R.id.textEmailInfo2)
        val name = view.findViewById<TextView>(R.id.textNickName)
        val name2 = view.findViewById<TextView>(R.id.textNickName2)
        val user = view.findViewById<TextView>(R.id.textUserInfo)

        val intent = activity?.intent
        val receivedName = intent?.getStringExtra("USER_NAME")

        // 이름, 이메일, 전화번호 등 정보를 textview에 출력
        email.text = fb.currentUser?.email
        name.text = fb.currentUser?.displayName.toString()
        email2.text = fb.currentUser?.email
        name2.text = fb.currentUser?.displayName.toString()
        user.text = receivedName
        view.findViewById<Button>(R.id.signout)?.setOnClickListener {
            Firebase.auth.signOut()
            startLoginActivityAndFinish()
        }
    }
    private fun startLoginActivityAndFinish() { // 로그아웃 함수
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // 현재 activity 종료
    }
}