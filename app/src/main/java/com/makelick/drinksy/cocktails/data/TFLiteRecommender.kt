package com.makelick.drinksy.cocktails.data

import android.content.Context
import com.makelick.drinksy.profile.data.User
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject

class TFLiteRecommender @Inject constructor(@ApplicationContext private val context: Context) {
    private val interpreter: Interpreter
    private val metadata: JSONObject
    private val cocktailIds: List<String>
    private val allTags: List<String>
    private val allCategories: List<String>
    private val scalerMean: List<Double>
    private val scalerScale: List<Double>
    private val featureDim: Int
    private val nCocktails: Int

    init {
        interpreter = Interpreter(loadModelFile(context))

        val metaString = context.assets.open("fuzzy_cocktail_knn_metadata.json")
            .bufferedReader().use { it.readText() }
        metadata = JSONObject(metaString)
        cocktailIds = metadata.getJSONArray("cocktail_ids").let { arr ->
            List(arr.length()) { arr.getString(it) }
        }
        allTags = metadata.getJSONArray("all_tags").let { arr ->
            List(arr.length()) { arr.getString(it) }
        }
        allCategories = metadata.getJSONArray("all_categories").let { arr ->
            List(arr.length()) { arr.getString(it) }
        }
        scalerMean = metadata.getJSONArray("scaler_mean").let { arr ->
            List(arr.length()) { arr.getDouble(it) }
        }
        scalerScale = metadata.getJSONArray("scaler_scale").let { arr ->
            List(arr.length()) { arr.getDouble(it) }
        }
        featureDim = metadata.getInt("feature_dim")
        nCocktails = metadata.getInt("n_cocktails")
    }

    private fun loadModelFile(
        context: Context,
        filename: String = "fuzzy_cocktail_knn.tflite"
    ): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(filename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun encodeUser(user: User): FloatArray {
        val tasteVector = FloatArray(allTags.size) { tagIdx ->
            if (user.tastes.contains(allTags[tagIdx])) 1f else 0f
        }
        val categoryZeros = FloatArray(allCategories.size)
        val ingredientCountZero = floatArrayOf(0f)
        val userFeatures = tasteVector + categoryZeros + ingredientCountZero
        return userFeatures.mapIndexed { i, v ->
            ((v - scalerMean[i]) / scalerScale[i]).toFloat()
        }.toFloatArray()
    }

    private fun loadCocktailFeatures(context: Context): Array<FloatArray> {
        val json = context.assets.open("fuzzy_cocktail_knn_cocktail_features.json")
            .bufferedReader().use { it.readText() }
        val arr = org.json.JSONArray(json)
        return Array(arr.length()) { i ->
            val row = arr.getJSONArray(i)
            FloatArray(row.length()) { j -> row.getDouble(j).toFloat() }
        }
    }

    fun recommend(user: User, topN: Int = 100): List<Pair<String, Float>> {
        val userFeatures = encodeUser(user)
        val cocktailFeatures = loadCocktailFeatures(context)

        val userInput = arrayOf(userFeatures)
        val cocktailInput = arrayOf(cocktailFeatures)

        val output = Array(1) { FloatArray(nCocktails) }

        interpreter.runForMultipleInputsOutputs(
            arrayOf(userInput, cocktailInput),
            mapOf(0 to output)
        )

        val favoriteSet = user.favoriteCocktails.toSet()
        val scores = output[0].mapIndexed { idx, score ->
            if (cocktailIds[idx] in favoriteSet) 0f else score
        }

        return scores.withIndex()
            .sortedByDescending { it.value }
            .take(topN)
            .map { Pair(cocktailIds[it.index], it.value) }
    }
}
