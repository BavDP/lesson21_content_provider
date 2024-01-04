package com.example.lesson21_content_provider.mvvm.contactList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class ContactListViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactListViewModel::class.java)) {
            return ContactListViewModel(ContactListRepository()) as T
        }
        throw IllegalArgumentException()
    }
}