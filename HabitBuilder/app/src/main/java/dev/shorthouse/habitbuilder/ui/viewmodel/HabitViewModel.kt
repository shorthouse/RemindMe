package dev.shorthouse.habitbuilder.ui.viewmodel

import androidx.lifecycle.*
import dev.shorthouse.habitbuilder.data.HabitDao
import dev.shorthouse.habitbuilder.model.Habit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HabitViewModel(private val habitDao: HabitDao
) : ViewModel() {
    val habits = habitDao.getHabits().asLiveData()

    fun getHabit(id: Long): LiveData<Habit> {
        return habitDao.getHabit(id).asLiveData()
    }

    fun addHabit(
        name: String,
        timestamp: Long,
        notes: String,
    ) {
        val habit = Habit(
            name = name,
            timestamp = timestamp,
            notes = notes,
        )

        viewModelScope.launch(Dispatchers.IO) {
            habitDao.insert(habit)
        }
    }

    fun updateHabit(
        id: Long,
        name: String,
        timestamp: Long,
        notes: String,
    ) {
        val habit = Habit(
            id = id,
            name = name,
            timestamp = timestamp,
            notes = notes,
        )

        viewModelScope.launch(Dispatchers.IO) {
            habitDao.update(habit)
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch(Dispatchers.IO) {
            habitDao.delete(habit)
        }
    }
}

class HabitViewModelFactory(
    private val habitDao: HabitDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HabitViewModel(habitDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}