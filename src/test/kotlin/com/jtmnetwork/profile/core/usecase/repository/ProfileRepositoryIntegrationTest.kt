package com.jtmnetwork.profile.core.usecase.repository

import com.jtmnetwork.profile.core.domain.entity.Profile
import junit.framework.TestCase.assertNotNull
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.junit4.SpringRunner
import reactor.test.StepVerifier
import java.util.*

@RunWith(SpringRunner::class)
@DataMongoTest
class ProfileRepositoryIntegrationTest {

    @Autowired
    lateinit var profileRepository: ProfileRepository

    private val id = UUID.randomUUID()
    private val created = Profile(id.toString())

    @Test
    fun save_shouldReturnProfile() {
        val returned = profileRepository.save(created).block()

        assertNotNull(returned)

        if (returned != null) {
            assertThat(returned.id).isEqualTo(id.toString())
        }
    }

    @Test
    fun findById_shouldReturnProfile() {
        profileRepository.save(created).block()

        val returned = profileRepository.findById(id.toString())

        StepVerifier.create(returned)
            .assertNext { assertThat(it.id).isEqualTo(id.toString()) }
            .verifyComplete()
    }

    @Test
    fun findAll_shouldReturnProfiles() {
        val secondId = UUID.randomUUID()

        profileRepository.save(created).block()
        profileRepository.save(Profile(secondId.toString())).block()

        val returned = profileRepository.findAll()

        StepVerifier.create(returned)
            .assertNext { assertThat(it.id).isEqualTo(id.toString()) }
            .assertNext { assertThat(it.id).isEqualTo(secondId.toString()) }
            .verifyComplete()
    }

    @Test
    fun deleteById_shouldReturnProfile() {
        profileRepository.save(created).block()

        val exists = profileRepository.existsById(id.toString()).block()

        assertNotNull(exists)
        assertTrue(exists)

        profileRepository.deleteById(id.toString()).block()

        val returned = profileRepository.existsById(id.toString()).block()

        assertNotNull(returned)
        assertFalse(returned)
    }
}