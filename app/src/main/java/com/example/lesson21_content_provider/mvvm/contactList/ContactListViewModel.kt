package com.example.lesson21_content_provider.mvvm.contactList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lesson21_content_provider.models.Contact

class ContactListViewModel(private val repository: ContactListRepository): ViewModel() {

    val contactsLiveData = MutableLiveData<List<Contact>>()
    fun requireContactList(): Unit {
         contactsLiveData.value = repository.loadContacts()
    }
}