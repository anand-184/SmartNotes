package com.anand.smartnotes.data.repositories

import com.anand.smartnotes.data.dataclasses.Class
import com.anand.smartnotes.data.dataclasses.Student
import com.anand.smartnotes.data.dataclasses.University
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.tasks.await

class StudentRepository {
    private var db= FirebaseFirestore.getInstance()
    suspend fun getUniversities(): Result<List<University>>{
        return try {
            val snapshot = db.collection("universities").get().await()
            val universities = snapshot.documents.mapNotNull {
                University(id = it.id, name = it.getString("name") ?: "")
        }
            Result.success(universities)

    }catch (e:Exception){
    Result.failure(e)}
    }

    suspend fun getClasses(universityId: String): Result<List<Class>> {
        return try {
            val snapshot = db.collection("universities")
                .document(universityId)
                .collection("classes")
                .get()
                .await()
            val classes = snapshot.documents.mapNotNull {
                Class(id = it.id, name = it.getString("name") ?: "")
            }
            Result.success(classes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun saveStudentContext(student: Student): Result<Unit> {
        return try {
            db.collection("students")
                .document(student.id)
                .set(student)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}