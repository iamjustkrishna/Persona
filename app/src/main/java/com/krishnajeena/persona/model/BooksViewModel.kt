package com.krishnajeena.persona.model

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krishnajeena.persona.network.GutendexBook
import com.krishnajeena.persona.network.RetrofitInstanceGutendex
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class BooksViewModel(context: Context) : ViewModel() {

    private val _pdfList = MutableLiveData<List<File>>(emptyList())
    val pdfList : LiveData<List<File>> get() = _pdfList

    // Online books state
    var onlineBooks by mutableStateOf<List<GutendexBook>>(emptyList())
        private set

    var isLoadingOnlineBooks by mutableStateOf(false)
        private set

    var onlineBooksError by mutableStateOf<String?>(null)
        private set

    var searchQuery by mutableStateOf("")
        private set

    init{
        viewModelScope.launch {
            _pdfList.value = loadBooks(context)
            fetchPopularBooks()
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
        if (query.length >= 2) {
            searchOnlineBooks(query)
        } else if (query.isEmpty()) {
            fetchPopularBooks()
        }
    }

    fun fetchPopularBooks() {
        viewModelScope.launch {
            isLoadingOnlineBooks = true
            onlineBooksError = null
            try {
                val response = RetrofitInstanceGutendex.api.getPopularBooks()
                onlineBooks = response.results
            } catch (e: Exception) {
                onlineBooksError = "Failed to load books"
                onlineBooks = emptyList()
            } finally {
                isLoadingOnlineBooks = false
            }
        }
    }

    private fun searchOnlineBooks(query: String) {
        viewModelScope.launch {
            isLoadingOnlineBooks = true
            onlineBooksError = null
            try {
                val response = RetrofitInstanceGutendex.api.getBooks(search = query)
                onlineBooks = response.results
            } catch (e: Exception) {
                onlineBooksError = "Search failed"
            } finally {
                isLoadingOnlineBooks = false
            }
        }
    }
    fun savePdfToAppDirectory(context: Context, pdfUri: Uri) {
    val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(pdfUri)

        val pdfDir =
            File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "PersonaPdfs")
        if (!pdfDir.exists()) pdfDir.mkdirs()

        val fileName = getFileName(contentResolver, pdfUri)
        val pdfFile = File(pdfDir, fileName)

        if(pdfFile.exists()){
            Toast.makeText(context, "This is already in!", Toast.LENGTH_SHORT).show()

        }
else {
            inputStream?.use { input ->
                FileOutputStream(pdfFile).use { output ->

                    copyFile(input, output)

                }
            }

            _pdfList.value = loadBooks(context)
        }
      //  }
    }

    fun removePdfFromAppDirectory(context: Context, pdfUri: Uri) {
//        val contentResolver = context.contentResolver
//        contentResolver.delete(pdfUri, null, null)

        viewModelScope.launch{
        val file = pdfUri.path?.let { File(it) }
        if (file != null) {
            if(file.exists()) {
                _pdfList.value = _pdfList.value?.filter{it != file}
                if (file.delete()) Toast.makeText(context, "File removed", Toast.LENGTH_SHORT)
                    .show()

            }
        }
        }
        // Alternatively, manually remove from the file system if managing local files
    }


    private fun getFileName(contentResolver: ContentResolver, uri: Uri): String {
        var name = ""
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                if (nameIndex >= 0) {
                    name = it.getString(nameIndex)
                }
            }
        }
        return name
    }

    // Helper function to copy the file from input stream to output stream
    private fun copyFile(input: InputStream, output: OutputStream) {
      viewModelScope.launch{

        val buffer = ByteArray(1024)
        var length: Int
        while (input.read(buffer).also { length = it } > 0) {
            output.write(buffer, 0, length)
        }
      }
    }

    private fun loadBooks(context: Context) : List<File> {

        val pdfDir =
            File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "PersonaPdfs")
        if(pdfDir.exists()){
            return pdfDir.listFiles()?.toList() ?: emptyList()
        }
        return emptyList()
    }

    //companion object {
        fun isEmpty(): Boolean {
return _pdfList.value?.isEmpty() ?: true
        }
    //}

}
