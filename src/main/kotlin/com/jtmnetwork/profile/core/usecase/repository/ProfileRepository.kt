package com.jtmnetwork.profile.core.usecase.repository

import com.jtmnetwork.profile.core.domain.entity.Profile
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ProfileRepository: ReactiveMongoRepository<Profile, String>