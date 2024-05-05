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
    val jawaban: List<Jawaban?>?,
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
        @SerializedName("value_1")
        val value1: String?,
        @SerializedName("value_10")
        val value10: String?,
        @SerializedName("value_11")
        val value11: String?,
        @SerializedName("value_12")
        val value12: String?,
        @SerializedName("value_13")
        val value13: String?,
        @SerializedName("value_14")
        val value14: String?,
        @SerializedName("value_15")
        val value15: String?,
        @SerializedName("value_16")
        val value16: String?,
        @SerializedName("value_17")
        val value17: String?,
        @SerializedName("value_18")
        val value18: String?,
        @SerializedName("value_19")
        val value19: String?,
        @SerializedName("value_2")
        val value2: String?,
        @SerializedName("value_20")
        val value20: String?,
        @SerializedName("value_3")
        val value3: String?,
        @SerializedName("value_4")
        val value4: String?,
        @SerializedName("value_5")
        val value5: String?,
        @SerializedName("value_6")
        val value6: String?,
        @SerializedName("value_7")
        val value7: String?,
        @SerializedName("value_8")
        val value8: String?,
        @SerializedName("value_9")
        val value9: String?
    ) : Parcelable
}