package com.jantonioc.lab9sqlitedatabasecontentprovider

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.*

class MainActivity : AppCompatActivity() {
    private var myDBAdapter: MyDBAdapter? = null
    private val mAllFaculties = arrayOf("Engineering", "Business", "Arts")
    private lateinit var etStudentName: EditText
    private lateinit var spFaculty: Spinner
    private lateinit var btnAddStudent: Button
    private lateinit var btnDeleteAllStudent: Button
    private lateinit var lvStudent: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etStudentName = findViewById(R.id.etStudentName)
        spFaculty = findViewById(R.id.spFaculties)
        btnAddStudent = findViewById(R.id.btnAddStudent)
        btnDeleteAllStudent = findViewById(R.id.btnDeleteStudents)
        lvStudent = findViewById(R.id.lvStudents)

        spFaculty.adapter = ArrayAdapter(this@MainActivity,android.R.layout.simple_list_item_1,mAllFaculties)
        initializeDatabase()
        loadList()

        btnAddStudent.setOnClickListener {
            if (TextUtils.isEmpty(etStudentName.text.toString())){
                Toast.makeText(this,  "Debe Digitar un nombre", Toast.LENGTH_LONG).show()
            }else
            {
                myDBAdapter?.insertStudent(etStudentName.text.toString(),spFaculty.selectedItemPosition+1)
                loadList()
            }
        }

        btnDeleteAllStudent.setOnClickListener {
            myDBAdapter?.deleteAllStudents()
            loadList()
        }
    }


    private fun initializeDatabase() {
        myDBAdapter = MyDBAdapter(this@MainActivity)
        myDBAdapter?.openDataBase()

    }

    private fun loadList() {
        val allStudents: List<String>? = myDBAdapter?.selectAllStudents()
        val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, allStudents!!)
        lvStudent.adapter = adapter
    }
}