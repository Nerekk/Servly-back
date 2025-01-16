package org.example.servlyback.user

import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    fun getAllUsers(): List<User> = userRepository.findAll()

    fun getUserById(id: Long): User? = userRepository.findById(id).orElse(null)

    fun getUserByEmail(email: String): User? = userRepository.findByEmail(email)

    fun saveUser(user: User): User = userRepository.save(user)

    fun deleteUser(id: Long) = userRepository.deleteById(id)
}