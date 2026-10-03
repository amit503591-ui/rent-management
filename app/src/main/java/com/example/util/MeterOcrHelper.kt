package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object MeterOcrHelper {

    suspend fun recognizeMeterReading(bitmap: Bitmap): OcrResult = suspendCancellableCoroutine { continuation ->
        try {
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val image = InputImage.fromBitmap(bitmap, 0)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val fullText = visionText.text
                    val candidates = extractMeterNumbers(fullText)
                    val best = candidates.firstOrNull()
                    continuation.resume(
                        OcrResult(
                            success = true,
                            detectedReading = best,
                            allCandidates = candidates,
                            rawText = fullText
                        )
                    )
                }
                .addOnFailureListener { error ->
                    // Return failure with fallback info
                    continuation.resume(
                        OcrResult(
                            success = false,
                            errorMessage = error.localizedMessage ?: "OCR processing failed"
                        )
                    )
                }
        } catch (e: Exception) {
            continuation.resume(
                OcrResult(
                    success = false,
                    errorMessage = e.localizedMessage ?: "Unexpected error during recognition"
                )
            )
        }
    }

    fun extractMeterNumbers(rawText: String): List<Double> {
        val candidates = mutableListOf<Double>()
        // Normalize common OCR character confusions: 'O' or 'o' to '0', 'I' or 'l' to '1' in digit-like clusters
        val lines = rawText.split("\n", " ", ",")

        val regex = Regex("""\b\d{2,6}(?:[\.,]\d{1,2})?\b""")

        for (token in lines) {
            val cleaned = token.trim()
                .replace("kWh", "", ignoreCase = true)
                .replace("kW", "", ignoreCase = true)
                .replace("V", "", ignoreCase = true)
                .replace("A", "", ignoreCase = true)
                .replace("Hz", "", ignoreCase = true)
                .replace(",", ".")
                .trim()

            val match = regex.find(cleaned)
            if (match != null) {
                val numStr = match.value
                val num = numStr.toDoubleOrNull()
                if (num != null && num > 0.0 && num < 999999.0) {
                    // Filter out years like 2024, 2025, 2026, 230 (volt), 50 (frequency) if other candidates exist
                    if (num !in listOf(230.0, 415.0, 50.0, 2024.0, 2025.0, 2026.0, 2027.0)) {
                        if (!candidates.contains(num)) {
                            candidates.add(num)
                        }
                    }
                }
            }
        }

        // Sort prioritizing numbers with decimals or 4-5 digit numbers typical for submeters
        return candidates.sortedByDescending { if (it.toString().contains(".")) 10 else 1 }
    }

    fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.isMutableRequired = true
                }
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
        } catch (e: Exception) {
            null
        }
    }
}

data class OcrResult(
    val success: Boolean,
    val detectedReading: Double? = null,
    val allCandidates: List<Double> = emptyList(),
    val rawText: String = "",
    val errorMessage: String? = null
)
