package com.jtmnetwork.profile.core.usecase.repository

import com.jtmnetwork.profile.core.domain.dto.ProfileInfoDto
import com.jtmnetwork.profile.core.domain.entity.Profile
import com.jtmnetwork.profile.core.util.TestUtil
import junit.framework.TestCase.assertNotNull
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
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
    private val created = TestUtil.createProfile(id.toString())

    @Before
    fun setup() {
        profileRepository.deleteAll().block()
    }

    @Test
    fun save_shouldReturnProfile() {
        val returned = profileRepository.save(created).block()

        assertNotNull(returned)

        if (returned != null) {
            assertThat(returned.id).isEqualTo(id.toString())
        }
    }

    @Test
    fun findByInfoUsername_shouldReturnProfile() {
        profileRepository.save(created.updateProfile(ProfileInfoDto("test", 1980, 5, 21))).block()

        val returned = profileRepository.findByInfo_Username("test")

        StepVerifier.create(returned)
            .assertNext { assertThat(it.id).isEqualTo(created.id) }
            .verifyComplete()
    }

    @Test
    fun findByInfoUsername_shouldReturnEmpty() {
        val returned = profileRepository.findByInfo_Username("test")

        StepVerifier.create(returned)
            .expectNextCount(0)
            .verifyComplete()
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
    fun findById_shouldReturnEmpty() {
        val returned = profileRepository.findById(id.toString())

        StepVerifier.create(returned)
            .expectNextCount(0)
            .verifyComplete()
    }

    @Test
    fun findAll_shouldReturnProfiles() {
        profileRepository.deleteAll().block()

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
    fun findAll_shouldReturnEmpty() {
        profileRepository.deleteAll().block()

        val returned = profileRepository.findAll()

        StepVerifier.create(returned)
            .expectNextCount(0)
            .verifyComplete()
    }

    @Test
    fun deleteById_shouldDeleteProfile() {
        profileRepository.save(created).block()

        val exists = profileRepository.existsById(id.toString()).block()

        assertNotNull(exists)
        if (exists != null) assertTrue(exists)

        profileRepository.deleteById(id.toString()).block()

        val returned = profileRepository.existsById(id.toString()).block()

        assertNotNull(returned)
        if (returned != null) assertFalse(returned)
    }
}