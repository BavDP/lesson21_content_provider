package com.example.lesson21_content_provider

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.lesson21_content_provider.fragments.MainFragment
import com.example.lesson21_content_provider.storage.CONTACTS
import com.example.lesson21_content_provider.storage.ContactListStorage

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ContactListStorage.init(getSharedPreferences(CONTACTS, MODE_PRIVATE))
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragmentContainerView, MainFragment.newInstance())
                .commit()
        }
    }
}