package au.com.jayne.dogquiz.common.util

import android.content.SharedPreferences
import au.com.jayne.dogquiz.domain.model.Game
import au.com.jayne.dogquiz.domain.model.GameScores
import au.com.jayne.dogquiz.domain.model.HighScore
import au.com.jayne.dogquiz.domain.model.SharedPreferenceKey
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class SharedPreferenceStorageTest {

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var mockSharedPreferences: SharedPreferences

    @Mock
    lateinit var mockSharedPreferencesEditor: SharedPreferences.Editor

    lateinit var sharedPreferenceStorage: SharedPreferenceStorage

    @Before
    fun setup() {
        sharedPreferenceStorage = SharedPreferenceStorageImpl(mockSharedPreferences)
    }

    @Test
    fun testIsSoundEnabled() {
        whenever(mockSharedPreferences.getBoolean(SharedPreferenceKey.SOUNDS_ENABLED.keyName, true)).thenReturn(true)

        Assert.assertTrue(sharedPreferenceStorage.isSoundEnabled())

        whenever(mockSharedPreferences.getBoolean(SharedPreferenceKey.SOUNDS_ENABLED.keyName, true)).thenReturn(false)

        Assert.assertFalse(sharedPreferenceStorage.isSoundEnabled())
    }

    @Test
    fun testResetHighScores() {
        whenever(mockSharedPreferences.edit()).thenReturn(mockSharedPreferencesEditor)
        whenever(mockSharedPreferencesEditor.remove(SharedPreferenceKey.GAME_SCORES.keyName)).thenReturn(mockSharedPreferencesEditor)
        whenever(mockSharedPreferencesEditor.commit()).thenReturn(true)

        sharedPreferenceStorage.resetHighScores()

        verify(mockSharedPreferencesEditor, times(1)).remove(SharedPreferenceKey.GAME_SCORES.keyName)
        verify(mockSharedPreferencesEditor, times(1)).commit()
    }

    @Test
    fun testSetGameScore_NoPreviousGameScoresStored() {
        val name = "Julie"
        val score = 12

        whenever(mockSharedPreferences.getString(SharedPreferenceKey.GAME_SCORES.keyName, null)).thenReturn(null)
        whenever(mockSharedPreferences.edit()).thenReturn(mockSharedPreferencesEditor)
        whenever(mockSharedPreferencesEditor.putString(any(), any())).thenReturn(mockSharedPreferencesEditor)

        sharedPreferenceStorage.setGameScore(Game.TERRIER_QUIZ, HighScore(name, score))

        verify(mockSharedPreferences, times(2)).edit()
        verify(mockSharedPreferencesEditor, times(2)).putString(any(), any())

        verify(mockSharedPreferencesEditor).putString(SharedPreferenceKey.GAME_SCORES.keyName, "{\"highScoresMap\":{\"TERRIER_QUIZ\":{\"name\":\"Julie\",\"score\":12}}}")
        verify(mockSharedPreferencesEditor).putString(SharedPreferenceKey.LAST_NAME_USED.keyName, name)

        verify(mockSharedPreferencesEditor, times(2)).apply()
    }

    @Test
    fun testSetGameScore_WithPreviousSameGameScoresStored() {
        val name = "Max"
        val score = 14

        whenever(mockSharedPreferences.getString(SharedPreferenceKey.GAME_SCORES.keyName, null)).thenReturn("{\"highScoresMap\":{\"TERRIER_QUIZ\":{\"name\":\"Julie\",\"score\":12}}}")
        whenever(mockSharedPreferences.edit()).thenReturn(mockSharedPreferencesEditor)
        whenever(mockSharedPreferencesEditor.putString(any(), any())).thenReturn(mockSharedPreferencesEditor)

        sharedPreferenceStorage.setGameScore(Game.TERRIER_QUIZ, HighScore(name, score))

        verify(mockSharedPreferences, times(2)).edit()
        verify(mockSharedPreferencesEditor, times(2)).putString(any(), any())

        verify(mockSharedPreferencesEditor).putString(SharedPreferenceKey.GAME_SCORES.keyName, "{\"highScoresMap\":{\"TERRIER_QUIZ\":{\"name\":\"Max\",\"score\":14}}}")
        verify(mockSharedPreferencesEditor).putString(SharedPreferenceKey.LAST_NAME_USED.keyName, name)

        verify(mockSharedPreferencesEditor, times(2)).apply()
    }

    @Test
    fun testSetGameScore_WithDiffPreviousGameScoresStored() {
        val name = "Tony"
        val score = 2

        whenever(mockSharedPreferences.getString(SharedPreferenceKey.GAME_SCORES.keyName, null)).thenReturn("{\"highScoresMap\":{\"TERRIER_QUIZ\":{\"name\":\"Julie\",\"score\":12}}}")
        whenever(mockSharedPreferences.edit()).thenReturn(mockSharedPreferencesEditor)
        whenever(mockSharedPreferencesEditor.putString(any(), any())).thenReturn(mockSharedPreferencesEditor)

        sharedPreferenceStorage.setGameScore(Game.SPANIEL_QUIZ, HighScore(name, score))

        verify(mockSharedPreferences, times(2)).edit()
        verify(mockSharedPreferencesEditor, times(2)).putString(any(), any())

        verify(mockSharedPreferencesEditor).putString(SharedPreferenceKey.LAST_NAME_USED.keyName, name)

        verify(mockSharedPreferencesEditor, times(2)).apply()
    }

    @Test
    fun testGetGameScores() {
        val highScoresMap = HashMap<Game, HighScore>()
        highScoresMap.put(Game.SPANIEL_QUIZ, HighScore("Spaniel Boy", 5))
        highScoresMap.put(Game.HOUND_QUIZ, HighScore("Hounder", 10))
        highScoresMap.put(Game.TERRIER_QUIZ, HighScore("Terrier Girl", 6))
        highScoresMap.put(Game.DOGGY_QUIZ, HighScore("Grrrreat Dog", 15))
        val expectedGameScores = GameScores(highScoresMap)

        val gameScoresJSON = "{\"highScoresMap\":{" +
                                                "\"SPANIEL_QUIZ\":{\"name\":\"Spaniel Boy\",\"score\":5}," +
                                                "\"HOUND_QUIZ\":{\"name\":\"Hounder\",\"score\":10}," +
                                                "\"TERRIER_QUIZ\":{\"name\":\"Terrier Girl\",\"score\":6}," +
                                                "\"DOGGY_QUIZ\":{\"name\":\"Grrrreat Dog\",\"score\":15}" +
                                                "}"+
                             "}"
        whenever(mockSharedPreferences.getString(SharedPreferenceKey.GAME_SCORES.keyName, null)).thenReturn(gameScoresJSON)

        Assert.assertEquals(expectedGameScores, sharedPreferenceStorage.getGameScores())
    }

    @Test
    fun testGetLastNameUsed() {
        val name = "Joey"

        whenever(mockSharedPreferences.getString(SharedPreferenceKey.LAST_NAME_USED.keyName, null)).thenReturn(name)

        Assert.assertEquals(name, sharedPreferenceStorage.getLastNameUsed())

        verify(mockSharedPreferences, times(1)).getString(SharedPreferenceKey.LAST_NAME_USED.keyName, null)
    }

    @Test
    fun testSetLastNameUsed() {
        val name = "Joey"
        whenever(mockSharedPreferences.edit()).thenReturn(mockSharedPreferencesEditor)
        whenever(mockSharedPreferencesEditor.putString(SharedPreferenceKey.LAST_NAME_USED.keyName, name)).thenReturn(mockSharedPreferencesEditor)

        sharedPreferenceStorage.setLastNameUsed(name)

        verify(mockSharedPreferences, times(1)).edit()
        verify(mockSharedPreferencesEditor, times(1)).putString(SharedPreferenceKey.LAST_NAME_USED.keyName, name)
        verify(mockSharedPreferencesEditor, times(1)).apply()
    }

}