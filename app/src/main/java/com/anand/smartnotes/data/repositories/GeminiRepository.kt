package com.anand.smartnotes.data.repositories




import android.text.TextUtils.replace
import android.util.Log
import com.anand.smartnotes.BuildConfig
import com.anand.smartnotes.data.dataclasses.QuestionAnswer
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import org.json.JSONArray
import org.json.JSONObject

data class AIResponse(
    val summary: List<String>,
    val questions: List<QuestionAnswer>,
    val topic: String,
    val syllabusChapter: String
)

class GeminiRepository{

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash-001",
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = generationConfig {
            temperature = 0.7f
            topK = 40
            topP = 0.95f
        },safetySettings = listOf(
            SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE),
        )
    )



    suspend fun generateSummaryAndQuestions(
        extractedText: String,
        syllabusUrl: String,
        university: String,
        program: String,
        semester: String
    ): Result<AIResponse> {
        return try {
            val prompt = """
You are an educational AI assistant for $university - $program - $semester students.

Reference Syllabus PDF: $syllabusUrl
(Read and understand the syllabus topics from this URL)

Student's handwritten notes (OCR extracted):
"$extractedText"

Tasks:
1. Identify the main TOPIC from the notes 
2. Find which CHAPTER/UNIT from the syllabus this topic belongs to
3. Create a SUMMARY of the notes in 10-12 bullet points
4. Generate 5 EXAM-STYLE QUESTIONS with detailed answers based on syllabus topics

Return response in this EXACT JSON format:
{
  "topic": "Main topic name",
  "syllabusChapter": "Unit/Chapter name from syllabus",
  "summary": [
    "Summary point 1",
    "Summary point 2",
    "Summary point 3",
    "Summary point 4",
    "Summary point 5"
  ],
  "questions": [
    {
      "question": "Question 1?",
      "answer": "Detailed answer 1"
    },
    {
      "question": "Question 2?",
      "answer": "Detailed answer 2"
    },
    {
      "question": "Question 3?",
      "answer": "Detailed answer 3"
    },
    {
      "question": "Question 4?",
      "answer": "Detailed answer 4"
    },
    {
      "question": "Question 5?",
      "answer": "Detailed answer 5"
    }
  ]
}

Return ONLY valid JSON, no additional text.
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            val jsonText = response.text ?: throw Exception("Empty response from AI")
                        Log.d("AI_RAW_RESPONSE", jsonText)
            val cleanJson = jsonText
                .replace("```json", "", ignoreCase = true)
                .replace("```", "")
                .replace("'''", "")
                .trim()


            val aiResponse = parseAIResponse(cleanJson)
            return Result.success(aiResponse)




        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseAIResponse(jsonText: String): AIResponse {
        // Clean JSON (remove markdown code blocks if present)
        val json = JSONObject(jsonText)
        val cleanJson=jsonText.replace("'''","").trim()



        // Parse summary
        val summaryArray = json.getJSONArray("summary")
        val summary = List(summaryArray.length()) {
            summaryArray.getString(it)
        }

        // Parse questions
        val questionsArray = json.getJSONArray("questions")
        val questions = List(questionsArray.length()) { i ->
            val qa = questionsArray.getJSONObject(i)
            QuestionAnswer(
                question = qa.getString("question"),
                answer = qa.getString("answer")
            )
        }

        return AIResponse(
            topic = json.getString("topic"),
            syllabusChapter = json.getString("syllabusChapter"),
            summary = summary,
            questions = questions
        )
    }


}