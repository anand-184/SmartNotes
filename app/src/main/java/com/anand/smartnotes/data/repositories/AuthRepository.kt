package com.anand.smartnotes.data.repositories

import com.anand.smartnotes.data.dataclasses.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    suspend fun registerUser(
        userEmail: String,
        password: String,
        userName: String,
        university: String,
        program: String,
        semester: String
    ): Result<FirebaseUser> {
        return try {
            val authResult = auth?.createUserWithEmailAndPassword(userEmail, password)?.await()
            val firebaseUser = authResult?.user!!

            val user = User(
                id = firebaseUser.uid,
                userEmail = userEmail,
                userPassword = password,
                userName = userName,
                university = university,
                program = program,
                semester = semester
            )
            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(user)
                .await()
            Result.success(firebaseUser)
        }catch (e:Exception){
            Result.failure(e)
        }

    }

    suspend fun loginUser(userEmail: String,password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(userEmail, password).await()
            Result.success(authResult.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    fun logOutUser() {
        auth.signOut()
    }

}