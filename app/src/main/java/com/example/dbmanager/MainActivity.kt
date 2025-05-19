package com.example.dbmanager

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dbmanager.ui.theme.DbmanagerTheme
import androidx.navigation.compose.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color


sealed class Pantalla(val ruta: String) {
    // pantallas
    object Principal : Pantalla("principal")
    object List : Pantalla("lista")
    object Create : Pantalla("crear")
    object Delete : Pantalla("eliminar")
    object Update : Pantalla("actualizar")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val db = DatabaseOpenHelper(this)
            DbmanagerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding), db = db
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier, db: DatabaseOpenHelper) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Pantalla.Principal.ruta)
    {
        composable(Pantalla.Principal.ruta) {
            PrincipalScreen(navController, db)
        }
        composable(Pantalla.List.ruta){
            ListScreen(navController, db)
        }
        composable(Pantalla.Create.ruta){
            CrearScreen(navController, db)
        }
        composable(Pantalla.Delete.ruta){
            DeleteScreen(navController, db)
        }
        composable(Pantalla.Update.ruta){
            UpdateScreen(navController, db)
        }
    }
}


@Composable
fun PrincipalScreen(navController: NavController, db : DatabaseOpenHelper){
    Box(contentAlignment = Alignment.Center, modifier =
        Modifier
            .fillMaxSize()
            .padding(16.dp)){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                "Bienvenido al DBManager",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Button(onClick = {
                navController.navigate(Pantalla.List.ruta)
            }) {
                Text("Lista Usuarios")
            }

            Button(onClick = {
            navController.navigate(Pantalla.Create.ruta)
            }) {
            Text("Crear Usuario")
            }
            Button(onClick = {
                navController.navigate(Pantalla.Update.ruta)
            }) {
                Text("Actualizar Usuario")
            }
            Button(onClick = {
                navController.navigate(Pantalla.Delete.ruta)
            }) {
                Text("Eliminar Usuario")
            }
        }//fin Colum
    }//fin box
}

@Composable
fun ListScreen(navController: NavController, db: DatabaseOpenHelper) {
    val users = db.getAllUsers()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        Column ( horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            "Lista de Usuarios",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray)
                    .padding(5.dp)
            ) {
                TableCell("Id")
                TableCell("Nombre")
                TableCell("Edad")
                TableCell("Género")
                TableCell("Teléfono")
                TableCell("Email")
            }

            // Lista de usuarios
            LazyColumn(modifier = Modifier.background(Color.Blue)) {
                items(users) { user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        TableCell("${user["id"]}")
                        TableCell("${user["first_name"]} ${user["last_name"]}")
                        TableCell("${user["age"]}")
                        TableCell("${user["gender"]}")
                        TableCell("${user["phone"]}")
                        TableCell("${user["email"]}")
                    }
                }
            }
        }
    }
}


@Composable
fun UserRow(user: Map<String, Any>, db: DatabaseOpenHelper, onDelete: (String) -> Unit) {
    val coroutineScope = rememberCoroutineScope()

//    Button(
//        onClick = {
//            coroutineScope.launch {
//                db.deleteUser(user["id"].toString())
//                onDelete(user["id"].toString()?:"")
//            }
//        }
//    ) {
        Row {
            TableCell("${user["id"]}")
            TableCell("${user["first_name"]} ${user["last_name"]}")
            TableCell("${user["age"]}")
            TableCell("${user["gender"]}")
            TableCell("${user["phone"]}")
            TableCell("${user["email"]}")
        }
//    }
}

@Composable
fun TableCell(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .width(60.dp)
            .padding(4.dp)
//            .background(Color.White)
            ,
        maxLines = 1
    )
}

@Composable
fun CrearScreen( navController: NavController, db: DatabaseOpenHelper){
    Box(contentAlignment = Alignment.Center, modifier =
        Modifier
            .fillMaxSize()
            .padding(16.dp)) {
        var firstName by remember { mutableStateOf("") }
        var lastName by remember { mutableStateOf("") }
        var age by remember { mutableStateOf("") }
        var gender by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }

        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Crear nuevo usuario", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Nombre") })
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Apellido") })
            OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Edad") })
            OutlinedTextField(
                value = gender,
                onValueChange = { gender = it },
                label = { Text("Género") })
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Teléfono") })
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") })

            Button(onClick = {
                val ageInt = age.toIntOrNull()
                if (ageInt != null && firstName.isNotBlank() && lastName.isNotBlank()) {
                    db.insertUser(firstName, lastName, ageInt, gender, phone, email)
                    navController.navigate(Pantalla.Principal.ruta) // volver a la pantalla anterior
                }
            }, modifier = Modifier.padding(top = 16.dp)) {
                Text("Crear")
            }
        }
    }
}
@Composable
fun UpdateScreen( navController: NavController, db: DatabaseOpenHelper){
    Box(contentAlignment = Alignment.Center, modifier =
        Modifier
            .fillMaxSize()
            .padding(16.dp)) {
        var id by remember { mutableStateOf("") }
        var firstName by remember { mutableStateOf("") }
        var lastName by remember { mutableStateOf("") }
        var age by remember { mutableStateOf("") }
        var gender by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }

        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Actualizar usuario", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = id,
                onValueChange = { id = it },
                label = { Text("id") })
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Nombre") })
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Apellido") })
            OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Edad") })
            OutlinedTextField(
                value = gender,
                onValueChange = { gender = it },
                label = { Text("Género") })
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Teléfono") })
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") })

            Button(onClick = {
                val ageInt = age.toIntOrNull()
                if (ageInt != null && firstName.isNotBlank() && lastName.isNotBlank()) {
                    db.updateUser(id.toInt(), firstName, lastName, ageInt, gender, phone, email)
                    navController.navigate(Pantalla.Principal.ruta) // volver a la pantalla anterior
                }
            }, modifier = Modifier.padding(top = 16.dp)) {
                Text("Actualizar")
            }
        }
    }
}

@Composable
fun DeleteScreen(navController: NavController, db: DatabaseOpenHelper) {
    Box(
        contentAlignment = Alignment.Center, modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
    ) {
        var id by remember { mutableStateOf("") }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Eliminar usuario", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = id,
                onValueChange = { id = it },
                label = { Text("id") })
            Button(onClick = {
                db.deleteUser(id)
            }) {
                Text("Eliminar")
            }
        }

    }
}

class DatabaseOpenHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
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
        // onCreate(db)
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
            COLUMN_GENDER, COLUMN_PHONE, COLUMN_EMAIL), null, null, null, null,
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

    fun deleteUser(id: String): Boolean {
        val db = writableDatabase
        return try {
            val result = db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
            db.close()
            result > 0
        } catch (e: Exception) {
            db.close()
            false
        }
    }

    fun updateUser(id: Int, firstname: String, lastname: String, age: Int, gender: String, phone: String, email: String): Boolean{
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FIRSTNAME, firstname)
            put(COLUMN_LASTNAME, lastname)
            put(COLUMN_AGE, age)
            put(COLUMN_GENDER, gender)
            put(COLUMN_PHONE, phone)
            put(COLUMN_EMAIL, email)
        }
        return try{
            val result = db.update(TABLE_NAME, values, "$COLUMN_ID=?", arrayOf(id.toString()))
            db.close()
            result > 0
        }
        catch (e: Exception){
            db.close()
            false
        }
    }
}
