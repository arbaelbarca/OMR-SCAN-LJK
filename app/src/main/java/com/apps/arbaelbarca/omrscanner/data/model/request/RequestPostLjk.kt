package com.apps.arbaelbarca.omrscanner.data.model.request


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Keep
@Parcelize
data class RequestPostLjk(
    @SerializedName("AnswerFalse")
    val answerFalse: String?,
    @SerializedName("AnswerTrue")
    val answerTrue: String?,
    @SerializedName("DateCreated")
    val dateCreated: String?,
    @SerializedName("DateUpdated")
    val dateUpdated: String?,
    @SerializedName("Jawaban")
    val jawaban: MutableList<Jawaban>?,
    @SerializedName("Matkul")
    val matkul: String?,
    @SerializedName("NIK")
    val nIK: String?,
    @SerializedName("Nama")
    val nama: String?,
    @SerializedName("Score")
    val score: String?
) : Parcelable {
    @Keep
    @Parcelize
    data class Jawaban(
        @SerializedName("value")
        val value: String
    ) : Parcelable
}