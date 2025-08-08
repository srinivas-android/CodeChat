package com.example.codechat.data.repository

import android.content.Context
import android.net.Uri
import com.example.codechat.core.network.AuthApiService
import com.example.codechat.core.network.ProfileApi
import com.example.codechat.data.model.ProfileImageResponse
import com.example.codechat.domain.model.User
import com.example.codechat.domain.repository.ProfileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileApi: ProfileApi,
    private val authApiService: AuthApiService,
    @ApplicationContext private val context: Context
): ProfileRepository {

    override suspend fun getMyProfile(userId: String): User {
        val response = authApiService.getMyProfile(id = userId)
        return User(
            id = response.id,
            name = response.name ?: "Unknown",
            email = response.email ?: "",
            token = response.token ?: "",
            profileImage = response.profileImage ?: ""
        )
    }

    override suspend fun getUsers(): List<User> = profileApi.getUsers()

    override suspend fun updateProfileImage(
        userId: String,
        profileImage: Uri
    ): ProfileImageResponse {
        val userIdRequestBody = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val inputStream = context.contentResolver.openInputStream(profileImage)
            ?: throw IOException("Unable to open input stream for URI: $profileImage")


        val filePart: MultipartBody.Part = try {
            inputStream.use { stream -> // 'stream' is now a non-null InputStream
                val fileBytes = stream.readBytes()
                val mimeType = context.contentResolver.getType(profileImage)
                val requestFile = fileBytes.toRequestBody(mimeType?.toMediaTypeOrNull())

                // The part name "profileImage" MUST match the @Part name in the ProfileApi interface.
                MultipartBody.Part.createFormData("profile", "profile_image.jpg", requestFile)
            }
        } catch (e: Exception) {
            // Consider more specific logging, e.g., Timber.e(e, "Error creating file part from URI: $imageUri")
            throw IOException("Failed to prepare image for upload: ${e.message}", e)
        }

        // Call the API
        try {
            val response = profileApi.updateProfileImage(userIdRequestBody, filePart)
            // Assuming ProfileImageResponse has a field like `newImageUrl` or similar
            // Adjust this based on your actual ProfileImageResponse structure
            return response ?: throw IOException("Server did not return a new image URL.")
        } catch (e: Exception) {
            // Log.e("ProfileRepository", "Error uploading profile image to server", e)
            throw IOException("Failed to upload image: ${e.message}", e)
        }
    }
}
