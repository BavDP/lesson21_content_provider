package com.example.lesson21_content_provider.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lesson21_content_provider.R
import com.example.lesson21_content_provider.models.Contact

class ContactListAdapter: RecyclerView.Adapter<ContactListAdapter.ViewHolder>() {
    private var contacts: List<Contact> = listOf(Contact("",""))

    class ViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        fun bind(contact: Contact) {
            val contactName = view.findViewById<TextView>(R.id.contactNameTextView)
            val contactPhone = view.findViewById<TextView>(R.id.contactPhoneTextView)
            contactName.text = contact.contactName
            contactPhone.text = contact.phone
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(contacts[position])
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    fun setContacts(value: List<Contact>) {
        contacts = value
        notifyDataSetChanged()
    }
}