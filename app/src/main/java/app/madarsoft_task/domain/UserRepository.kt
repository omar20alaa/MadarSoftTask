package app.madarsoft_task.domain

import app.madarsoft_task.data.database.User
import app.madarsoft_task.data.database.UserDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepository  @Inject constructor(private val userDao: UserDao) {

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers()
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }

    suspend fun clearAllUsers() {
        userDao.clearAll()
    }

}