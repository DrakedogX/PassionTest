package com.passion.chatapp.ui.contacts

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.passion.chatapp.data.model.User
import com.passion.chatapp.databinding.ContactItemBinding
import com.passion.chatapp.ui.findUser.mQuery
import java.util.*


class ContactsAdapter(private val itemClickCallback: ItemClickCallback) :
    ListAdapter<User, ContactsAdapter.UserHolder>(DiffCallbackContacts()), Filterable,
    OnQueryTextChange {

    private var filteredUserList = mutableListOf<User>()
    var usersList = listOf<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {

        return UserHolder.from(
            parent
        )

    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val item = getItem(position)

        holder.bind(item, itemClickCallback)
    }

    class UserHolder private constructor(val binding: ContactItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: User,
            itemClickCallback: ItemClickCallback
        ) {
            if (mQuery.isEmpty()) {
                binding.nameTextView.text = item.username
            } else {
                var index = item.username!!.indexOf(mQuery, 0, true)
                val sb = SpannableStringBuilder(item.username)
                while (index >= 0) {
                    val fcs = ForegroundColorSpan(Color.rgb(135, 206, 235))
                    sb.setSpan(
                        fcs,
                        index,
                        index + mQuery.length,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                    sb.setSpan(
                        StyleSpan(Typeface.BOLD),
                        index,
                        index + mQuery.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    index = item.username.indexOf(mQuery, index + 1)
                }
                binding.nameTextView.text = sb
            }

            binding.user = item
            binding.executePendingBindings()
            binding.parentLayout.setOnClickListener {
                itemClickCallback.onItemClicked(item)
            }

        }

        companion object {
            fun from(parent: ViewGroup): UserHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ContactItemBinding.inflate(layoutInflater, parent, false)

                return UserHolder(
                    binding
                )
            }
        }
    }

    interface ItemClickCallback {
        fun onItemClicked(user: User)

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                filteredUserList = mutableListOf()
                if (charString.isEmpty()) {
                    filteredUserList = usersList as MutableList<User>


                } else {
                    for (user in usersList) {
                        if (user.username?.toLowerCase(Locale.ENGLISH)?.contains(
                                charString.toLowerCase(Locale.ENGLISH)
                            )!!
                        ) {
                            filteredUserList.add(user)
                        }
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredUserList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {

                val mutableList = filterResults.values as MutableList<User>
                submitList(mutableList)
                notifyItemRangeChanged(0, mutableList.size)

            }
        }
    }

    override fun onChange(query: String) {
        mQuery = query
    }

}

class DiffCallbackContacts : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.uid == newItem.uid
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}

interface OnQueryTextChange {
    fun onChange(query: String)
}


