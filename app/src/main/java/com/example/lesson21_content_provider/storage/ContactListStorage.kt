package com.example.lesson21_content_provider.storage

import android.content.SharedPreferences
import com.example.lesson21_content_provider.models.Contact
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val CONTACTS = "contacts"
class ContactListStorage private constructor() {
    companion object {
        private lateinit var storage: SharedPreferences
        private lateinit var instance: ContactListStorage

        fun getInstance(): ContactListStorage {
            if (instance == null) {
                instance = ContactListStorage()
            }
            return instance
        }

        fun init(value: SharedPreferences) {
            this.storage = value
        }
    }

    fun getContacts(): List<Contact> {
        return Gson().fromJson(storage.getString(CONTACTS, "[]"), object: TypeToken<List<Contact>>(){}.type)
    }
}