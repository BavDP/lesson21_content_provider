package com.example.lesson21_content_provider.fragments

import android.Manifest
import android.content.ContentProviderOperation
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lesson21_content_provider.R
import com.example.lesson21_content_provider.adapters.ContactListAdapter
import com.example.lesson21_content_provider.databinding.FragmentMainBinding
import com.example.lesson21_content_provider.models.Contact
import com.example.lesson21_content_provider.mvvm.contactList.ContactListViewModel
import com.example.lesson21_content_provider.mvvm.contactList.ContactListViewModelFactory
import com.example.lesson21_content_provider.utils.reqRunTimePermission
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainFragment : Fragment() {
    private var loadContactsFromDeviceLauncher: ActivityResultLauncher<Array<String>>? = null
    private var saveContactsToDeviceLauncher: ActivityResultLauncher<Array<String>>? = null
    private val _viewModel: ContactListViewModel by viewModels { ContactListViewModelFactory() }
    private var _newContactName = ""
    private var _newContactPhone = ""
    private lateinit var binding: FragmentMainBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        initActivityResults()
        binding = FragmentMainBinding.inflate(LayoutInflater.from(this.context))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeContactsRecycleView()
        initializeObserves()
        binding.addNewContacts.setOnClickListener { addContactsClick() }
    }

    private fun initActivityResults() {
        loadContactsFromDeviceLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.containsKey(Manifest.permission.READ_CONTACTS)) loadContactsFromDevice()
        }

        saveContactsToDeviceLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.containsKey(Manifest.permission.WRITE_CONTACTS)) saveContactsToDevice()
        }
    }

    private fun initializeContactsRecycleView() {
        binding.ContactListRV.adapter = ContactListAdapter()
        binding.ContactListRV.layoutManager = LinearLayoutManager(this.requireContext())
    }

    private fun saveContactsToDevice () {
        val resolver = requireContext().contentResolver
        val operations = ArrayList<ContentProviderOperation>()
        val rawURI = ContactsContract.RawContacts.CONTENT_URI
        val dataURI = ContactsContract.Data.CONTENT_URI

        val ind = operations.run {
            this.add(
                ContentProviderOperation.newInsert(rawURI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build()
            )
            this.size - 1
        }
        operations.add(
            ContentProviderOperation.newInsert(dataURI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, ind)
                .withValue(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                )
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, _newContactName)
                .build()
        )

        operations.add(
            ContentProviderOperation.newInsert(dataURI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, ind)
                .withValue(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                )
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, _newContactPhone)
                .build()
        )
        resolver.applyBatch(ContactsContract.AUTHORITY, operations)
        loadContactsFromDevice()
    }

    private fun initializeObserves() {
        _viewModel.contactsLiveData.observe(viewLifecycleOwner) {contacts ->
            setRecycleViewVisible(contacts.isNotEmpty())
            if (contacts.isNotEmpty()) {
                (binding.ContactListRV.adapter as ContactListAdapter).setContacts(contacts)
            } else {
                // отображаем сообщение, что контактов локальных нет и их необходимо
                // добавить

            }
        }
    }

    private fun addContactsClick() {
        MaterialAlertDialogBuilder(requireContext(), 0)
            .setTitle(getString(R.string.select_new_contact_source))
            .setMessage(getString(R.string.select_add_contact_method))
            .setNeutralButton(resources.getString(R.string.create)) { _, _ ->
                // создание нового контакта
                val resultKey = "Contact"
                parentFragmentManager.setFragmentResultListener(resultKey, this) { _, bundle ->
                    addNewContact(bundle.getString("name")?:"", bundle.getString("phone")?:"")
                }
                parentFragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragmentContainerView, NewPhoneFragment.newInstance(resultKey))
                    .addToBackStack("")
                    .commit()
            }
            .setPositiveButton(resources.getString(R.string.fromDevice)) { _, _ ->
                // загрузка контактов из телефона и отображение их в списке
                reqRunTimePermission(requireActivity(), loadContactsFromDeviceLauncher, arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS))
            }
            .show()
    }

    private fun setRecycleViewVisible(value: Boolean) {
        if (value) {
            binding.ContactListRV.visibility = View.VISIBLE
            binding.NoContactsTextView.visibility = View.GONE
        } else {
            binding.ContactListRV.visibility = View.GONE
            binding.NoContactsTextView.visibility = View.VISIBLE
        }
    }

    private fun addNewContact(name: String, phone: String) {
        if (name != "" && phone != "") {
            _newContactName = name
            _newContactPhone = phone
            reqRunTimePermission(requireActivity(), saveContactsToDeviceLauncher, arrayOf(Manifest.permission.WRITE_CONTACTS))
        }
    }

    private fun loadContactsFromDevice() {
        val resolver = requireContext().contentResolver
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        resolver.query(uri, projection, null, null, null)?.let {cursor ->
            cursor.moveToFirst()
            val nameColInd = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numColInd = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val contacts = mutableListOf<Contact>()
            do {
                val name = cursor.getString(nameColInd)
                val number = cursor.getString(numColInd)
                contacts.add(Contact(name, number))
            } while(cursor.moveToNext())
            _viewModel.contactsLiveData.value = contacts
            cursor.close()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}