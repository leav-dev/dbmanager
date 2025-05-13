package com.example.dbmanager

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.core.database.getStringOrNull
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import com.example.dbmanager.ui.theme.DbmanagerTheme

sealed class Pantalla(val ruta: String){
    object  Principal : Pantalla ("principal")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DbmanagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DbManagerApp()
                }
            }
        }
    }
}

@Composable
fun DbManagerApp(){
    // definimos el controlador de la navegacion
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Pantalla.Principal.ruta){
        composable(Pantalla.Principal.ruta) {PantallaPrincial(navController)}
    }
}

@Composable
fun PantallaPrincial(navController: NavController){

}

class DatabasOpenHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    companion object{
        private const val DATABASE_NAME = "user_database.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_FIRSTNAME = "first_name"
        private const val COLUMN_LASTNAME = "last_name"
        private const val COLUMN_AGE = "age"
        private const val COLUMN_GENDER = "gender"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_EMAIL = "email"

        private const val CREATE_TABLE = """
            CREATE TABLE $TABLE_NAME(
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_FIRSTNAME TEXT NOT NULL,
            $COLUMN_LASTNAME TEXT NOT NULL,
            $COLUMN_AGE INTEGER NOT NULL,
            $COLUMN_GENDER TEXT NOT NULL,
            $COLUMN_PHONE TEXT NOT NULL,
            $COLUMN_EMAIL TEXT NOT NULL)
        """

    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
        onCreate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertUser(firstname: String, lastname: String, age: Int, gender: String, phone: String, email: String): Boolean{
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FIRSTNAME, firstname)
            put(COLUMN_LASTNAME, lastname)
            put(COLUMN_AGE, age)
            put(COLUMN_GENDER, gender)
            put(COLUMN_PHONE, phone)
            put(COLUMN_EMAIL, email)
        }
        try{
            val result = db.insert(TABLE_NAME, null, values)
            db.close()
            return result != -1L
        }catch (e: Exception){
            db.close()
            return false
        }
    }

    fun getAllUsers():List<Map<String, Any>>{
        val db = readableDatabase
        val usersList = mutableListOf<Map<String, Any>>()

        val cursor = db.query(TABLE_NAME, arrayOf(COLUMN_ID, COLUMN_FIRSTNAME, COLUMN_LASTNAME, COLUMN_AGE,
            COLUMN_GENDER, COLUMN_PHONE, COLUMN_PHONE), null, null, null, null,
            null)
        if (cursor.moveToFirst()){
            do {
                val user = mapOf(COLUMN_ID to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    COLUMN_FIRSTNAME to cursor.getString(cursor.getColumnIndexOrThrow(
                        COLUMN_FIRSTNAME)),
                    COLUMN_LASTNAME to cursor.getString(cursor.getColumnIndexOrThrow(
                        COLUMN_LASTNAME)),
                    COLUMN_AGE to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)),
                    COLUMN_GENDER to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)),
                    COLUMN_PHONE to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                    COLUMN_EMAIL to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
                    )
                usersList.add(user)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return usersList
    }
}

