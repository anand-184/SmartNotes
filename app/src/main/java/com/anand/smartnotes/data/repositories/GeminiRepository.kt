package com.anand.smartnotes.data.repositories




import android.text.TextUtils.replace
import android.util.Log
import com.anand.smartnotes.BuildConfig
import com.anand.smartnotes.data.dataclasses.QuestionAnswer
import com.anand.smartnotes.data.dataclasses.University
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
        university: String,
        program: String,
        semester: String,
        batch: String
    ): Result<AIResponse> {
        return try {
            val prompt = """
                You are a highly intelligent educational AI assistant working with student notes.

                Student context:
                - University: $university
                - Program: $program
                - Semester: $semester
                - Batch: $batch

                Instructions:
                1. Based on the student's university, program, semester, and batch, autonomously locate the official syllabus curriculum for that combination by searching the official university websites or databases accessible to you.
                2. Thoroughly analyze the identified syllabus topics, units, and chapters.
                3. Analyze the student's handwritten notes (OCR extracted text below):
                   "$extractedText"
                4. Determine if the notes content is relevant and belongs to any of the syllabus topics found.
                5. If the content is relevant (matchFound = true):
                   - Identify main matched topics and syllabus chapters.
                   - Generate 5-7 concise summary points aligned strictly with the syllabus.
                   - Create 5 relevant exam-style questions and answers based on matched topics.
                6. If irrelevant or no syllabus match is found (matchFound = false):
                   - Respond that the uploaded content does not correspond to the identified syllabus.
                   - Provide a brief summary of the uploaded note content only.
                   - Do not generate exam questions.
                7. Return ONLY structured JSON in this format:

                {
                  "matchFound": true | false,
                  "message": "If false, provide this message explaining no syllabus match was found.",
                  "topic": "Matched topic or null",
                  "syllabusChapter": "Matched chapter or null",
                  "summary": [
                    "Summary bullet 1",
                    "Summary bullet 2",
                    ...
                  ],
                  "questions": [
                    {"question": "...", "answer": "..."},
                    ...
                  ]
                }

                Return ONLY valid JSON. Do NOT ask for the syllabus URL or include it in your prompt input.

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