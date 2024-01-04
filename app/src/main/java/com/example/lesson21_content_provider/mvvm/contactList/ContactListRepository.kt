package com.example.lesson21_content_provider.mvvm.contactList

import com.example.lesson21_content_provider.models.Contact
import com.example.lesson21_content_provider.storage.ContactListStorage

class ContactListRepository {
    fun loadContacts(): List<Contact> {
        return ContactListStorage.getInstance().getContacts()
    }
}