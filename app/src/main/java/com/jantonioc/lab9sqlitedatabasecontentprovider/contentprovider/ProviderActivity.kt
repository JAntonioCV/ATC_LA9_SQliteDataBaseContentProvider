package com.jantonioc.lab9sqlitedatabasecontentprovider.contentprovider

import android.annotation.SuppressLint
import android.content.ContentValues
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.jantonioc.lab9sqlitedatabasecontentprovider.R

class ProviderActivity : AppCompatActivity() {
    private lateinit var etname: EditText
    private lateinit var etNickname: EditText
    private lateinit var btnAdd:Button
    private lateinit var btnShow:Button
    private lateinit var btnDelete:Button

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provider)

        etname = findViewById(R.id.etName)
        etNickname = findViewById(R.id.etNickName)
        btnAdd = findViewById(R.id.btnAdd)
        btnShow = findViewById(R.id.btnShow)
        btnDelete = findViewById(R.id.btnDelete)

        btnAdd.setOnClickListener {
            var values = ContentValues()

            if(etname.text.isNotEmpty() && etname.text.isNotEmpty())
            {
                values.put(CustomContentProvider.NAME,etname.text.toString())
                values.put(CustomContentProvider.NICK_NAME,etNickname.text.toString())
                contentResolver.insert(CustomContentProvider.CONTENT_URI,values)
                Toast.makeText(this,"Record Innserted",Toast.LENGTH_SHORT).show()
            }else
            {
                Toast.makeText(this,"Please Enter Records First",Toast.LENGTH_SHORT).show()
            }
        }

        btnShow.setOnClickListener {
            val friends: Uri = CustomContentProvider.CONTENT_URI
            var cursor = contentResolver.query(friends,null,null,null,CustomContentProvider.NAME)
            var result = "Content Provider Results: "

            if(!cursor?.moveToFirst()!!)
            {
                Toast.makeText(this,"Please Enter Records First",Toast.LENGTH_SHORT).show()
            }
            else
            {
                do {
                    result += "\n${cursor.getString(cursor.getColumnIndex(CustomContentProvider.NAME))} with id: " +
                            "${cursor.getString(cursor.getColumnIndex(CustomContentProvider.ID))} has NickName: " +
                            "${cursor.getString(cursor.getColumnIndex(CustomContentProvider.NICK_NAME))}."
                } while (cursor.moveToNext())

                if(result.isNotEmpty())
                {
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show()
                }
                else
                {
                    Toast.makeText(this,"No records present" , Toast.LENGTH_SHORT).show()
                }

            }
        }

        btnDelete.setOnClickListener {
            val count = contentResolver.delete(CustomContentProvider.CONTENT_URI, null, null)
            Toast.makeText(this, "$count records are deleted", Toast.LENGTH_LONG).show()
        }


    }
}