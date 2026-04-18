package com.krishnajeena.persona.other

import android.content.Context
import com.krishnajeena.persona.data_layer.DailyQuote
import com.krishnajeena.persona.network.RetrofitClientQuote
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withTimeout
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class QuoteRepository @Inject constructor(
    @ApplicationContext private val context: Context // Use ApplicationContext with Hilt
) {

    private val prefs = context.getSharedPreferences("daily_quote", Context.MODE_PRIVATE)

    // Fallback quotes collection
    private val fallbackQuotes = listOf(
        "The only way to do great work is to love what you do. - Steve Jobs",
        "Innovation distinguishes between a leader and a follower. - Steve Jobs",
        "Stay hungry, stay foolish. - Steve Jobs",
        "The future belongs to those who believe in the beauty of their dreams. - Eleanor Roosevelt",
        "Success is not final, failure is not fatal: it is the courage to continue that counts. - Winston Churchill",
        "Believe you can and you're halfway there. - Theodore Roosevelt",
        "The only impossible journey is the one you never begin. - Tony Robbins",
        "Don't watch the clock; do what it does. Keep going. - Sam Levenson",
        "The secret of getting ahead is getting started. - Mark Twain",
        "It's not whether you get knocked down, it's whether you get up. - Vince Lombardi",
        "The only limit to our realization of tomorrow will be our doubts of today. - Franklin D. Roosevelt",
        "Do not wait to strike till the iron is hot, but make it hot by striking. - William Butler Yeats",
        "Everything you've ever wanted is on the other side of fear. - George Addair",
        "Believe in yourself and all that you are. Know that there is something inside you that is greater than any obstacle.",
        "You are never too old to set another goal or to dream a new dream. - C.S. Lewis",
        "The best time to plant a tree was 20 years ago. The second best time is now. - Chinese Proverb",
        "Your time is limited, don't waste it living someone else's life. - Steve Jobs",
        "The only thing standing between you and your goal is the story you keep telling yourself. - Jordan Belfort",
        "Challenges are what make life interesting and overcoming them is what makes life meaningful. - Joshua J. Marine",
        "If you want to lift yourself up, lift up someone else. - Booker T. Washington",
        "I have not failed. I've just found 10,000 ways that won't work. - Thomas Edison",
        "A person who never made a mistake never tried anything new. - Albert Einstein",
        "The person who says it cannot be done should not interrupt the person who is doing it. - Chinese Proverb",
        "There are no traffic jams along the extra mile. - Roger Staubach",
        "It is during our darkest moments that we must focus to see the light. - Aristotle",
        "Start where you are. Use what you have. Do what you can. - Arthur Ashe",
        "You miss 100% of the shots you don't take. - Wayne Gretzky",
        "The best revenge is massive success. - Frank Sinatra",
        "People who are crazy enough to think they can change the world, are the ones who do. - Rob Siltanen",
        "Failure will never overtake me if my determination to succeed is strong enough. - Og Mandino",
        "We may encounter many defeats but we must not be defeated. - Maya Angelou",
        "Knowing is not enough; we must apply. Wishing is not enough; we must do. - Johann Wolfgang Von Goethe",
        "Imagine your life is perfect in every respect; what would it look like? - Brian Tracy",
        "Whether you think you can or think you can't, you're right. - Henry Ford",
        "Security is mostly a superstition. Life is either a daring adventure or nothing. - Helen Keller",
        "The only person you are destined to become is the person you decide to be. - Ralph Waldo Emerson",
        "Go confidently in the direction of your dreams! Live the life you've imagined. - Henry David Thoreau",
        "Everything has beauty, but not everyone can see. - Confucius",
        "How wonderful it is that nobody need wait a single moment before starting to improve the world. - Anne Frank",
        "When I let go of what I am, I become what I might be. - Lao Tzu",
        "Life is 10% what happens to me and 90% of how I react to it. - Charles Swindoll",
        "An unexamined life is not worth living. - Socrates",
        "Eighty percent of success is showing up. - Woody Allen",
        "I am not a product of my circumstances. I am a product of my decisions. - Stephen Covey",
        "Every strike brings me closer to the next home run. - Babe Ruth",
        "Life isn't about getting and having, it's about giving and being. - Kevin Kruse",
        "The most difficult thing is the decision to act, the rest is merely tenacity. - Amelia Earhart",
        "Every child is an artist. The problem is how to remain an artist once he grows up. - Pablo Picasso",
        "You can never cross the ocean until you have the courage to lose sight of the shore. - Christopher Columbus",
        "Either you run the day, or the day runs you. - Jim Rohn"
    )

    suspend fun fetchQuote(): DailyQuote {
        val today = LocalDate.now().toString()
        val saved = getSavedQuote()

        // Return saved quote if it's from today
        if (saved?.date == today) {
            return saved
        }

        return try {
            withTimeout(5000L) {
                val response = RetrofitClientQuote.getInstance().getQuoteOfTheDay()
                val newQuote = response.body()?.quote
                if (!newQuote.isNullOrBlank()) {
                    val dailyQuote = DailyQuote(newQuote, today)
                    saveQuote(dailyQuote)
                    dailyQuote
                } else {
                    // API returned empty, use fallback
                    getDailyFallbackQuote(today)
                }
            }
        } catch (e: Exception) {
            // Network error or timeout, use fallback
            saved ?: getDailyFallbackQuote(today)
        }
    }

    /**
     * Get a fallback quote based on the current date
     * This ensures the same quote is shown throughout the day
     */
    private fun getDailyFallbackQuote(today: String): DailyQuote {
        // Use date hash to get consistent quote for the day
        val dateHash = today.hashCode()
        val index = kotlin.math.abs(dateHash % fallbackQuotes.size)
        val quote = fallbackQuotes[index]
        val dailyQuote = DailyQuote(quote, today)
        saveQuote(dailyQuote) // Save for later retrieval
        return dailyQuote
    }

    /**
     * Get a random quote (for other use cases)
     */
    fun getRandomQuote(): String {
        return fallbackQuotes[Random.nextInt(fallbackQuotes.size)]
    }

    private fun saveQuote(quote: DailyQuote) {
        prefs.edit().apply {
            putString("quote_text", quote.text)
            putString("quote_date", quote.date)
            apply()
        }
    }

    private fun getSavedQuote(): DailyQuote? {
        val date = prefs.getString("quote_date", null)
        val text = prefs.getString("quote_text", null)
        return if (date != null && text != null) DailyQuote(text, date) else null
    }
}
