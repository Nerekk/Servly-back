package org.example.servlyback.features._provider

import org.example.servlyback.entities.Provider
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProviderRepository : JpaRepository<Provider, Long> {
    fun findByUserUid(uid: String): Provider?
}