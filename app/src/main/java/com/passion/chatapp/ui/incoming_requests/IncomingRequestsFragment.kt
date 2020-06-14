package com.passion.chatapp.ui.incoming_requests

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.passion.chatapp.R
import com.passion.chatapp.utils.LOGGED_USER
import com.passion.chatapp.data.model.User
import com.passion.chatapp.databinding.IncomingRequestsFragmentBinding

class IncomingRequestsFragment : Fragment() {


    private lateinit var adapter: IncomingRequestsAdapter
    private lateinit var binding: IncomingRequestsFragmentBinding
    var sendersList: MutableList<User>? = null


    companion object {
        fun newInstance() = IncomingRequestsFragment()
    }

    private lateinit var viewModel: IncomingRequestsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = "친구 요청이 도착하였습니다."
        binding =
            DataBindingUtil.inflate(inflater, R.layout.incoming_requests_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(IncomingRequestsViewModel::class.java)

        val mPrefs: SharedPreferences = activity!!.getPreferences(Context.MODE_PRIVATE)
        val gson = Gson()
        val json: String? = mPrefs.getString(LOGGED_USER, null)
        val loggedUser: User = gson.fromJson(json, User::class.java)

        val receivedRequest = loggedUser.receivedRequests
        if (!receivedRequest.isNullOrEmpty()) {
            viewModel.downloadRequests(receivedRequest).observe(this, Observer { requestersList ->
                binding.loadingRequestsImageView.visibility = View.GONE

                if (requestersList == null) {
                    binding.noIncomingRequestsLayout.visibility = View.VISIBLE
                    Toast.makeText(
                        context,
                        "친구 요청을 로딩하는 중 오류 발생",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    binding.noIncomingRequestsLayout.visibility = View.GONE
                    adapter.setDataSource(requestersList)
                    sendersList = requestersList
                    binding.receivedRequestsRecycler.adapter = adapter
                }
            })
        } else {
            binding.noIncomingRequestsLayout.visibility = View.VISIBLE
            binding.loadingRequestsImageView.visibility = View.GONE

        }

        adapter =
            IncomingRequestsAdapter(
                object : IncomingRequestsAdapter.ButtonCallback {
                    override fun onConfirmClicked(requestSender: User, position: Int) {
                        viewModel.addToFriends(requestSender.uid!!, loggedUser.uid!!)

                        Toast.makeText(
                            context,
                            "${requestSender.username} 친구를 추가하세요.",
                            Toast.LENGTH_LONG
                        ).show()
                        deleteFromRecycler(position)
                    }

                    override fun onDeleteClicked(requestSender: User, position: Int) {
                        viewModel.deleteRequest(requestSender.uid!!, loggedUser.uid!!)
                        Toast.makeText(context, "요청이 삭제되었습니다.", Toast.LENGTH_LONG).show()
                        deleteFromRecycler(position)
                    }

                    private fun deleteFromRecycler(position: Int) {
                        sendersList?.removeAt(position)
                        adapter.setDataSource(sendersList)
                        adapter.notifyItemRemoved(position)

                        if (sendersList?.size == 0) {
                            binding.noIncomingRequestsLayout.visibility = View.VISIBLE
                        }
                    }

                })


    }


}
